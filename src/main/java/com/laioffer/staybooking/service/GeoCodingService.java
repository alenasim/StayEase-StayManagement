package com.laioffer.staybooking.service;


import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.laioffer.staybooking.exception.GeoCodingException;
import com.laioffer.staybooking.exception.InvalidStayAddressException;
import com.laioffer.staybooking.model.Location;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;

// This class is responsible for interacting with the Google Maps Geocoding API to retrieve geographic coordinates (latitude and longitude) for a given address.
@Service
public class GeoCodingService {

    private final GeoApiContext context;

    public GeoCodingService(GeoApiContext context) {
        this.context = context;
    }

    public Location getLatLng(Long id, String address) {
        try {
            GeocodingResult result = GeocodingApi.geocode(context, address).await()[0];  // await()执行的意思【0】的意思是取match最高的第一个结果
            if (result.partialMatch) {      // 只做exact match。partial match的话还是invalid
                throw new InvalidStayAddressException("Failed to find stay address");
            }
            return new Location(id, new GeoPoint(result.geometry.location.lat, result.geometry.location.lng));   // return 一个 Location object。这个object就存在Elastic Search的database里
        } catch (IOException | ApiException | InterruptedException e) {
            e.printStackTrace();
            throw new GeoCodingException("Failed to encode stay address");
        }
    }
}
