package com.y3.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yacoub on 2016-09-05.
 */
public class User {
    private String email;
    private String fullName;
    private int points;
    private String ID;
    private String source;
    private String photo;

    public User() {}

    public User(String fullName, String email, int points, String ID, String photo, String source) {
        this.fullName = fullName;
        this.email = email;
        this.points = points;
        this.ID = ID;
        this.source = source;
        this.photo = photo;
    }
    public void setPhoto(String photo){
        this.photo = photo;
    }
    public String getPhoto(){
        return photo;
    }
    public String getEmail() {
        return email;
    }
    public String getFullName() {
        return fullName;
    }
    public int getPoints(){
        return points;
    }
    public String getID(){
        return ID;
    }
    public void setID(String id){
        this.ID = id;
    }
    public String getSource(){
        return source;
    }
    public void setSource(String source){
        this.source = source;
    }
    public void setPoints(int points){
        this.points = points;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("fullName", fullName);
        result.put("points", points);
        result.put("ID", ID);
        result.put("source", source);
        result.put("photo", photo);

        return result;
    }
}
