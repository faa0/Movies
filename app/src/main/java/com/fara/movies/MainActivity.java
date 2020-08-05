package com.fara.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fara.movies.data.FavouriteMovie;
import com.fara.movies.data.MainViewModel;
import com.fara.movies.data.Movie;
import com.fara.movies.utils.JSONUtils;
import com.fara.movies.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Switch switchSort;
    private RecyclerView rvPosters;
    private MovieAdapter movieAdapter;
    private TextView tvTopRated;
    private TextView tvPopularity;

    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTopRated = findViewById(R.id.tvTopRated);
        tvPopularity = findViewById(R.id.tvPopularity);
        switchSort = findViewById(R.id.switchSort);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        rvPosters = findViewById(R.id.rvPosters);
        rvPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();
        rvPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setMethodOfSort(b);
            }
        });
        switchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "sos", Toast.LENGTH_SHORT).show();
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    public void setMethodOfSort(boolean isTopRated) {
        int methodOfSort;
        if (isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            tvTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            tvPopularity.setTextColor(getResources().getColor(R.color.white));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            tvPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            tvTopRated.setTextColor(getResources().getColor(R.color.white));
        }
        downloadDate(methodOfSort, 1);
    }

    private void downloadDate(int methodOfSort, int page) {

        JSONObject jsonObject = null;
        try {
            jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        } catch (MalformedURLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
            if (movies != null && !movies.isEmpty()) {
                viewModel.deleteAllMovies();
                for(Movie movie : movies) {
                    viewModel.insertMovie(movie);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}