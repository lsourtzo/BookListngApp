/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lsourtzo.app.book_listing_app;

import android.app.ListActivity;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils extends ListActivity {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the  dataset and return a list of {@link List} objects.
     */
    public static List<BookList> fetchBookListData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<BookList> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link books}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";


        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link List} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<BookList> extractFeatureFromJson(String booksJson) {
        // If the JSON string is empty or null, then return early.

        // values
        String title = "";
        String subTitle = "";
        String publisher = "";
        String publishedDdate = "";
        String details = "";
        String linkURL = "";
        String imageURL = "";

        if (TextUtils.isEmpty(booksJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<BookList> books = new ArrayList<>();


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJson);

            if (!baseJsonResponse.getString("totalItems").equals("0")) {
                // Extract the JSONArray associated with the key called "items",
                // which represents a list of features (or books).
                JSONArray booksArray = baseJsonResponse.getJSONArray("items");

                // For each book in the bookArray, create an {@link Book} object
                for (int i = 0; i < booksArray.length(); i++) {

                    // Get a single book at position i within the list of books
                    JSONObject currentBook = booksArray.getJSONObject(i);

                    // For a given book, extract the JSONObject associated with the
                    // key called "properties", which represents a list of all volumeInfo
                    // for that book.
                    JSONObject volume = currentBook.getJSONObject("volumeInfo");

                    //Extract all keys checking if they has value
                    if (volume.has("title")) {
                        title = volume.getString("title");
                    } else {
                        title = "No title";
                    }
                    if (volume.has("publisher")) {
                        publisher = volume.getString("publisher");
                    } else {
                        publisher = "No title";
                    }
                    if (volume.has("publishedDate")) {
                        publishedDdate = volume.getString("publishedDate");
                    } else {
                        publishedDdate = "-";
                    }
                    if (volume.has("description")) {
                        details = volume.getString("description");
                    } else {
                        details = "No Info's";
                    }

                    // if there is subtitle
                    if (volume.has("subtitle")) {
                        subTitle = volume.getString("subtitle");
                    } else {
                        // if there is no subtitle ... looking if there is description text and take this as subtitle
                        if (volume.has("description")) {
                            subTitle = volume.getString("description");
                        } else {
                            // else show no info
                            subTitle = "No Info's";
                        }
                    }
                    if (volume.has("infoLink")) {
                        linkURL = volume.getString("infoLink");
                    } else {
                        linkURL = "null";
                    }

                    JSONObject images = volume.getJSONObject("imageLinks");
                    if (images.has("smallThumbnail")) {
                        imageURL = images.getString("smallThumbnail");
                    } else {
                        imageURL = "null";
                    }

                    String authorsNames = "";

                    // if authors exist
                    if (volume.has("authors")) {
                        JSONArray authorsN = volume.getJSONArray("authors");
                        authorsNames = authorsN.getString(0);
                        //in case tha there is more tha one ...
                        for (int z = 1; z < authorsN.length(); z++) {
                            String currentAuthor = authorsN.getString(z);
                            authorsNames = authorsNames + "\n" + currentAuthor;
                        }
                    } else {
                        //in case that the author dose not exist
                        authorsNames = "No Info";
                    }
                    // Create a new {@link Book} object with the magnitude, location, time,
                    // and url from the JSON response.
                    BookList bookList = new BookList(title, subTitle, details, authorsNames, imageURL, publisher, publishedDdate, linkURL);

                    // Add the new {@link Book} to the list of Books.
                    books.add(bookList);
                }
                //View footerView = (View) ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
                //getListView().addFooterView(footerView);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the books JSON results", e);
        }

        // Return the list of Books
        return books;
    }

}
