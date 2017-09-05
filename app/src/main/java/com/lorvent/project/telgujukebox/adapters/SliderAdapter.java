package com.lorvent.project.telgujukebox.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lorvent.project.telgujukebox.DetailFragment;
import com.lorvent.project.telgujukebox.model.Movie;
import com.lorvent.project.telgujukebox.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 21/7/17.
 */

public class SliderAdapter extends PagerAdapter {

    private ArrayList<String> images;
    private ArrayList<Movie> movies;
    private LayoutInflater inflater;
    private Context context;

    public SliderAdapter(ArrayList<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
 /*   public SliderAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }*/
    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        Movie movie=movies.get(position);
        final String video_id=movie.getVideo_id();
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        Picasso.with(context)
                //.load(images.get(position))
                .load(movie.getImage_url())
                .placeholder(R.drawable.movie_poster)
                .error(R.drawable.movie_poster)
                .into(myImage);
       // myImage.setImageResource(images.get(position));
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment detailFragment = new DetailFragment();
                Bundle bundle=new Bundle();
                bundle.putString("video_id",video_id);
                bundle.putParcelableArrayList("most_popular",movies);
                detailFragment.setArguments(bundle);
                ((AppCompatActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        //.addSharedElement(holder.poster, "kittenImage")
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.frame, detailFragment)
                        .addToBackStack(null)
                        .commit();
    /*            Intent intent = new Intent(context, VideoViewActivity.class);
                context.startActivity(intent);*/
            }
        });
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
