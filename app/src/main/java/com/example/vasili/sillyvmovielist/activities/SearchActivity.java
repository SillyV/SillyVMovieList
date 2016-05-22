package com.example.vasili.sillyvmovielist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vasili.sillyvmovielist.R;
import com.example.vasili.sillyvmovielist.adapters.MovieAdapter;
import com.example.vasili.sillyvmovielist.db.Movie;
import com.example.vasili.sillyvmovielist.networking.AsyncTaskRunner;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity implements AsyncTaskRunner.OnDataReceivedListener
{
    private static final String INNER_SEARCH_URL = "http://www.omdbapi.com/?i=";
    public static final String SEARCH_URL = "http://www.omdbapi.com/?s=";
    private static final String ORIENTATION_CHANGE_SERIALIZED = "saveInstantStateListView";
    final private List<Movie> mv = new ArrayList<>();
    private SearchView searchView;
    private boolean aMovieWasSelected = false;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchQuery = query;
                if (query.length() > 1)
                {
                    new AsyncTaskRunner(getUrl(query), SearchActivity.this).execute();
                    findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                    findViewById(R.id.progress_bar).bringToFront();
                    searchView.setIconified(true);
                    searchView.onActionViewCollapsed();
                    return  true;
                }
                return false;
            }

            private String getUrl(String query)
            {
                String searchStr = query.trim().replaceAll(" ", "+");
                return SEARCH_URL + searchStr;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_search)
        {
            return true;
        }
        else if (id == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataReceived(List<Movie> response)
    {
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        if (aMovieWasSelected)
        {
            goToEditActivity(response);
        }
        else
        {
            loadListViewWithResponse(response);
        }
    }

    private void loadListViewWithResponse(List<Movie> response)
    {
        mv.clear();
        ListView lv = (ListView) findViewById(R.id.search_results_list_view);
        if (getSupportActionBar() != null)
        {
            if (response.size() == 0)
            {
                getSupportActionBar().setTitle(R.string.no_results);
                lv.setVisibility(View.INVISIBLE);
            }
            else
            {
                getSupportActionBar().setTitle(response.size() + getString(R.string.results_found) + searchQuery + "\"");
                lv.setVisibility(View.VISIBLE);
            }
        }
        mv.addAll(response);
        MovieAdapter adapter = new MovieAdapter(this, mv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                aMovieWasSelected = true;
                new AsyncTaskRunner(getUrl(mv.get(position).getImdbID()), SearchActivity.this).execute();
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                findViewById(R.id.progress_bar).bringToFront();
            }

            private String getUrl(String query)
            {
                String searchStr = query.trim().replaceAll(" ", "+");
                return INNER_SEARCH_URL + searchStr;
            }
        });
    }

    private void goToEditActivity(List<Movie> response)
    {
        aMovieWasSelected = false;
        Movie movie = response.get(0);
        Intent intent;
        intent = new Intent(this, EditActivity.class);
        intent.putExtra(MainActivity.ACTIVITY_INTENT, MainActivity.SEARCH_RESULT);
        intent.putExtra(MainActivity.SEARCH_RESULT_TITLE, movie.getTitle());
        intent.putExtra(MainActivity.SEARCH_RESULT_YEAR, movie.getYear());
        intent.putExtra(MainActivity.SEARCH_RESULT_BODY, movie.getPlot());
        intent.putExtra(MainActivity.SEARCH_RESULT_URL, movie.getPoster());
        startActivityForResult(intent, MainActivity.RESULT_CODE_SEARCH);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MainActivity.RESULT_CODE_SEARCH)
        {
            if (resultCode == RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mv.size() > 0)
        {
            outState.putSerializable(ORIENTATION_CHANGE_SERIALIZED,(ArrayList<Movie>)mv);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<Movie> tempList = (ArrayList<Movie>) savedInstanceState.getSerializable(ORIENTATION_CHANGE_SERIALIZED);
            loadListViewWithResponse(tempList);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
