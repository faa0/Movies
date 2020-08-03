package com.fara.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fara.movies.data.Movie;
import com.fara.movies.utils.JSONUtils;
import com.fara.movies.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSONObject jsonObject = null;
        try {
            jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 5);
            ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
            StringBuilder builder = new StringBuilder();
            for (Movie movie : movies) {
                builder.append(movie.getTitle()).append("\n");
            }
            Log.i("MyResult", builder.toString());
        } catch (MalformedURLException | ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
}