package com.example.vasili.sillyvmovielist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vasili.sillyvmovielist.R;
import com.example.vasili.sillyvmovielist.adapters.MovieAdapter;
import com.example.vasili.sillyvmovielist.components.FabAnimator;
import com.example.vasili.sillyvmovielist.db.Movie;
import com.example.vasili.sillyvmovielist.db.MovieTableHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String ACTIVITY_INTENT = "com.example.vasili.sillyVMovieList.activity_intent";
    public static final String EDIT_ITEM = "com.example.vasili.sillyVMovieList.edit_item";
    public static final String NEW_ITEM = "com.example.vasili.sillyVMovieList.new_item";
    public static final String SEARCH_RESULT = "com.example.vasili.sillyVMovieList.search_result";
    public static final String SEARCH_RESULT_ID = "com.example.vasili.sillyVMovieList.search_result_id";
    public static final String SEARCH_RESULT_TITLE = "com.example.vasili.sillyVMovieList.search_result_title";
    public static final String SEARCH_RESULT_YEAR = "com.example.vasili.sillyVMovieList.search_result_year";
    public static final String SEARCH_RESULT_BODY = "com.example.vasili.sillyVMovieList.search_result_body";
    public static final String SEARCH_RESULT_URL = "com.example.vasili.sillyVMovieList.search_result_url";
    private static final int RESULT_CODE_MAIN = 44441;
    public static final int RESULT_CODE_SEARCH = 44442;

    private MovieAdapter adapter;
    private List<Movie> mv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        displayListView();
        defineFloatingActionButtons();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        minimizeFloatingActionButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_exit)
        {
            finish();
        }
        else if (id == R.id.action_remove_all)
        {
            MovieTableHandler db = new MovieTableHandler(this);
            db.removeAll();
            reloadList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadList()
    {
        MovieTableHandler db = new MovieTableHandler(this);
        mv.clear();
        mv.addAll(db.getAllMovies());
        adapter.notifyDataSetChanged();
    }

    private void defineFloatingActionButtons()
    {
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FabAnimator fabAnimator = new FabAnimator(this, new FloatingActionButton[]{fab1, fab2});
        fab.show();
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
                FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
                if (fab1.getVisibility() == View.GONE)
                {
                    fab.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate));
                    fab1.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.jump_from_down));
                    fab2.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.jump_from_down));
                    fab1.show();
                    fab2.show();
                }
                else
                {
                    fab.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_back));
                    fab1.startAnimation(fabAnimator.getMenuAnimationDown());
                    fab2.startAnimation(fabAnimator.getMenuAnimationDown());
                }
            }
        });
        fab1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, RESULT_CODE_MAIN);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent;
                intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(ACTIVITY_INTENT, NEW_ITEM);
                startActivityForResult(intent, RESULT_CODE_MAIN);
            }
        });
    }

    private void displayListView()
    {
        MovieTableHandler db = new MovieTableHandler(this);
        mv = db.getAllMovies();
        adapter = new MovieAdapter(this, mv);
        ListView lv = (ListView) findViewById(R.id.list_view_main);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MovieTableHandler db = new MovieTableHandler(MainActivity.this);
                editMovie(db.getMovie((int) view.getTag()));
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                final View selectedItem = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).
                        setTitle("Choose Action").
                        setIcon(R.mipmap.ic_launcher).
                        setItems(getResources().getStringArray(R.array.long_click_options), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MovieTableHandler db = new MovieTableHandler(MainActivity.this);
                                switch (which)
                                {
                                    case 0:
                                        editMovie(db.getMovie((int) selectedItem.getTag()));
                                        break;
                                    case 1:
                                        db.removeMovie((int) selectedItem.getTag());
                                        reloadList();
                                        break;
                                    case 2:
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                                        sendIntent.setType("text/plain");
                                        startActivity(sendIntent);
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    private void editMovie(Movie movie)
    {
        Intent intent;
        intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(ACTIVITY_INTENT, EDIT_ITEM);
        intent.putExtra(SEARCH_RESULT_ID, movie.getID());
        intent.putExtra(SEARCH_RESULT_TITLE, movie.getTitle());
        intent.putExtra(SEARCH_RESULT_YEAR, movie.getYear());
        intent.putExtra(SEARCH_RESULT_BODY, movie.getPlot());
        intent.putExtra(SEARCH_RESULT_URL, movie.getPoster());
        startActivityForResult(intent, RESULT_CODE_MAIN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_CODE_MAIN)
        {
            if (resultCode == RESULT_OK)
            {
                reloadList();
            }
        }
    }

    private void minimizeFloatingActionButton()
    {
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab1.setVisibility(View.GONE);
        fab2.setVisibility(View.GONE);
    }
}
