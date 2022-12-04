package com.lab.model;

import androidx.room.Entity;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@Entity(tableName="meteors")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meteor {

    @JsonProperty
    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @JsonProperty
    @ColumnInfo(name = "name")
    private String name;

    @JsonProperty
    @ColumnInfo(name = "recclass")
    private String recclass;

    @JsonProperty
    @ColumnInfo(name = "mass")
    private float mass;

    @JsonProperty
    @ColumnInfo(name = "fall")
    private String fall;

    @JsonProperty
    @ColumnInfo(name = "year")
    private String year;

    @Override
    public String toString() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecclass() {
        return recclass;
    }

    public void setRecclass(String recclass) {
        this.recclass = recclass;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public String getFall() {
        return fall;
    }

    public void setFall(String fall) {
        this.fall = fall;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meteor meteor = (Meteor) o;
        return id == meteor.id && Float.compare(meteor.mass, mass) == 0 && Objects.equals(name, meteor.name) && Objects.equals(recclass, meteor.recclass) && Objects.equals(fall, meteor.fall) && Objects.equals(year, meteor.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, recclass, mass, fall, year);
    }
}
