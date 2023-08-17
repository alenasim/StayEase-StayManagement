package com.laioffer.staybooking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;


/*part of a project that uses the Spring Data Elasticsearch library to interact with an Elasticsearch database.
Elasticsearch is a search and analytics engine often used for indexing and querying large volumes of data.
用elastic search因为他有geo indexing很方便的去index location。

Below code imports various annotations and classes from the Spring Data Elasticsearch library.
These annotations are used to provide metadata about how the class fields should be mapped to the Elasticsearch index.
*/

/* @Document(indexName = "loc") specifies that instances of this class should be stored in an Elasticsearch index named "loc".
In Elasticsearch, an index is similar to a table in SQL databases.
白话说，你创建了一个搜索空间，然后把所有的内容放在搜索空间。跟SQL里面的table很像。这里的table就是index。index里面的每一个搜索空间里面的element，
就是一个document （就像db里面的一行一行的数据，但是没有像db一样一行一行的列出来因为他是noSQL）

*/
@Document(indexName = "loc")
public class Location {

    @Id                            // id field is the identifier for the document in the Elasticsearch index.
    @Field(type = FieldType.Long)  // the id field should be stored as a Long type in the Elasticsearch index
    private Long id;

    @GeoPointField
    private GeoPoint geoPoint;   // Geopoint class 里面有两个field - latitude and longitude

    public Location(Long id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public Long getId() {
        return id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}

