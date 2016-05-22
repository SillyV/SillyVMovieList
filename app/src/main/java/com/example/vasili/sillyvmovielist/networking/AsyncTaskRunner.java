package com.example.vasili.sillyvmovielist.networking;

import android.os.AsyncTask;
import android.util.Log;

import com.example.vasili.sillyvmovielist.activities.SearchActivity;
import com.example.vasili.sillyvmovielist.db.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasili on 12/7/2015.
 */
public class AsyncTaskRunner extends AsyncTask<Void, Void, List<Movie>>
{

    private static final String NOT_FOUND_ERROR = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}\n";
    private static final String TAG = "SillyV.task";
    private final String url;
    private final OnDataReceivedListener listener;

    public AsyncTaskRunner(String url, OnDataReceivedListener listener)
    {
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected List<Movie> doInBackground(Void... params)
    {
        String response = sendHttpRequest(url);
        List<Movie> movies;
        if (!response.equals(NOT_FOUND_ERROR))
        {
            if (url.contains(SearchActivity.SEARCH_URL))
            {
                movies = firstJSONExtractor(response);
            }
            else
            {
                movies = secondJSONExtractor(response);
            }
        }
        else
        {
            movies = new ArrayList<>();
        }
        return movies;
    }

    private List<Movie> firstJSONExtractor(String s)
    {
        List<Movie> movies = new ArrayList<>();
        try
        {
            JSONArray searchResults;
            searchResults = new JSONObject(s).getJSONArray("Search");
            for (int i = 0; i < searchResults.length(); i++)
            {
                try
                {
                    JSONObject jo = searchResults.getJSONObject(i);
                    String title = getJ(jo, "Title");
                    String sYear = getJ(jo, "Year");
                    int year = convertToNumber(sYear);
                    String imdbID = getJ(jo, "imdbID");
                    String poster = getJ(jo, "Poster");
                    Movie m = new Movie(title, year, "", poster);
                    m.setImdbID(imdbID);
                    movies.add(m);
                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    Log.e(TAG, je.getMessage());
                }
            }
            return movies;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private int convertToNumber(String sYear)
    {
        int d;
        try
        {
            d = Integer.parseInt(sYear);
        }
        catch (NumberFormatException nfe)
        {
            return 0;
        }
        return d;
    }


    private List<Movie> secondJSONExtractor(String s)
    {
        List<Movie> movies = new ArrayList<>();
        try
        {
            JSONObject jo = new JSONObject(s);
            String title = getJ(jo, "Title");
            String sYear = getJ(jo, "Year");
            int year = convertToNumber(sYear);
            String poster = getJ(jo, "Poster");
            String plot = getJ(jo, "Plot");
            Movie m = new Movie(title, year, plot, poster);
            movies.add(m);
            return movies;
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            Log.e(TAG, je.getMessage());
        }
        return null;
    }

    private String sendHttpRequest(String urlString)
    {
        BufferedReader input = null;
        HttpURLConnection httpCon = null;
        StringBuilder response = new StringBuilder();

        try
        {
            URL url = new URL(urlString);
            httpCon = (HttpURLConnection) url.openConnection();

            // Check the status of the connection
            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                Log.e(TAG, "Cannot connect to: " + urlString + " response code " + httpCon.getResponseCode());
                return "";
            }
            //convert the data from the server into a string

            // Use BufferReader to read the data into a string
            input = new BufferedReader(
                    new InputStreamReader(httpCon.getInputStream()));

            String line;
            while ((line = input.readLine()) != null)
            {
                response.append(line).append("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (httpCon != null)
            {
                httpCon.disconnect();
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(List<Movie> s)
    {
        listener.onDataReceived(s);
    }

    public interface OnDataReceivedListener
    {
        void onDataReceived(List<Movie> response);
    }

    private String getJ(JSONObject o, String key)
    {
        if (o.has(key))
        {
            try
            {
                return o.getString(key);
            }
            catch (JSONException je)
            {
                je.printStackTrace();
                Log.w(TAG, je.getMessage());
            }
        }
        return "";
    }
}
