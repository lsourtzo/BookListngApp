package com.lsourtzo.app.book_listing_app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String finalUrlS = "finalUrl";

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    SearchView searchView;
    String finalUrl = "https://www.googleapis.com/books/v1/volumes?q=\"\"&maxResults=20";

    //this text gona help us keep the search word
    String queryWord = "";
    int Page;

    TextView button;
    //TextView footerButton;
    ImageView sendMailButton;

    ScrollView scroll;
    ScrollView rDawerScroll;

    //Variables
    String checkViewVisibility;
    FragmentManager fragmentManager;
    DrawerLayout drawer;

    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    //RadioGroups
    RadioGroup rNumberPerPage;
    RadioGroup rSortBy;
    RadioGroup rBookType;
    RadioGroup rPrintType;
    RadioButton idNumberPerPage;
    RadioButton idSortBy;
    RadioButton idBookType;
    RadioButton idPrintType;


    //Save Instance State to save the current search options on rotation.
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        savedInstanceState.putString(finalUrlS, finalUrl);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restore Instance State to restore the current search options on rotation.
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        finalUrl = savedInstanceState.getString(finalUrlS);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scroll = (ScrollView) findViewById(R.id.description_view);

        //LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // View view = inflater.inflate(R.layout.content_main, null);
        //footerButton = (TextView) view.findViewById(R.id.footerMessage);
        //footerButton.setText("test");

        // Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Left Drawer Stuff's
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Email Button
        View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        sendMailButton = (ImageView) hView.findViewById(R.id.sendMail);
        sendMailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lsourtzo@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Book Listing App");
                intent.putExtra(Intent.EXTRA_TEXT, "This Email send from Book Listing app.");
                startActivity(Intent.createChooser(intent, "Send Email"));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        // Right Drawer Stuff's
        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view2);
        View hView2 = navigationView2.inflateHeaderView(R.layout.nav_header_main2);
        rDawerScroll = (ScrollView) hView2.findViewById(R.id.rightDrawerScrollView);
        button = (TextView) hView2.findViewById(R.id.applyButton);

        rBookType = (RadioGroup) hView2.findViewById(R.id.RadioGroupBookType);
        rNumberPerPage = (RadioGroup) hView2.findViewById(R.id.RadioGroupBookPerPage);
        rPrintType = (RadioGroup) hView2.findViewById(R.id.RadioGroupPrintType);
        rSortBy = (RadioGroup) hView2.findViewById(R.id.RadioGroupSortBy);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                applyNewUrl();
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        //footerButton.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        Toast.makeText(getBaseContext(), "footer clicked",Toast.LENGTH_LONG).show();
        //    }
        //});

        // show loading indicator
        ProgressBar loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        //first time
        if (savedInstanceState == null) {
            //call fragment
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = Fragment_Activity.class;
            Bundle bundle = new Bundle();
            bundle.putString("finalUrl", finalUrl);
            bundle.putString("BooksPerPage", "20");
            bundle.putInt("Page", 0);
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();

            }
            // set Fragmentclass Arguments
            fragment.setArguments(bundle);
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // nothing to do ...
        return true;
    }

    // return if the detail view is on screan
    public void isViewVisible(String isVV) {
        checkViewVisibility = isVV;
    }

    //specify what to do when back button pressed
    @Override
    public void onBackPressed() {
        rDawerScroll.pageScroll(View.FOCUS_UP);
        if (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)) {
            //If drawable is opes close it ...
            drawer.closeDrawer(GravityCompat.START);
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if (checkViewVisibility == "false") {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();
                    finish();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), R.string.doubleClick, Toast.LENGTH_SHORT).show();
                }
                mBackPressed = System.currentTimeMillis();
            } else {
                // call close view from fragment activity
                Fragment_Activity frag = (Fragment_Activity) fragmentManager.findFragmentById(R.id.flContent);
                frag.closeView();
                //set visibility back to false because I just close the view
                checkViewVisibility = "false";
            }
        }
        scroll.pageScroll(View.FOCUS_UP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filters) {
            drawer.openDrawer(GravityCompat.END);
            toggle.syncState();
            return true;
        } else if (id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.filters_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                queryWord = query;
                applyNewUrl();
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });

        return true;
    }

    public void applyNewUrl() {

        Class fragmentClass = null;
        Fragment fragment = null;

        int selectedId = rBookType.getCheckedRadioButtonId();
        idBookType = (RadioButton) findViewById(selectedId);
        selectedId = rNumberPerPage.getCheckedRadioButtonId();
        idNumberPerPage = (RadioButton) findViewById(selectedId);
        selectedId = rPrintType.getCheckedRadioButtonId();
        idPrintType = (RadioButton) findViewById(selectedId);
        selectedId = rSortBy.getCheckedRadioButtonId();
        idSortBy = (RadioButton) findViewById(selectedId);

        //Build new url
        StringBuilder newURL = new StringBuilder(200);
        // first part...
        newURL.append("https://www.googleapis.com/books/v1/volumes?q=");
        // Searching word
        if (queryWord.equals("")) {
            newURL.append("\"\"");
        } else {
            // this is important to search for more than one word ...
            queryWord = queryWord.replace(" ", "%20");
            newURL.append("\"" + queryWord + "\"");
        }
        // Filter = Book Type
        String bookTypetext = idBookType.getText().toString();
        if (!bookTypetext.equals("none")) {
            newURL.append("&filter=");
            newURL.append(bookTypetext);
        }
        // SortBy
        String sortBytext = idSortBy.getText().toString();
        if (!sortBytext.equals("unsorted")) {
            newURL.append("&orderBy=");
            newURL.append(sortBytext);
        }
        // Print Type
        newURL.append("&printType=");
        newURL.append(idPrintType.getText().toString());
        // max results
        newURL.append("&maxResults=");
        newURL.append(idNumberPerPage.getText().toString());
        finalUrl = newURL.toString();

        //renew fragment activity
        try {
            fragmentClass = Fragment_Activity.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putString("finalUrl", finalUrl);
        bundle.putString("BooksPerPage", idNumberPerPage.getText().toString());
        bundle.putInt("Page", Page);
        Log.d("test 2","books per page :"+idNumberPerPage.getText().toString());
        // set Fragmentclass Arguments
        fragment.setArguments(bundle);
        fragmentManager = getFragmentManager();
        // Replace the existing fragment...by Reseting the stack.
        fragmentManager.popBackStackImmediate("0", 0);
        int count = fragmentManager.getBackStackEntryCount();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(String.valueOf(count)).commit();
        // show loading indicator
        ProgressBar loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

}
