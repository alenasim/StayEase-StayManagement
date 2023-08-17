package com.laioffer.staybooking.repository;


import com.laioffer.staybooking.model.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository {
}


/*
@Repository Annotation:
The @Repository annotation is often used in Spring to indicate that a class is a repository, which is a type of
Spring component responsible for handling database interactions. Repositories typically provide methods for querying
and manipulating data in a database, making it easier to work with database operations in a more abstract and convenient manner.

LocationRepository Interface:
This interface is meant to define methods for interacting with data stored in Elasticsearch, specifically for the Location entity.
The methods defined here will allow you to perform various operations on the Elasticsearch index associated with the Location class.

ElasticsearchRepository<Location, Long>:
This is an extension of the ElasticsearchRepository interface provided by Spring Data Elasticsearch. It's a generic interface
that helps with common Elasticsearch operations. The ElasticsearchRepository interface has methods for CRUD (Create, Read, Update,
Delete) operations and other common queries on Elasticsearch indexes.

Location: This is the entity class type that the repository is dealing with (in this case, the Location class).
Long: This is the type of the identifier used by the entity class (id field in the Location class is of type Long).

CustomLocationRepository:   --> 需要extend去实现search
This seems to be a custom interface or repository that extends beyond the basic operations provided by ElasticsearchRepository.
It's not part of the standard Spring Data Elasticsearch naming conventions, so it's likely defined elsewhere in your codebase.
By extending this interface, the LocationRepository gains access to additional custom methods that might be specific to your application's requirements.
* */