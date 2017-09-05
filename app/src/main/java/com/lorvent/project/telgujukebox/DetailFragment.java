package com.lorvent.project.telgujukebox;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.lorvent.project.telgujukebox.adapters.MovieReviewAdapter;
import com.lorvent.project.telgujukebox.data.FavouriteContract;
import com.lorvent.project.telgujukebox.model.Movie;
import com.lorvent.project.telgujukebox.model.Review;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView mMovieReviewRecyclerView;
    MovieReviewAdapter mMovieReviewAdapter;
    ArrayList<Review> reviewArrayList;
    String video_id,image_url,like,view_count,title;
    ArrayList<Movie> movieArrayList;
    ImageView movie_poster, small_movie_poster;
    TextView release_date, duration, movie_title;
    SimpleRatingBar simpleRatingBar;
    ImageView favoriteButton;
    private boolean favorite;
    private ExpandableTextView exp_overview;
    String description;
    public DetailFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        reviewArrayList = new ArrayList<>();
        mMovieReviewRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMovieReviews);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_movie_detail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        movie_poster = (ImageView) view.findViewById(R.id.image);
        small_movie_poster = (ImageView) view.findViewById(R.id.small_movie_poster);
        exp_overview = (ExpandableTextView) view.findViewById(R.id.expand_text_overview);
        release_date = (TextView) view.findViewById(R.id.release_date);
        duration = (TextView) view.findViewById(R.id.duration);
        //  movie_title=(TextView)view.findViewById(R.id.movie_title);
        collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_movie_detail);
        //favorite = false;
        favoriteButton = (ImageView) view.findViewById(R.id.favorite);
        movieArrayList = getArguments().getParcelableArrayList("most_popular");
        video_id = getArguments().getString("video_id");
        image_url=getArguments().getString("image_url");
        like=getArguments().getString("like");
        view_count=getArguments().getString("view");
        title=getArguments().getString("movie_name");
        isMovieFavorite(String.valueOf(video_id));
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(getActivity(), CustomVideoViewActivity.class);
                Intent intent = new Intent(getActivity(), VideoViewActivity.class);
                 intent.putExtra("video_id",video_id);
                getActivity().startActivity(intent);
            }
        });
        Log.i("video_id", video_id);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorite) {
                    favoriteButton.setImageResource(R.drawable.unstarred);
                    deleteFromDB(String.valueOf(video_id));
                    favorite = false;
                } else {
                    favoriteButton.setImageResource(R.drawable.starred);
                    saveToDB();
                    favorite = true;
                }
            }
        });
        new GetVideoDetails().execute();
        new GetComments().execute();

        simpleRatingBar = (SimpleRatingBar) view.findViewById(R.id.rating);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //getFragmentManager().popBackStackImmediate();
                        //getActivity().getSupportFragmentManager().popBackStackImmediate();
                        Fragment fragment1 = new MainFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("most_popular", movieArrayList);
                        fragment1.setArguments(bundle);
                        FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                        trans1.replace(R.id.frame, fragment1);
                        trans1.addToBackStack(null);
                        trans1.commit();
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //getFragmentManager().popBackStackImmediate();

            Fragment fragment1 = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("most_popular", movieArrayList);
            fragment1.setArguments(bundle);
            FragmentTransaction trans1 = getFragmentManager().beginTransaction();
            trans1.replace(R.id.frame, fragment1);
            trans1.addToBackStack(null);
            trans1.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetVideoDetails extends AsyncTask<String, Void, String> {
        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
               // url = new URL("https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2cstatistics&id=" + video_id + "&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA");

                url=new URL(Config.VIDEO_URL+video_id);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    buffer.append(temp);
                }
                response = buffer.toString();
                System.out.println("response" + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {

            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = new JSONObject(response);
                JSONArray jsonArray = jsonObject1.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONObject sub_object = object.getJSONObject("snippet");
                    JSONObject thumbnails = sub_object.getJSONObject("thumbnails");
                    JSONObject image_url = thumbnails.getJSONObject("high");
                    JSONObject small_image_url = thumbnails.getJSONObject("medium");
                    JSONObject statistics = object.getJSONObject("statistics");
                    int like = Integer.parseInt(statistics.getString("likeCount"));
                    int dislike = Integer.parseInt(statistics.getString("dislikeCount"));
                    // System.out.println(like+"  "+dislike);
                    // System.out.println((float) dislike/like);

                    Picasso.with(getActivity())
                            .load(image_url.getString("url"))
                            .placeholder(R.drawable.movie_poster)
                            .error(R.drawable.movie_poster)
                            .into(movie_poster);
                    sub_object.getString("description");
                    Picasso.with(getActivity())
                            .load(small_image_url.getString("url"))
                            .placeholder(R.drawable.movie_poster)
                            .error(R.drawable.movie_poster)
                            .into(small_movie_poster);
                    description=sub_object.getString("description");
                    exp_overview.setText(sub_object.getString("description"));
                    JSONObject contentDetails = object.getJSONObject("contentDetails");
                    collapsingToolbar.setTitle(sub_object.getString("title"));

                    simpleRatingBar.setRating((float) ((((float) dislike / like) * 100) / 2));
                    String publish = sub_object.getString("publishedAt").substring(0, 10);
                    String[] dates = publish.split("-");
                    SimpleDateFormat dateformat = new SimpleDateFormat("MMMM d,y", Locale.getDefault());
                    Date date = new Date(Integer.parseInt(dates[0]) - 1900, Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
                    release_date.setText(dateformat.format(date));
                    duration.setText(contentDetails.getString("duration").substring(2, 4) + " min");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetComments extends AsyncTask<String, Void, String> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
               // url = new URL("https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=" + video_id + "&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA");
                url=new URL(Config.COMMENT_URL+video_id);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    buffer.append(temp);
                }
                response = buffer.toString();
                System.out.println("response" + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {

            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = new JSONObject(response);
                JSONArray jsonArray = jsonObject1.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONObject sub_object = object.getJSONObject("snippet");
                    JSONObject toplevelComment = sub_object.getJSONObject("topLevelComment");
                    JSONObject snippet = toplevelComment.getJSONObject("snippet");
                    String comment = snippet.getString("textDisplay");
                    String author = snippet.getString("authorDisplayName");
                    Review review = new Review();
                    review.setmContent(comment);
                    review.setmAuthor(author);
                    reviewArrayList.add(review);
                }
                mMovieReviewAdapter = new MovieReviewAdapter(getActivity(), reviewArrayList);
                mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter);
                LinearLayoutManager layoutManagerMovieReview = new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.VERTICAL, false);
                mMovieReviewRecyclerView.setLayoutManager(layoutManagerMovieReview);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToDB() {
        ContentValues values = new ContentValues();
        values.put(FavouriteContract.FavoriteEntry.COLUMN_VIDEO_ID,video_id);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_POSTER_PATH,image_url);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_LIKE_COUNT,like);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_VIEW_COUNT,view_count);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_MOVIE_NAME,title);

        getActivity().getContentResolver().insert(FavouriteContract.FavoriteEntry.CONTENT_URI, values);

    }
    private void deleteFromDB(String video_id) {

        getActivity().getContentResolver().delete(FavouriteContract.FavoriteEntry.buildFavouriteUri(2),null, new String[]{video_id});

    }
    private void isMovieFavorite(String video_id) {
         ArrayList<String>videoIdList;
        Cursor c = getActivity().getContentResolver().query(FavouriteContract.FavoriteEntry.buildFavouriteUri(2),null,null,new String[]{video_id},null);
        System.out.println("cursor_size  "+c.getCount());
        if (c.getCount()>0) {
            favorite=true;
            Log.i("available", String.valueOf(favorite));
            favoriteButton.setImageResource(R.drawable.starred);
        } else {
            favorite=false;
            Log.i("available", String.valueOf(favorite));
            favoriteButton.setImageResource(R.drawable.unstarred);
        }
    }


}
