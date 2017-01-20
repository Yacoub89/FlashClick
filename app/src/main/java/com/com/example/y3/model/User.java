package com.com.example.y3.model;

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
    private String fbID;

    public User() {}

    public User(String fullName, String email, int points, String fbID) {
        this.fullName = fullName;
        this.email = email;
        this.points = points;
        this.fbID = fbID;
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
    public String getFbID(){
        return fbID;
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
        result.put("fbID", fbID);

        return result;
    }
}
