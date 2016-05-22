package com.example.vasili.sillyvmovielist.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Vasili on 12/7/2015.
 */
class MovieDBHelper extends SQLiteOpenHelper
{
    private static final String TAG = "SillyV.DBHelper";
    private static final int VERSION = 2;
    private static final String DATA_BASE_NAME = "movies_db";


    public MovieDBHelper(Context context)
    {
        super(context, DATA_BASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTableStatement = "CREATE TABLE " + MovieTableHandler.MOVIES_TABLE_NAME +
                "(" + MovieTableHandler.ID + " INTEGER PRIMARY KEY," + MovieTableHandler.TITLE + " TEXT," +
                MovieTableHandler.BODY + " TEXT," + MovieTableHandler.YEAR + " INTEGER," +
                MovieTableHandler.URL + " TEXT)";
        Log.d(TAG, createTableStatement);
        try
        {
            db.execSQL(createTableStatement);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + MovieTableHandler.MOVIES_TABLE_NAME);
        onCreate(db);
    }

}
