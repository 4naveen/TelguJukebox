package com.lorvent.project.telgujukebox.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.lorvent.project.telgujukebox.model.Movie;
import com.lorvent.project.telgujukebox.R;
import com.lorvent.project.telgujukebox.RecyclerClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    public  ArrayList<Movie> movies;
    private final RecyclerClickListener mListener;

    private Context mContext;

    public ListAdapter(Context context, ArrayList<Movie> movieArrayList,RecyclerClickListener listener)
    {    this.mContext=context;
        movies = movieArrayList;
        mListener=listener;

    }
    @Override
    public ListAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

     View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
     return new SimpleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ListAdapter.SimpleViewHolder viewHolder, final int position) {
        final Movie movie = movies.get(position);
        svHolder=viewHolder;
        viewHolder.title.setText(movie.getTitle());
        viewHolder.like.setText(movie.getLike());
        viewHolder.view.setText(movie.getView());
        Picasso.with(mContext)
                .load(movie.getImage_url())
                .placeholder(R.drawable.movie_poster)
                .error(R.drawable.movie_poster)
                .into(viewHolder.poster);
       // viewHolder.poster.setImageResource(movie.getImage_id());
        ViewCompat.setTransitionName(viewHolder.poster, String.valueOf(position) + "_image");
        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(viewHolder,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }


     public class SimpleViewHolder extends RecyclerView.ViewHolder{
        TextView title,like,view;
        ImageView poster;
        SimpleViewHolder(View itemView) {
            super(itemView);
            poster=(ImageView)itemView.findViewById(R.id.poster_image);
            title = (TextView) itemView.findViewById(R.id.title);
            like = (TextView) itemView.findViewById(R.id.like);
            view= (TextView) itemView.findViewById(R.id.view);


        }
     }


}