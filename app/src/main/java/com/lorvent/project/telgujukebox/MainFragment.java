package com.lorvent.project.telgujukebox;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.lorvent.project.telgujukebox.adapters.List1Adapter;
import com.lorvent.project.telgujukebox.adapters.List2Adapter;
import com.lorvent.project.telgujukebox.adapters.ListAdapter;
import com.lorvent.project.telgujukebox.adapters.SliderAdapter;
import com.lorvent.project.telgujukebox.data.FavouriteContract;
import com.lorvent.project.telgujukebox.model.Movie;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements RecyclerClickListener,LoaderManager.LoaderCallbacks<ArrayList<Movie>>{
    MaterialSearchView searchView;
    private static ViewPager mPager;
    private static int currentPage = 0;

// private static final Integer[] images= {R.drawable.movie1,R.drawable.movie2,R.drawable.movie3,R.drawable.movie4,R.drawable.movie5};
    private  String[] images;
   // private ArrayList<Integer> imageArrayList = new ArrayList<Integer>();
    private ArrayList<String> imageArrayList = new ArrayList<String>();
    ListAdapter listAdapter;
    List1Adapter list1Adapter;
    List2Adapter list2Adapter;
    ArrayList<Movie> movieArrayList;
    ArrayList<Movie> movie1ArrayList;
    ArrayList<Movie> movie2ArrayList;
    ArrayList<Movie> movie3ArrayList;
    RecyclerView rv1,rv2,rv3;
    CircleIndicator indicator;
    Timer swipeTimer;
    FrameLayout layout;
    ProgressBar loadingIndicator;
    TextView fav_text,see_all_latest,see_all_old;
    LinearLayout latestJukeboxLayout,oldJukeboxLayout,favJukeboxLayout;
    private static final int LATEST_JUKEBOX_LOADER_ID = 111;
    private static final int OLD_JUKEBOX_LOADER_ID = 122;
    private static final int FAVOURITE_JUKEBOX_LOADER_ID = 103;
    Cursor cursor;
    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            movie1ArrayList = savedInstanceState.getParcelableArrayList("latest");
            movie2ArrayList = savedInstanceState.getParcelableArrayList("old");
            movie3ArrayList = savedInstanceState.getParcelableArrayList("favourite");
        } else {
            movie1ArrayList = new ArrayList<>();
            movie2ArrayList = new ArrayList<>();
            movie3ArrayList = new ArrayList<>();
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                getLoaderManager().initLoader(LATEST_JUKEBOX_LOADER_ID, null,this).forceLoad();
                getLoaderManager().initLoader(OLD_JUKEBOX_LOADER_ID, null,this).forceLoad();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_main, container, false);
            setHasOptionsMenu(true);
        setRetainInstance(true);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        movie1ArrayList = new ArrayList<>();
        movie2ArrayList = new ArrayList<>();
        movie3ArrayList = new ArrayList<>();
        images=new String[5];
        layout=(FrameLayout)view.findViewById(R.id.layout);
        loadingIndicator = (ProgressBar)view.findViewById(R.id.loading_indicator_movie_poster);
        latestJukeboxLayout = (LinearLayout)view.findViewById(R.id.latestJukeboxlayout);
        oldJukeboxLayout = (LinearLayout)view.findViewById(R.id.oldJukeboxlayout);
        favJukeboxLayout = (LinearLayout)view.findViewById(R.id.favJukeboxlayout);
        movieArrayList=getArguments().getParcelableArrayList("most_popular");
        rv1=(RecyclerView)view.findViewById(R.id.latest);
        rv2=(RecyclerView)view.findViewById(R.id.favorite);
        rv3=(RecyclerView)view.findViewById(R.id.old);
        fav_text=(TextView)view.findViewById(R.id.text_fav);
        see_all_latest=(TextView)view.findViewById(R.id.see_all_latest);
        see_all_old=(TextView)view.findViewById(R.id.see_all_old);
        mPager = (ViewPager)view.findViewById(R.id.pager);
        indicator = (CircleIndicator)view.findViewById(R.id.indicator);
        init();
        cursor = getActivity().getContentResolver().query(FavouriteContract.FavoriteEntry.CONTENT_URI, null, null, null, null);
        if (cursor!=null){
            if (cursor.getCount()>0){fav_text.setVisibility(View.VISIBLE);
                System.out.println(cursor.getCount());}
            if (cursor.moveToFirst())
            {
                do {
                    Movie movie=new Movie();
                    movie.setVideo_id(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_VIDEO_ID)));
                    movie.setImage_url(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_POSTER_PATH)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_MOVIE_NAME)));
                    movie.setLike(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_LIKE_COUNT)));
                    movie.setView(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_VIEW_COUNT)));
                    movie2ArrayList.add(movie);
                }while (cursor.moveToNext());
            }
            cursor.close();
            list1Adapter =new List1Adapter(getActivity(), movie2ArrayList,MainFragment.this);
            rv2.setAdapter(list1Adapter);
            RecyclerView.LayoutManager lmanager2=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
            rv2.setLayoutManager(lmanager2);
            SnapHelper snapHelper2 = new GravitySnapHelper(Gravity.START);
            rv2.setOnFlingListener(null);
            snapHelper2.attachToRecyclerView(rv2);
        }
        if (movie1ArrayList.isEmpty() && movie3ArrayList.isEmpty()) {
            loadingIndicator.setVisibility(View.VISIBLE);
            latestJukeboxLayout.setVisibility(View.GONE);
            oldJukeboxLayout.setVisibility(View.GONE);
        } else if ((!(movie1ArrayList.isEmpty()) || !(movie3ArrayList.isEmpty()))) {
            loadingIndicator.setVisibility(View.GONE);
        }

        see_all_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AllLatestJukeboxActivity.class);
                getActivity().startActivity(intent);
            }
        });
        see_all_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AllOldJukeboxActivity.class);
                getActivity().startActivity(intent);
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                           getActivity().finish();
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.menu_share,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId()==R.id.share)
        {
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("text/plain");
            intentShare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.project.lorvent.way2freshers");
            //startActivity(Intent.createChooser(intentShare, "Select an action"));
            startActivityForResult(Intent.createChooser(intentShare, "Select an action"), 0);
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {

        swipeTimer = new Timer();

       // mPager.setAdapter(new SliderAdapter(imageArrayList,getActivity()));
        mPager.setAdapter(new SliderAdapter(movieArrayList,getActivity()));

        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == images.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipeTimer.cancel();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("latest", movie1ArrayList);
        outState.putParcelableArrayList("old", movie2ArrayList);
        outState.putParcelableArrayList("favourite", movie3ArrayList);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onItemClicked(ListAdapter.SimpleViewHolder holder, int position) {
         swipeTimer.cancel();
        Fragment detailFragment = new DetailFragment();
        Bundle bundle=new Bundle();
        Movie movie=movie1ArrayList.get(position);
        bundle.putString("video_id",movie.getVideo_id());
        bundle.putString("image_url",movie.getImage_url());
        bundle.putString("like",movie.getLike());
        bundle.putString("view",movie.getView());
        bundle.putString("movie_name",movie.getTitle());
        bundle.putParcelableArrayList("most_popular",movieArrayList);

        detailFragment.setArguments(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          //  detailFragment.setSharedElementEnterTransition(new DetailsTransition());
       /*     detailFragment.setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
            detailFragment.setEnterTransition(new Slide());
            setExitTransition(new Fade());
            detailFragment.setSharedElementReturnTransition(new DetailsTransition());*/
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                //.addSharedElement(holder.poster, "kittenImage")
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemClicked(List1Adapter.SimpleViewHolder holder, int position) {
        swipeTimer.cancel();
        Fragment detailFragment = new DetailFragment();
        Bundle bundle=new Bundle();
        Movie movie=movie2ArrayList.get(position);
        bundle.putString("video_id",movie.getVideo_id());
        bundle.putString("image_url",movie.getImage_url());
        bundle.putString("like",movie.getLike());
        bundle.putString("view",movie.getView());
        bundle.putString("movie_name",movie.getTitle());
        bundle.putParcelableArrayList("most_popular",movieArrayList);
        detailFragment.setArguments(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //  detailFragment.setSharedElementEnterTransition(new DetailsTransition());
           // detailFragment.setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
         /*   detailFragment.setEnterTransition(new Slide());
            setExitTransition(new Fade());
            detailFragment.setSharedElementReturnTransition(new DetailsTransition());*/
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                //.addSharedElement(holder.poster, "kittenImage")
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemClicked(List2Adapter.SimpleViewHolder holder, int position) {
        swipeTimer.cancel();
        Fragment detailFragment = new DetailFragment();
        Bundle bundle=new Bundle();
        Movie movie=movie3ArrayList.get(position);
        bundle.putString("video_id",movie.getVideo_id());
        bundle.putString("image_url",movie.getImage_url());
        bundle.putString("like",movie.getLike());
        bundle.putString("view",movie.getView());
        bundle.putString("movie_name",movie.getTitle());
        bundle.putParcelableArrayList("most_popular",movieArrayList);
        detailFragment.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //  detailFragment.setSharedElementEnterTransition(new DetailsTransition());
           // detailFragment.setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.fade));
            /*detailFragment.setEnterTransition(new Slide());
            setExitTransition(new Fade());
            detailFragment.setSharedElementReturnTransition(new DetailsTransition());*/
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                //.addSharedElement(holder.poster, "kittenImage")
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frame, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        String url="";
        if (id == LATEST_JUKEBOX_LOADER_ID) {
            //url="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&playlistId=PLUsLbqK_M40yR_0pgN50r0ZhP-adYrreq&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
            url=Config.LATEST_JUKEBOX_URL;
        } else if (id == OLD_JUKEBOX_LOADER_ID) {
            //url="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&playlistId=PLUsLbqK_M40yXsLU7--NB52V4Q-u9-0-a&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
            url=Config.OLD_JUKEBOX_URL;
        }
        return new JukeboxLoader(getActivity(),url);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        switch (loader.getId()) {
            case LATEST_JUKEBOX_LOADER_ID:
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
                    movie1ArrayList=data;
                    listAdapter =new ListAdapter(getActivity(), movie1ArrayList,MainFragment.this);
                    rv1.setAdapter(listAdapter);
                    rv1.setItemAnimator(new DefaultItemAnimator());
                    RecyclerView.LayoutManager lmanager1=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
                    rv1.setLayoutManager(lmanager1);
                    SnapHelper snapHelper1 = new GravitySnapHelper(Gravity.START);
                    rv1.setOnFlingListener(null);
                    snapHelper1.attachToRecyclerView(rv1);
                    latestJukeboxLayout.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);

                }
                break;
            case OLD_JUKEBOX_LOADER_ID:
                if (data.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(layout, "we are getting some server issue!Please wait for sometime", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbar.show();
                    return;
                } else {
                    movie3ArrayList=data;
                    list2Adapter =new List2Adapter(getActivity(), movie3ArrayList,MainFragment.this);
                    rv3.setAdapter(list2Adapter);
                    RecyclerView.LayoutManager lmanager3=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
                    rv3.setLayoutManager(lmanager3);
                    SnapHelper snapHelper3 = new GravitySnapHelper(Gravity.START);
                    rv3.setOnFlingListener(null);
                    snapHelper3.attachToRecyclerView(rv3);
                    oldJukeboxLayout.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);

                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

 /*   public class YoutubeTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            URL url ;
            HttpURLConnection connection ;
            try {

                url=new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=PLUsLbqK_M40yR_0pgN50r0ZhP-adYrreq&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("items");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    JSONObject sub_object=object.getJSONObject("snippet");
                    JSONObject thumbnails=sub_object.getJSONObject("thumbnails");
                    JSONObject image_url=thumbnails.getJSONObject("medium");
                    Movie movie =new Movie();
                    movie.setTitle(sub_object.getString("title"));
                    movie.setLike("56");
                    movie.setView("128");
                    movie.setImage_url(image_url.getString("url"));
                    movie1ArrayList.add(movie);
                    movie2ArrayList.add(movie);
                    movie3ArrayList.add(movie);
                }

                listAdapter =new ListAdapter(getActivity(), movie1ArrayList,MainFragment.this);
                list1Adapter =new List1Adapter(getActivity(), movie2ArrayList,MainFragment.this);
                list2Adapter =new List2Adapter(getActivity(), movie3ArrayList,MainFragment.this);

                rv1.setAdapter(listAdapter);
                rv2.setAdapter(list1Adapter);
                rv3.setAdapter(list2Adapter);

                rv1.setItemAnimator(new DefaultItemAnimator());
                rv2.setItemAnimator(new DefaultItemAnimator());
                rv3.setItemAnimator(new DefaultItemAnimator());

                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager1=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
                RecyclerView.LayoutManager lmanager2=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
                RecyclerView.LayoutManager lmanager3=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);

                rv1.setLayoutManager(lmanager1);
                rv2.setLayoutManager(lmanager2);
                rv3.setLayoutManager(lmanager3);

                SnapHelper snapHelper1 = new GravitySnapHelper(Gravity.START);
                snapHelper1.attachToRecyclerView(rv1);
                SnapHelper snapHelper2 = new GravitySnapHelper(Gravity.START);
                snapHelper2.attachToRecyclerView(rv2);
                SnapHelper snapHelper3 = new GravitySnapHelper(Gravity.START);
                snapHelper3.attachToRecyclerView(rv3);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }*/

}
