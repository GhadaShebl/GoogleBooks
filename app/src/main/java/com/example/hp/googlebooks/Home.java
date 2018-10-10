package com.example.hp.googlebooks;

import android.app.DownloadManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int INITIAL_LOADER_ID = 1;

    /** URL used to fetch latest published books (max 10) as default list view items */
    private static final String INITIAL_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=2018&orderBy=newest&langRestrict=en&maxResults=10";

    /** Initial part of the url used when a search query is entered */
    private static final String SEARCH_QUERY_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    /** The part that is added to the end of url to order query results by newest */
    private static final String ORDERBY = "&orderBy=newest";

    /** This variable will contain the url that the loader will pass to asyncTask */
    String QUERY;

    /** Holds the boolean value for if the device is connected to a network or not*/
    boolean isConnected;

    ArrayList<Book> Books;
    EditText searchQuery;
    ListView booksList;
    BooksListCustomAdapter booksListCustomAdapter;
    LinearLayout emptyView, noConnectionView;
    ProgressBar loadingIndicator;
    ImageButton searchBtn;
    EditText search_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        showCursorOnTouch();

        // References to UI elements.
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        booksList = (ListView) findViewById(R.id.booksList);
        noConnectionView = (LinearLayout) findViewById(R.id.noConnectionView);
        emptyView = (LinearLayout) findViewById(R.id.emptyView);

        // Call the function to check internet connectivity
        // now this variable holds the network state of the device
        isConnected = checkInternetConnectivity();


        // Setup the list view with an empty adapter until the data is fetched
        booksListCustomAdapter = new BooksListCustomAdapter(new ArrayList<Book>(), getApplicationContext());
        booksList.setAdapter(booksListCustomAdapter);

        // Open the web page of the book when it's list item is clicked
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = booksListCustomAdapter.getItem(i).getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader.
        QUERY = INITIAL_REQUEST_URL;
        loaderManager.initLoader(INITIAL_LOADER_ID, null, this);

        // When the search button is clicked, a new query should be issued
        searchBtn = (ImageButton) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_query = (EditText) findViewById(R.id.search_query);
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append(SEARCH_QUERY_URL);
                queryBuilder.append(search_query.getText().toString());
                queryBuilder.append(ORDERBY);
                QUERY = queryBuilder.toString();
                getLoaderManager().restartLoader(INITIAL_LOADER_ID, null, Home.this);
            }
        });
    }

    /**
     * This manages the search edit text, as it shouldn't show any blinking cursor
     * at first, the cursor should show along with the keyboard once the edit text is tapped.
     */
    private void showCursorOnTouch() {
        searchQuery = (EditText) findViewById(R.id.search_query);
        searchQuery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                searchQuery.setCursorVisible(true);
                return false;
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, QUERY);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> Books) {
        // Stop the loading indicator once the loading is finished so we can show either the list or the empty state
        loadingIndicator.setVisibility(View.GONE);

        isConnected = checkInternetConnectivity();
        noConnectionView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        if (isConnected == false) {
            booksList.setEmptyView(noConnectionView);
        } else {
            booksList.setEmptyView(emptyView);
        }

        // Clear the adapter of previous book data and load new data
        booksListCustomAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (Books != null && !Books.isEmpty()) {
            booksListCustomAdapter.addAll(Books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        booksListCustomAdapter.clear();
    }

    /**
     * Returns the state of device's network connectivity
     */
    boolean checkInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connection = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return connection;
    }

}
