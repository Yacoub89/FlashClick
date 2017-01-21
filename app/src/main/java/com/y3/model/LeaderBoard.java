package com.y3.model;

import java.util.List;

/**
 * Created by Yacoub on 2016-10-16.
 */

public class LeaderBoard {
    List<User> userList;

    public LeaderBoard(){}

//    public LeaderBoard(User user){
//
//    }


    public List<User> getUserList() {
        return userList;
    }
    public void addToList(User user){
        userList.add(user);
    }
    public User getUser(int index){
        return userList.get(index);
    }
}
