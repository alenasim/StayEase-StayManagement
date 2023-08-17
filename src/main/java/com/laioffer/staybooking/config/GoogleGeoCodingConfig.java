package com.laioffer.staybooking.config;


import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
The GoogleGeoCodingConfig configuration class is responsible for creating a bean of type GeoApiContext using the provided Google API key.
This context can then be used throughout the application to interact with Google Maps Geocoding services, enabling the application
to convert between addresses and geographic coordinates. The API key is provided through Spring's property injection mechanism,
allowing for easy configuration and management.
*/
@Configuration
public class GoogleGeoCodingConfig {


    @Value("${geocoding.apikey}")   // 用@Value 把GeoCodingKey inject 进来
    private String apiKey;

    @Bean
    public GeoApiContext geoApiContext() {
        return new GeoApiContext.Builder().apiKey(apiKey).build();
    }
}

/*
Google Cloud API Key:
Google Cloud APIs, such as the Google Maps Geocoding API, require an API key for authentication and access control.
This API key is provided by Google Cloud when you create a project and enable the necessary APIs.

Application Properties:
Application properties are configuration settings that can be externalized from the code.
They allow you to configure various aspects of your application without modifying the code directly.
In the provided code, the application property geocoding.apikey is used to store the Google Cloud API key for geocoding.

Dependency Injection:
Dependency injection is a design pattern used in Spring and other frameworks. It allows you to provide dependencies
(such as objects or values) to a class rather than having the class create them itself. In the context of the provided code,
dependency injection is used to inject the Google Cloud API key into the GoogleGeoCodingConfig configuration class.

Configuration Class (GoogleGeoCodingConfig):
The GoogleGeoCodingConfig class is annotated with @Configuration, making it a Spring configuration class.
It defines a method geoApiContext() that is annotated with @Bean. This method creates and configures a bean of type GeoApiContext,
which is part of the Google Maps Java Client library.

@Value("${geocoding.apikey}") injects the value of the geocoding.apikey property from the application's configuration properties.
This means that the Google Cloud API key is injected into the apiKey field of the configuration class.
The geoApiContext() method builds a GeoApiContext instance using the injected API key. This context is used to interact with
Google Maps Geocoding services.

Interaction Flow:
When the application starts, Spring reads the application properties to find the value of geocoding.apikey.
The GoogleGeoCodingConfig configuration class is processed by Spring, and the apiKey field is injected with the API key value from the properties.
When the geoApiContext() bean method is called, it creates a GeoApiContext instance with the provided API key.
Other parts of the application can use dependency injection to obtain the configured GeoApiContext bean and use it to interact with
Google Maps Geocoding services.

In summary, the provided GoogleGeoCodingConfig configuration class acts as a bridge between the Google Cloud API key,
application properties, and the application's code. It uses dependency injection to inject the API key from application properties
into a GeoApiContext bean, which can then be used across the application to interact with Google Maps Geocoding services.
This approach keeps sensitive configuration like API keys separate from the application code and promotes modularity and reusability.

*/