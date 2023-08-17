package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Spring Data Jpa是一个Hibernate提出的一个规范。Hibernate是Jpa的实现。Hibernate把所有实现需要的API拿出来做一个interface让其他也可以公用。
//这里因为用了JpaRepository，table自动创建了，不需要resource下面创建单独的table
//what is JpaRepository？extending paging/sorting repository which extends CRUDRepository
@Repository
public interface UserRepository extends JpaRepository<User, String> {    //这里的string是primary key
}


// By extending this interface, the UserRepository inherits various methods, including methods for
// saving, deleting, updating, and querying User objects. It also provides pagination and sorting capabilities.