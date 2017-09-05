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


public class List1Adapter extends RecyclerView.Adapter<List1Adapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    public  ArrayList<Movie> movies;
    private final RecyclerClickListener mListener;

    private Context mContext;

    public List1Adapter(Context context, ArrayList<Movie> movieArrayList, RecyclerClickListener listener)
    {    this.mContext=context;
        movies = movieArrayList;
        mListener=listener;

    }
    @Override
    public List1Adapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

     View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
     return new SimpleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder,final int position) {
        final Movie movie = movies.get(position);
        svHolder=holder;
        holder.title.setText(movie.getTitle());
        holder.like.setText(movie.getLike());
        holder.view.setText(movie.getView());
       // holder.poster.setImageResource(movie.getImage_id());
        Picasso.with(mContext)
                .load(movie.getImage_url())
                .placeholder(R.drawable.movie_poster)
                .error(R.drawable.movie_poster)
                .into(holder.poster);
        ViewCompat.setTransitionName(holder.poster, String.valueOf(position) + "_image");
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(holder,position);
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