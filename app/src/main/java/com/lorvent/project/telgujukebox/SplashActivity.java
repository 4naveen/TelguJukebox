package com.lorvent.project.telgujukebox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lorvent.project.telgujukebox.model.Movie;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>{
    ImageView image;
    ArrayList<Movie>movieArrayList;
    FrameLayout layout;
    private static final int LOADER_ID = 111;
    NewtonCradleLoading newtonCradleLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        image = (ImageView) findViewById(R.id.imageView);
        changeStatusBarColor();
        newtonCradleLoading = (NewtonCradleLoading)findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();
        layout=(FrameLayout)findViewById(R.id.activity_splash);
        movieArrayList=new ArrayList<>();
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getSupportLoaderManager().initLoader(LOADER_ID, null,SplashActivity.this).forceLoad();
        }
        else {
            final Snackbar snackbar = Snackbar.make(layout, "please check your internet connectivity!", Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            v.setMinimumWidth(1000);
            TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
       // String url="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=PLUsLbqK_M40zeH9pVOHU_pflHRh2uwRfS&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
        String  url="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&playlistId=PLUsLbqK_M40zeH9pVOHU_pflHRh2uwRfS&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";

        return new JukeboxLoader(this,url);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {

        if (data.isEmpty()) {
            final Snackbar snackbar = Snackbar.make(layout, "we are getting some server issue!Please wait for sometime", Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            v.setMinimumWidth(1000);
            TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
            return;
        }
        else {
            movieArrayList=data;
            Intent i = new Intent(getBaseContext(),MainActivity.class);
            i.putParcelableArrayListExtra("most_popular",movieArrayList);
            startActivity(i);
            finish();
        }
        newtonCradleLoading.stop();

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(40,111,173));
        }
    }
}
