package com.lorvent.project.telgujukebox.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorvent.project.telgujukebox.ListDetailFragment;
import com.lorvent.project.telgujukebox.R;
import com.lorvent.project.telgujukebox.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 4/8/17.
 */

public class JukeboxAdapter extends RecyclerView.Adapter<JukeboxAdapter.ViewHolder>{

    public ArrayList<Movie> movies;
    private Context mContext;
    ViewGroup mParent;
    String jukebox_type;
    public JukeboxAdapter(ArrayList<Movie> movies, Context mContext, String jukebox_type) {
        this.movies = movies;
        this.mContext = mContext;
        this.jukebox_type=jukebox_type;
    }

    @Override
    public JukeboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_movie,parent,false);
        mParent=parent;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(JukeboxAdapter.ViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.like.setText(movie.getLike());
        holder.view.setText(movie.getView());
        Picasso.with(mContext)
                .load(movie.getImage_url())
                .placeholder(R.drawable.movie_poster)
                .error(R.drawable.movie_poster)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title,like,view;
        ImageView poster;
        public ViewHolder(View itemView) {
            super(itemView);
            poster=(ImageView)itemView.findViewById(R.id.movie_image);
            title = (TextView) itemView.findViewById(R.id.title);
            like = (TextView) itemView.findViewById(R.id.like);
            view= (TextView) itemView.findViewById(R.id.view);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Fragment detailFragment = new ListDetailFragment();
            Bundle bundle=new Bundle();
            Movie movie=movies.get(getAdapterPosition());
            bundle.putString("video_id",movie.getVideo_id());
            bundle.putString("image_url",movie.getImage_url());
            bundle.putString("like",movie.getLike());
            bundle.putString("view",movie.getView());
            bundle.putString("jukebox_type",jukebox_type);
            bundle.putString("movie_name",movie.getTitle());
            detailFragment.setArguments(bundle);
            ((FragmentActivity)mParent.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    //.addSharedElement(holder.poster, "kittenImage")
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.frame, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
