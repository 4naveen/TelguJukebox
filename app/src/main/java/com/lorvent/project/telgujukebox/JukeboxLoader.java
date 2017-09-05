package com.lorvent.project.telgujukebox;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.lorvent.project.telgujukebox.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 7/7/17.
 */

public class JukeboxLoader extends AsyncTaskLoader{
    private String mUrl;
    ArrayList<Movie> jukeboxArrayList;
    Context context;
    Movie movie;
    public JukeboxLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
        this.context=context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
    @Override
    public ArrayList<Movie> loadInBackground() {
        String response;
        URL url = null;
        HttpURLConnection connection = null;
        jukeboxArrayList=new ArrayList<>();

        try {
            url=new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String temp;
            while ((temp=br.readLine())!=null)
            {
                buffer.append(temp);
            }
            response=buffer.toString();
            System.out.println("response"+response);
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("items");
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject object=jsonArray.getJSONObject(i);
                JSONObject contentDetails=object.getJSONObject("contentDetails");
                String video_id=contentDetails.getString("videoId");
                JSONObject sub_object=object.getJSONObject("snippet");
                JSONObject thumbnails=sub_object.getJSONObject("thumbnails");
                JSONObject image_url=thumbnails.getJSONObject("medium");
                movie =new Movie();
                movie.setTitle(sub_object.getString("title"));
                movie.setVideo_id(video_id);
                movie.setImage_url(image_url.getString("url"));
                URL url1=new URL("https://www.googleapis.com/youtube/v3/videos?part=snippet%2Cstatistics&id="+video_id+"&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA");
                connection = (HttpURLConnection) url1.openConnection();
                BufferedReader br1 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer1 = new StringBuilder();
                String temp1;
                while ((temp1=br1.readLine())!=null)
                {
                    buffer1.append(temp1);
                }
                String response1=buffer1.toString();
                System.out.println("response1"+response1);

                JSONObject jsonObject1=new JSONObject(response1);
                JSONArray jsonArray1=jsonObject1.getJSONArray("items");
                for (int j=0;j<jsonArray1.length();j++)
                {
                    JSONObject object1=jsonArray1.getJSONObject(j);
                    JSONObject statistics=object1.getJSONObject("statistics");
                    movie.setLike(statistics.getString("likeCount"));
                    movie.setView(statistics.getString("viewCount"));
                    jukeboxArrayList.add(movie);
                }
            }
        } catch (IOException e) {
            //handle exception
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jukeboxArrayList;
    }


}
