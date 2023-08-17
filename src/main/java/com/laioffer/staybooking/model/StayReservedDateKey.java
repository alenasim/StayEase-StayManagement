package com.laioffer.staybooking.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/*use when you need a composite key， no need to create a separate primary key as stay id and date are already unique.
either stay_id or date cannot uniquely determine the specific stay, the key must be combination of stay_id and date to keep it unique. */
@Embeddable     // This annotation indicates that the field id is used as the embedded composite primary key for the table. The primary key is represented by the StayReservedDateKey class.
public class StayReservedDateKey implements Serializable {
    private Long stay_id;
    private LocalDate date;   // 跟time zone没有关系的 当地时间。 反义词：zonedDate就跟时区有关。

    public StayReservedDateKey() {
    }

    public StayReservedDateKey(Long stay_id, LocalDate date) {
        this.stay_id = stay_id;
        this.date = date;
    }

    public Long getStay_id() {
        return stay_id;
    }

    public void setStay_id(Long stay_id) {
        this.stay_id = stay_id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StayReservedDateKey that = (StayReservedDateKey) o;
        return Objects.equals(stay_id, that.stay_id) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stay_id, date);
    }
}