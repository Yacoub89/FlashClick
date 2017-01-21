package com.y3.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yacoub on 2016-09-09.
 */
public class Date {
    private  String year;
    private String month;
    private String day;
    private String hour;
    private String minuets;
    private String seconds;

    public Date(){}

    public Date(String year, String month, String day, String hour, String minuets, String seconds){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minuets = minuets;
        this.seconds = seconds;
    }

    public String getDay(){
        return day;
    }
    public void setDay(String day){
        this.day = day;
    }
    public String getHour(){
        return hour;
    }
    public void setHour(String hour){
        this.hour = hour;
    }
    public String getMinuets(){
        return minuets;
    }
    public void setMinuets(String minuets){
        this.minuets = minuets;
    }
    public String getSeconds(){
        return seconds;
    }
    public void setSeconds(String seconds){
        this.seconds = seconds;
    }
    public String getYear(){
        return year;
    }
    public String getMonth(){
        return month;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("day", day);
        result.put("hour", hour);
        result.put("minuets", minuets);
        result.put("seconds", seconds);

        return result;
    }
}
