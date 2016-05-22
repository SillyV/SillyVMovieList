package com.example.vasili.sillyvmovielist.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.vasili.sillyvmovielist.R;
import com.example.vasili.sillyvmovielist.db.Movie;
import com.example.vasili.sillyvmovielist.db.MovieTableHandler;
import com.squareup.picasso.Picasso;

public class EditActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (!getIntent().getStringExtra(MainActivity.ACTIVITY_INTENT).equals(MainActivity.NEW_ITEM))
        {
            insertData(getMovieFromIntent());
            EditText title = (EditText)findViewById(R.id.title_edit);
            setUpToolbar(title.getText().toString());
        }
        else
        {
            setUpToolbar("Add a Movie");
        }
        setUpTextChangedListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_save)
        {
            saveMovie();
        }
        else if (id == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpTextChangedListener()
    {
        ((EditText) findViewById(R.id.url_edit)).addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                ((ImageView) findViewById(R.id.image_view_edit_activity)).setImageResource(R.drawable.image_background);
            }
            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
    }

    private Movie getMovieFromIntent()
    {
        String title = getIntent().getStringExtra(MainActivity.SEARCH_RESULT_TITLE);
        int year = getIntent().getIntExtra(MainActivity.SEARCH_RESULT_YEAR, 0);
        String body = getIntent().getStringExtra(MainActivity.SEARCH_RESULT_BODY);
        String url = getIntent().getStringExtra(MainActivity.SEARCH_RESULT_URL);
        int id = getIntent().getIntExtra(MainActivity.SEARCH_RESULT_ID, -1);
        if (id > -1)
        {
            return new Movie(id, title, year, body, url);
        }
        else
        {
            return new Movie(title, year, body, url);
        }
    }

    private void insertData(Movie movie)
    {
        final ImageView imageView = (ImageView) findViewById(R.id.image_view_edit_activity);
        ((EditText) findViewById(R.id.title_edit)).setText(movie.getTitle());
        ((EditText) findViewById(R.id.year_edit)).setText(String.valueOf(movie.getYear()));
        ((EditText) findViewById(R.id.body_edit)).setText(movie.getPlot());
        ((EditText) findViewById(R.id.url_edit)).setText(movie.getPoster());
        Picasso.with(this).load(movie.getPoster()).into(imageView, new com.squareup.picasso.Callback()
        {
            @Override
            public void onSuccess()
            {
            }

            @Override
            public void onError()
            {
                imageView.setImageResource(R.drawable.image_background);
            }
        });
    }

    private void setUpToolbar(String s)
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        toolbar.setTitle(s);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void saveMovie()
    {
        MovieTableHandler db = new MovieTableHandler(this);
        Movie movie = GenerateMovieObjectFromTextEdits();
        if (movie != null)
        {
            if (getIntent().getStringExtra(MainActivity.ACTIVITY_INTENT).equalsIgnoreCase(MainActivity.EDIT_ITEM))
            {
                movie.setID(getMovieFromIntent().getID());
                db.updateMovie(movie);
            }
            else
            {
                db.addMovie(movie);
            }
            setResult(RESULT_OK);
            finish();
        }
    }

    private Movie GenerateMovieObjectFromTextEdits()
    {
        String title = ((EditText) findViewById(R.id.title_edit)).getText().toString();
        if (title.equals(""))
        {
            TextInputLayout til = (TextInputLayout) findViewById(R.id.title_edit_layout);
            til.setError("Required Field");
            return null;
        }
        int year;
        try
        {
            year = Integer.parseInt(((EditText) findViewById(R.id.year_edit)).getText().toString());
        }
        catch (Exception e)
        {
            TextInputLayout til = (TextInputLayout) findViewById(R.id.year_edit_layout);
            til.setError("Not a valid year!");
            return null;
        }
        String body = CompensateForEmptyString(((EditText) findViewById(R.id.body_edit)).getText().toString());
        String url = CompensateForEmptyString(((EditText) findViewById(R.id.url_edit)).getText().toString());
        return new Movie(title, year, body, url);
    }

    private String CompensateForEmptyString(String s)
    {
        if (s.equals(""))
        {
            return "N/A";
        }
        return s;
    }

    public void onImageClick(View view)
    {
        final ImageView imageView = (ImageView) view;
        String urlToLoad = ((EditText) findViewById(R.id.url_edit)).getText().toString();
        if (!urlToLoad.equals(""))
        {
            Picasso.with(this).load(urlToLoad).into(imageView, new com.squareup.picasso.Callback()
            {
                @Override
                public void onSuccess()
                {
                }

                @Override
                public void onError()
                {
                    TextInputLayout til = (TextInputLayout) findViewById(R.id.url_edit_layout);
                    til.setError("Failed to load image.");
                    imageView.setImageResource(R.drawable.image_background);
                }
            });
        }
        else
        {
            TextInputLayout til = (TextInputLayout) findViewById(R.id.url_edit_layout);
            til.setError("Can't load from blank URL");
        }
    }
}