package com.laioffer.staybooking.service;


import com.laioffer.staybooking.exception.StayDeleteException;
import com.laioffer.staybooking.exception.StayNotExistException;
import com.laioffer.staybooking.model.*;
import com.laioffer.staybooking.repository.LocationRepository;
import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*this StayService class acts as an intermediary between the controller and the repository,
providing methods to perform various operations related to stays, including listing, adding, and deleting stays.
The class handles database interactions, business logic, and exception handling related to these operations.*/
@Service   // This annotation indicates that the class is a Spring service component. Services are used to encapsulate business logic and operations in a structured manner.
public class StayService {

    private final ImageStorageService imageStorageService;
    private final StayRepository stayRepository;
    private final GeoCodingService geoCodingService;
    private final LocationRepository locationRepository;
    private final ReservationRepository reservationRepository;

    public StayService(ImageStorageService imageStorageService, StayRepository stayRepository, GeoCodingService geoCodingService, LocationRepository locationRepository, ReservationRepository reservationRepository) {
        this.imageStorageService = imageStorageService;
        this.stayRepository = stayRepository;
        this.geoCodingService = geoCodingService;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
    }

    /*This method retrieves a list of stays associated with a specific user (host) based on the provided username.
    It uses the injected stayRepository to query the database. The method is useful for fetching stays that belong
    to a particular user for displaying or processing purposes.
    */
    public List<Stay> listByUser(String username) {
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
        // this method is querying the repository to find stays where the host matches the specified user.
        // returns the result of a query performed on a stayRepository.
    }

    /*This method retrieves a specific stay by its ID and verifies that the stay is associated with the provided host username.
    If the stay does not exist or is not associated with the host, a StayNotExistException is thrown.*/
    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    /*This method adds a new stay to the system. It accepts a Stay object and an array of MultipartFile images.
    It processes the images, saves them using the imageStorageService, and associates them with the stay as StayImage objects.
    It then saves the stay using the stayRepository. Additionally, it uses the geoCodingService to retrieve and
    save the location coordinates associated with the stay's address.*/
    // 为什么不把image直接存在database，而是先存在bucket，再存到database？SQL不适合存binary或者大数据。
    // 因为search不会很efficient，performance降低。假如一个column是图片的话，占很多内存。 任何不需要indexing的内容，
    // 我们直接用Amazon S3或者google cloud storage来存。
    @Transactional
    public void add(Stay stay, MultipartFile[] images) {
        List<StayImage> stayImages = Arrays.stream(images)   // 把list先变成Java的一个Stream
                .filter(image -> !image.isEmpty())    // corner case - image空了，就filter掉。什么都不做。
                .parallel()                          // 创建多个线程，同时上传
                .map(imageStorageService::save)      // 每个传进来的image，每次都存一次 can also be written as .map(image -> imageStorageService.save(image));
                .map(mediaLink -> new StayImage(mediaLink, stay))    // 每次存完每个file就有media link(以为url是field)
                .collect(Collectors.toList());  // 把media link放到一个list里面
                /* 跟上面的lambda写法是一模一样的意义。唯一不一样的就是，下面的for loop没带parallel()
                List<String> mediaLinks2 = new ArrayList<>();
                for (MultipartFile image : images) {
                    mediaLinks2.add(imageStorageService.save(image));
                }
                但是上面的frequent API写法读起来很顺，很容易理解
                */
        // 存下来的时候需要 - 存照片，stay存了，把geolocation也要存。需要加@Transactional因为有好多个写的操作。
        stay.setImages(stayImages);
        stayRepository.save(stay);

        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress()); //
        locationRepository.save(location);   // 连到elastic search的index里面
    }

    /*This method is used to delete a stay. It verifies that the stay with the provided ID exists and is associated with
    the provided host username. It also checks if there are active reservations associated with the stay.
    If there are, a StayDeleteException is thrown. If all checks pass, the stay is deleted using the stayRepository.*/
    public void delete(Long stayId, String username) throws StayNotExistException, StayDeleteException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }


        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && !reservations.isEmpty()) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }
        stayRepository.deleteById(stayId);
    }
}

