package com.example.yogajava;
import java.io.Serializable;
public class YogaJava implements Serializable {
    public long id;
    public String dayOfWeek;
    public String time;
    public int capacity;
    public int duration;
    public double price;
    public String classType;
    public String description;
    public YogaJava(long id, String dayOfWeek, String time, int capacity, int duration, double price, String classType, String description) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.description = description;
    }
    public YogaJava(String dayOfWeek, String time, int capacity, int duration, double price, String classType, String description) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.description = description;
    }
    @Override
    public String toString() {
        return "Day: " + this.dayOfWeek + ", " +
                "Time: " + this.time + ", " +
                "Type: " + this.classType + ", " +
                "Price: " + this.price + ", " +
                "Description: " + this.description;
    }
    public long getId() {
        return id;
    }
}