package com.y3.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.y3.flashclick.R;
import com.y3.model.Award;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * Created by abdy on 2016-10-20.
 */

public class AwardAdapter extends RecyclerView.Adapter<AwardAdapter.MyViewHolder> {

    private List<Award> awardList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.award_desc);
            thumbnail = (ImageView) view.findViewById(R.id.award_photo);

        }
    }

    public AwardAdapter(Context mContext, List<Award> awardList) {
        this.context = mContext;
        this.awardList = awardList;
    }

    @Override
    public AwardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.award_content, parent, false);

        return new AwardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AwardAdapter.MyViewHolder holder, int position) {
        Award award = awardList.get(position);
        holder.title.setText(award.getImgDescription());

        //holder.thumbnail
       // SharedPreferences userDetail = context.getSharedPreferences("userDetail", MODE_PRIVATE);

        GetImage(award.getImgUrl(), holder);

    }

    @Override
    public int getItemCount() {
        return awardList.size();
    }

    public void GetImage(String url, final AwardAdapter.MyViewHolder holder){
        //String url = "https://graph.facebook.com/" + fbId + "/picture?width=200&height=150";
        new AwardAdapter.ImageLoadTask(url, holder.thumbnail).execute();
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
