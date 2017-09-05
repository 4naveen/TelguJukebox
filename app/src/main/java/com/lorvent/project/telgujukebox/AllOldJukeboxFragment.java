package com.lorvent.project.telgujukebox;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lorvent.project.telgujukebox.adapters.JukeboxAdapter;
import com.lorvent.project.telgujukebox.model.Movie;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllOldJukeboxFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    MaterialSearchView searchView;
    RecyclerView recyclerView;
    JukeboxAdapter jukeboxAdapter;
    ArrayList<Movie> movieArrayList;
    FrameLayout layout;
    private static final int OLD_JUKEBOX_LOADER_ID = 111;
    ProgressDialog dialog;

    public AllOldJukeboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            movieArrayList = savedInstanceState.getParcelableArrayList("latest");
        } else {
            movieArrayList = new ArrayList<>();
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                getLoaderManager().initLoader(OLD_JUKEBOX_LOADER_ID, null,this).forceLoad();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_all_old_jukebox, container, false);
        setHasOptionsMenu(true);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);
        layout=(FrameLayout)view.findViewById(R.id.layout);
        movieArrayList = new ArrayList<>();
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("latest", movieArrayList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Movie> subMovieArrayList=new ArrayList<>();
                for (int i=0;i<movieArrayList.size();i++)
                {
                    if (movieArrayList.get(i).getTitle().contains(newText))
                    {
                        subMovieArrayList.add(movieArrayList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                recyclerView.setAdapter(new JukeboxAdapter(subMovieArrayList, getActivity(),"old"));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        super.onResume();
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(true);
        String url="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=25&playlistId=PLUsLbqK_M40yXsLU7--NB52V4Q-u9-0-a&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
        return new JukeboxLoader(getActivity(),Config.ALL_OLD_JUKEBOX_URL);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if (dialog!=null&&dialog.isShowing())
        {dialog.dismiss();}

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
            jukeboxAdapter =new JukeboxAdapter(movieArrayList,getActivity(),"old");
            recyclerView.setAdapter(jukeboxAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            RecyclerView.LayoutManager linearlayoutmanager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(linearlayoutmanager);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
}
