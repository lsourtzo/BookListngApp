package com.lsourtzo.app.book_listing_app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.System.load;

/**
 * Created by lsourtzo on 14/05/2017.
 */

public class BookListAdapter extends ArrayAdapter<BookList> {

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists
     */
    public BookListAdapter(Activity context, ArrayList<BookList> bookList){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, bookList);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent ) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        BookList currentWord = getItem(position);

        // Book Cover Image
        ImageView image2 = (ImageView) listItemView.findViewById(R.id.categorie_image);
        if (currentWord.getImageResourceId()!="null"){
            Picasso.with(getContext()).load(currentWord.getImageResourceId()).into(image2);}
        else{
            image2.setImageResource(R.drawable.book_stack);
        }

        // Title Text
        TextView titleText = (TextView) listItemView.findViewById(R.id.title_text_view);
        //check title size ...
        String temp3 = currentWord.getTitle();
        if (temp3.length() > 60) {
            int temp2 = temp3.lastIndexOf(".");
            if ((temp2 <= 60)&&(temp2>=1)) {
                titleText.setText(currentWord.getTitle().substring(0, temp2));
            } else {
                titleText.setText(currentWord.getTitle().substring(0, 50) + " ...");
            }
        } else {
            titleText.setText(temp3);
        }

        // SubTitle Text
        TextView subTitleText = (TextView) listItemView.findViewById(R.id.subtitle_text_view);
        subTitleText.setText(currentWord.getSubTitle().substring(0, Math.min(currentWord.getSubTitle().length(), 60)) + " ...");

        // Authors Names Text
        TextView authorsNamesText = (TextView) listItemView.findViewById(R.id.author_text_view);
        authorsNamesText.setText(currentWord.getAuthorName());

        String publisherNameText = currentWord.getPublisherName();
        String publisherDateText = currentWord.getPublishedDate();
        String detailedText = currentWord.getdetailedText();
        String linkText = currentWord.getlinkText();

        return listItemView;
    }
}