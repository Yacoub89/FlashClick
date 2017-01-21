package com.y3.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yacoub on 2016-10-10.
 */

public class Award {
    private String imgUrl;
    private String imgDescription;

    public Award(){}

    public  Award(String imgUrl, String imgDescription){
        this.imgUrl = imgUrl;
        this.imgDescription = imgDescription;
    }
    public String getImgUrl(){
        return imgUrl;
    }
    public String getImgDescription(){
        return imgDescription;
    }
    public void setImgUrl(String imgUrl){
        this.imgUrl =imgUrl;
    }
    public void setImgDescription(String imgDescription){
        this.imgDescription = imgDescription;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("imgUrl", imgUrl);
        result.put("imgDescription", imgDescription);
        return result;
    }
}
