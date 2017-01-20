package com.y3.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.com.example.y3.model.User;
import com.example.y3.flashclick.R;

import java.util.List;

/**
 * Created by Yacoub on 2016-10-16.
 */

public class LeaderBoardAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<User> user;
    private Context context;

    public LeaderBoardAdapter(Activity activity, List<User> user) {
        this.activity = activity;
        this.user = user;
    }

    @Override
    public int getCount() {
        return user.size();
    }

    @Override
    public Object getItem(int location) {
        return user.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent ) {


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);
        context = convertView.getContext();


        TextView playerName = (TextView) convertView.findViewById(R.id.playerName);
        TextView playerPoints = (TextView) convertView.findViewById(R.id.playerPoints);

        // getting church data for the row
        User h = user.get(position);

        // name
        playerName.setText(h.getFullName());
        // points
        playerPoints.setText(Integer.toString(h.getPoints()));

        return convertView;
    }

}
