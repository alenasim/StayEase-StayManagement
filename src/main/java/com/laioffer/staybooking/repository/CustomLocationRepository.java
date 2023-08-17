package com.laioffer.staybooking.repository;

import java.util.List;

// 创建这个CustomLocationRepository就是为了用search api。那search API怎么implement？需要自己做。需要自己写一个class去implement这个method
public interface CustomLocationRepository {

    List<Long> searchByDistance(double lat, double lon, String distance);
}
