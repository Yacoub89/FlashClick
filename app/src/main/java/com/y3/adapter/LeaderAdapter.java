package com.y3.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.com.example.y3.model.User;
import com.example.y3.flashclick.MainActivity;
import com.example.y3.flashclick.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by abdy on 2016-10-19.
 */

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.MyViewHolder> {

    private List<User> user;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, rank;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            //title = (TextView) view.findViewById(R.id.person_name);
           // count = (TextView) view.findViewById(R.id.person_points);
           // rank = (TextView) view.findViewById(R.id.person_rank);
            thumbnail = (ImageView) view.findViewById(R.id.person_photo);

        }
    }

    public LeaderAdapter(Context mContext, List<User> userList) {
        this.context = mContext;
        this.user = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_user_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        User userTemp = user.get(position);
      //  holder.title.setText(userTemp.getFullName());
      //  holder.rank.setText(Integer.toString(position  + 1));
        //holder.count.setText(Integer.toString(userTemp.getPoints()));
        GetImage(userTemp.getFbID(), holder);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public void GetImage(String fbId, final MyViewHolder holder){
        String url = "https://graph.facebook.com/" + fbId + "/picture?width=200&height=150";
        new LeaderAdapter.ImageLoadTask(url, holder.thumbnail).execute();
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}

