package com.example.vasili.sillyvmovielist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vasili.sillyvmovielist.R;
import com.example.vasili.sillyvmovielist.db.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Vasili on 12/7/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie>
{

    private final Context context;
    private final List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> objects)
    {
        super(context, R.layout.list_item_main, objects);
        this.context = context;
        this.movies = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_main, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.title_list_item_main);
        TextView year = (TextView) convertView.findViewById(R.id.year_list_item_main);
        ImageView image = (ImageView) convertView.findViewById(R.id.image_list_item_main);
        Picasso.with(getContext()).load(movies.get(position).getPoster()).into(image);
        title.setText(movies.get(position).getTitle());
        String yearStr = "";
        if (movies.get(position).getYear() > Movie.NO_INPUT_YEAR)
        {
            yearStr = (context.getString(R.string.leftBracket) + movies.get(position).getYear() + context.getString(R.string.rightBracket));
        }
        year.setText(yearStr);
        convertView.setTag(movies.get(position).getID());
        return convertView;
    }


}
