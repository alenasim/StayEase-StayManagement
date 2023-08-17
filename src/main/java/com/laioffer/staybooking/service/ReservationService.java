package com.laioffer.staybooking.service;

import com.laioffer.staybooking.exception.ReservationCollisionException;
import com.laioffer.staybooking.exception.ReservationNotFoundException;
import com.laioffer.staybooking.model.*;
import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StayReservationDateRepository stayReservationDateRepository;

    @Autowired
    // @Autowired automatically injects the dependencies (reservationRepository and stayReservationDateRepository) when the service is instantiated. 你让我自动跟他联系。但是当前代码里的field都是final，即使不写也会自动给我们连接
    public ReservationService(ReservationRepository reservationRepository, StayReservationDateRepository stayReservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    // This method retrieves a list of reservations associated with a guest's username using the reservationRepository.
    public List<Reservation> listByGuest(String username) {
         return reservationRepository.findByGuest_Username(username);
        // return reservationRepository.findByGuest(new User.Builder().setUsername(username).build()); => 不好。因为new出来的user不是全面的。User除了username意外还有password和其他field。这个user其他属性都没有填好。目前代码只是创建完马上调用username，但是以后别人可能拿着这个user去做其他操作的话，很容易mess up。不好的practice
    }

    // This method retrieves a list of reservations associated with a particular stay ID using the reservationRepository.
    public List<Reservation> listByStay(Long stayId) {
         return reservationRepository.findByStay_Id(stayId);
//        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }

    // This method is used to add a new reservation. It checks for any collision with existing reservations using the
    // stayReservationDateRepository, and if no collisions are found, it saves the reservation and updates the stay reservation dates accordingly.
    @Transactional
    public void add(Reservation reservation) throws ReservationCollisionException {
        Set<Long> stayIds = stayReservationDateRepository.findByIdInAndDateBetween(      // 3个arguments传进去
                List.of(reservation.getStay().getId()),        // id construct成一个list 因为在StayReservationDateRepository里面stayIds是一个list
                reservation.getCheckinDate(),            // reservation的check in 和check out也要传进去
                reservation.getCheckoutDate().minusDays(1)    // minusDays意思就是说check in和 check out可以同一天
        );
        if (!stayIds.isEmpty()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }

        List<StayReservedDate> reservedDates = new ArrayList<>();
        LocalDate start = reservation.getCheckinDate();
        LocalDate end = reservation.getCheckoutDate();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            StayReservedDateKey id = new StayReservedDateKey(reservation.getStay().getId(), date);    // stay reserved date is a composite key, therefore add two factors needed to build a key first
            StayReservedDate reservedDate = new StayReservedDate(id, reservation.getStay());          // and add the key to the list
            reservedDates.add(reservedDate);
        }
        stayReservationDateRepository.saveAll(reservedDates);
        reservationRepository.save(reservation);
    }

    // This method is used to delete a reservation. It retrieves the reservation using the reservationRepository and username, then deletes the reservation and updates the stay reservation dates
    @Transactional
    public void delete(Long reservationId, String username) {
         Reservation reservation = reservationRepository.findByIdAndGuest_Username(reservationId, username);
//        Reservation reservation = reservationRepository.findByIdAndGuest(reservationId, new User.Builder().setUsername(username).build());
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation is not available");
        }
        LocalDate start = reservation.getCheckinDate();
        LocalDate end = reservation.getCheckoutDate();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            stayReservationDateRepository.deleteById(new StayReservedDateKey(reservation.getStay().getId(), date));
        }
        reservationRepository.deleteById(reservationId);
    }
}
