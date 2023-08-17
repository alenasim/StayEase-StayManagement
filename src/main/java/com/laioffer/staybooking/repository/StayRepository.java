package com.laioffer.staybooking.repository;


import com.laioffer.staybooking.model.Stay;
import com.laioffer.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {
    // save() and delete() 都是自带的，不用单独写。
    List<Stay> findByHost(User user);        //为什么不直接用findById() 一定要加host? Id也是unique id呀。因为加完host，更安全。Id找的话，什么信息都能找到，但是用host找的话，一定要under这个host才能找到。more gatekeeper to keep information correct
    Stay findByIdAndHost(Long id, User host);
    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);
}