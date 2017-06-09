package com.lsourtzo.app.book_listing_app;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsourtzo on 14/05/2017.
 */

public class Fragment_Activity extends Fragment implements LoaderManager.LoaderCallbacks<List<BookList>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    LinearLayout dItemList;
    ScrollView dScrollView;

    android.app.FragmentManager fragmentManager;

    //TextViews and more In details View
    ImageView DetailsImage;
    TextView DetailsTitle;
    TextView DetailsSubTitle;
    TextView DetailsAuthors;
    TextView DetailsPublisher;
    TextView CenterMessage;
    TextView DetailsText;
    TextView DetailsPublishDate;
    ImageView DetailsLink;

    String BooksPerPage;
    int Page;
    String startURL;

    TextView errorTextView;

    private static final int BOOK_LOADER_ID = 1;

    private BookListAdapter mAdapter;

    String requestUrl;

    TextView footerButton;

    LoaderManager loaderManager;

    ProgressBar loadingIndicator;

    String DetailsLinkText;

    public Fragment_Activity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loaderManager = getLoaderManager();

        //get Url from main Activity
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            requestUrl = getArguments().getString("finalUrl");
            startURL= getArguments().getString("finalUrl");
            BooksPerPage = getArguments().getString("BooksPerPage");
            Page = getArguments().getInt("Page");
            Log.d("test","books per page :"+BooksPerPage);
            int temp4 = Integer.parseInt(BooksPerPage)*Page;
            requestUrl=requestUrl+"&startIndex="+temp4;
            Log.d("test",Integer.parseInt(BooksPerPage)+" "+Page+" "+requestUrl);
            Page=Page+1;
        } else {
            requestUrl = "https://www.googleapis.com/books/v1/volumes?q=\"\"&maxResults=20";
        }

        final View rootView = inflater.inflate(R.layout.content_main, container, false);
        loadingIndicator = (ProgressBar) getActivity().findViewById(R.id.loading_indicator);

        dScrollView = (ScrollView) rootView.findViewById(R.id.description_view);
        dItemList = (LinearLayout) rootView.findViewById(R.id.description_view_back);

        //TextViews and more In details View
        DetailsImage = (ImageView) rootView.findViewById(R.id.categorie_image2);
        DetailsTitle = (TextView) rootView.findViewById(R.id.title_text_view2);
        DetailsSubTitle = (TextView) rootView.findViewById(R.id.subtitle_text_view2);
        DetailsAuthors = (TextView) rootView.findViewById(R.id.title_text_view2_author);
        DetailsPublisher = (TextView) rootView.findViewById(R.id.default_text_view2_publisher);
        DetailsPublishDate = (TextView) rootView.findViewById(R.id.default_text_view2_date);
        DetailsText = (TextView) rootView.findViewById(R.id.default_text_view2);

        errorTextView = (TextView) rootView.findViewById(R.id.errorMessage);

        CenterMessage = (TextView) rootView.findViewById(R.id.wellcomeMessage);
        DetailsLink = (ImageView) rootView.findViewById(R.id.link);

        // Send to Main Activity if Layer is visible
        ((MainActivity) getActivity()).isViewVisible("false");

        //Fill Adapter
        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookListAdapter(getActivity(), new ArrayList<BookList>());
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        // Add a footer to the ListView
        //LayoutInflater inflater2 = getLayoutInflater();
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer_layout, listView, false);
        listView.addFooterView(footer,null,false);

        // set footerbutton
        footerButton = (TextView) listView.findViewById(R.id.footerMessage);
        // and call footerButton Listener...
        footerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToNextPage();
               // Toast.makeText(getActivity(), "footer clicked",Toast.LENGTH_LONG).show();
            }
        });

        listView.setAdapter(mAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BookList books = mAdapter.getItem(position);

                ImageView image = (ImageView) rootView.findViewById(R.id.categorie_image2);
                if (books.getImageResourceId() != "null") {
                    Picasso.with(getActivity()).load(books.getImageResourceId()).into(image);
                } else {
                    image.setImageResource(R.drawable.book_stack);
                }

                DetailsTitle.setText(books.getTitle());
                if (books.getSubTitle() != books.getdetailedText()) {
                    DetailsSubTitle.setVisibility(View.VISIBLE);
                    DetailsSubTitle.setText(books.getSubTitle());
                } else {
                    DetailsSubTitle.setVisibility(View.GONE);
                    DetailsSubTitle.setText("");
                }
                DetailsAuthors.setText(books.getAuthorName());
                DetailsPublisher.setText(books.getPublisherName());
                DetailsPublishDate.setText(books.getPublishedDate());
                DetailsText.setText(books.getdetailedText());

                DetailsLinkText = books.getlinkText();

                dItemList.setVisibility(View.VISIBLE);
                // Send to Main Activity if Layer is visible
                ((MainActivity) getActivity()).isViewVisible("true");
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.


            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Update empty state with no connection error message
            errorTextView.setText("No Internet Connection");
            errorTextView.setVisibility(View.VISIBLE);
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.setVisibility(View.GONE);
        }

        // Set a click listener on that View
        DetailsLink.setOnClickListener(new View.OnClickListener()

        {
            // The code in this method will be executed when the homebutton will pressed
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DetailsLinkText));
                startActivity(intent);
                ((MainActivity) getActivity()).isViewVisible("true");
            }
        });

        return rootView;
    }

    public void closeView() {
        dItemList.setVisibility(View.GONE);
    }

    @Override
    public Loader<List<BookList>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(requestUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        return new BookListLoader(getActivity(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<BookList>> loader, List<BookList> data) {
        // Hide loading indicator because the data has been loaded
        CenterMessage = (TextView) getActivity().findViewById(R.id.wellcomeMessage);
        CenterMessage.setVisibility(View.GONE);

        // Hide Message because the data has been loaded
        try {
            loadingIndicator.setVisibility(View.GONE);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // Clear the adapter of previous books data

        mAdapter.clear();

        // If there is a valid list of {@link books}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);

        } else {
            errorTextView.setText("No Books to Show.");
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookList>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public void goToNextPage() {
        Class fragmentClass = null;
        Fragment fragment = null;
        
        //renew fragment activity
        try {
            fragmentClass = Fragment_Activity.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putString("finalUrl", startURL);
        bundle.putString("BooksPerPage", BooksPerPage);
        bundle.putInt("Page", Page);
        // set Fragmentclass Arguments
        fragment.setArguments(bundle);
        fragmentManager = getFragmentManager();
        // Replace the existing fragment...by Reseting the stack.
        fragmentManager.popBackStackImmediate("0", 0);
        int count = fragmentManager.getBackStackEntryCount();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(String.valueOf(count)).commit();
        // show loading indicator
        try {
            loadingIndicator.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
