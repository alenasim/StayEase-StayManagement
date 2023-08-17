package com.laioffer.staybooking.service;


import com.laioffer.staybooking.model.Stay;
import com.laioffer.staybooking.repository.LocationRepository;
import com.laioffer.staybooking.repository.StayRepository;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
This class that provides methods for searching and filtering stays based on various criteria like guest number, check-in and
check-out dates, and location. The service interacts with different repositories to retrieve and filter stay information.
*/
@Service
public class SearchService {
    private final StayRepository stayRepository;
    private final StayReservationDateRepository stayReservationDateRepository;
    private final LocationRepository locationRepository;


    public SearchService(StayRepository stayRepository, StayReservationDateRepository stayReservationDateRepository, LocationRepository locationRepository) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
    }

    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat, double lon, String distance) {
        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        if (stayIds == null || stayIds.isEmpty()) {
            return Collections.emptyList();      // return了一个大家都用的empty list。这个用法是memory efficient， 而且外面得到结果往里面加的话，throw exception，因为这个api是immutable的只能读不能改
        }
        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(stayIds, checkinDate, checkoutDate.minusDays(1));
        List<Long> filteredStayIds = stayIds.stream()                   // 把stayIds变成一个stream
                .filter(stayId -> !reservedStayIds.contains(stayId))    // 先filter一遍，怎么filter呢？必须不能在stayId里面出现过
                .collect(Collectors.toList());                          //
        /*上一行的frequent API也可以写成，
        * List<Long> filteredStayIds = new ArrayList<>();
        * for (Long stayId : stayIds) {
        *   if (!reservationStayIds.contains(stayId) {
        *       filteredStayIds.add(stayId);
        *   }
        * }
        * */
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }
}
