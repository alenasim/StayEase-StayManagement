package com.laioffer.staybooking.model;

import javax.persistence.*;

/*an entity class that represents the relationship between Stay and ReservedDate entities using a join table named stay_reserved_date. */
@Entity             // This annotation marks the class as a JPA entity, indicating that it corresponds to a table in the database.
@Table(name = "stay_reserved_date")
public class StayReservedDate {

    @EmbeddedId    //This annotation indicates that the field id is used as the embedded composite primary key for the table. The primary key is represented by the StayReservedDateKey class.
    private StayReservedDateKey id;
    @MapsId("stay_id")        // foreign key - This annotation specifies that the stay_id field in the composite key corresponds to the stay field in the entity. This establishes a mapping between the primary key and the associated Stay entity.
    @ManyToOne                // This annotation indicates a many-to-one relationship between StayReservedDate and Stay entities. This implies that multiple instances of StayReservedDate can be associated with one Stay entity.
    private Stay stay;

    public StayReservedDate() {
    }

    public StayReservedDate(StayReservedDateKey id, Stay stay) {
        this.id = id;
        this.stay = stay;
    }

    public StayReservedDateKey getId() {
        return id;
    }

    public Stay getStay() {
        return stay;
    }
}

