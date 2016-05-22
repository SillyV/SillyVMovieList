package com.example.vasili.sillyvmovielist.db;


import java.io.Serializable;

/**
 * Created by Vasili on 12/7/2015.
 */
public class Movie implements Serializable
{
    public static final int NO_INPUT_YEAR = 0;
    private int _id;
    private String _title;
    private int _year;
    private String _imdbID;
    private String _plot;
    private String _poster;


    public Movie(int id, String title, int year, String plot, String poster)
    {
        _id = id;
        _title = title;
        _year = year;
        _plot = plot;
        _poster = poster;
    }

    public Movie(String title, int year, String plot, String poster)
    {
        _title = title;
        _year = year;
        _plot = plot;
        _poster = poster;
    }

    public int getID()
    {
        return _id;
    }

    public void setID(int _id)
    {
        this._id = _id;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String _title)
    {
        this._title = _title;
    }

    public int getYear()
    {
        return _year;
    }

    public void setYear(int _year)
    {
        this._year = _year;
    }

    public String getImdbID()
    {
        return _imdbID;
    }

    public void setImdbID(String _imdbID)
    {
        this._imdbID = _imdbID;
    }

    public String getPlot()
    {
        return _plot;
    }

    public void setPlot(String _plot)
    {
        this._plot = _plot;
    }

    public String getPoster()
    {
        return _poster;
    }

    public void setPoster(String _poster)
    {
        this._poster = _poster;
    }

}
