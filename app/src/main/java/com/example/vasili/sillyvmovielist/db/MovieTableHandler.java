package com.example.vasili.sillyvmovielist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasili on 12/7/2015.
 */
public class MovieTableHandler
{

    static final String MOVIES_TABLE_NAME = "movies";
    static final String ID = "id";
    static final String TITLE = "title";
    static final String BODY = "body";
    static final String URL = "url";
    static final String YEAR = "year";
    private static final String TAG = "SillyV.TableHandler";
    private final MovieDBHelper dbHelper;

    public MovieTableHandler(Context context)
    {
        dbHelper = new MovieDBHelper(context);
    }

    public List<Movie> getAllMovies()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MOVIES_TABLE_NAME, null, null, null, null, null, null);
        List<Movie> movies = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Movie movie = getMovie(cursor);
            movies.add(movie);
        }
        return movies;
    }

    public Movie getMovie(int id)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MOVIES_TABLE_NAME, null, ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst())
        {
            return getMovie(cursor);
        }
        return null;
    }

    public long addMovie(Movie newMovie)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getMovieValues(newMovie);
        try
        {
            return db.insert(MOVIES_TABLE_NAME, null, values);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
        return -1;
    }

    public void updateMovie(Movie editedMovie)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getMovieValues(editedMovie);
        try
        {
            db.update(MOVIES_TABLE_NAME, values, ID + " = " + editedMovie.getID(), null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }

    public void removeMovie(int id)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            db.delete(MOVIES_TABLE_NAME, ID + " = " + id, null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }

    private static ContentValues getMovieValues(Movie movie)
    {
        ContentValues values = new ContentValues();
        values.put(TITLE, movie.getTitle());
        values.put(BODY, movie.getPlot());
        values.put(URL, movie.getPoster());
        values.put(YEAR, movie.getYear());
        // values.put(OMDBID,"");
        return values;
    }

    private static Movie getMovie(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(ID));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String body = cursor.getString(cursor.getColumnIndex(BODY));
        String url = cursor.getString(cursor.getColumnIndex(URL));
        int year = cursor.getInt(cursor.getColumnIndex(YEAR));

        return new Movie(id, title, year, body, url);
    }

    public void removeAll()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            db.delete(MOVIES_TABLE_NAME, null, null);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        db.close();
    }
}
