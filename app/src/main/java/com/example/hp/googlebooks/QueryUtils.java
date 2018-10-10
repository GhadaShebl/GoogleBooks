package com.example.hp.googlebooks;

import android.text.TextUtils;
import android.util.JsonReader;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    String authors;
    private QueryUtils() {
    }

    final private static String default_author = "Not Specified";
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> Books = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return Books;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
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
            Log.e(LOG_TAG, "Problem retrieving the Books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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

    public static List<Book> extractFeatureFromJson(String BookJSON) {
        if (TextUtils.isEmpty(BookJSON)) {
            return null;
        }

        List<Book> Books = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(BookJSON);
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject currentBook = items.getJSONObject(i);
                JSONObject currentVolumeInfo = currentBook.getJSONObject("volumeInfo");
                String title = currentVolumeInfo.getString("title");
                String date = currentVolumeInfo.getString("publishedDate");
                String previewLink = currentVolumeInfo.getString("previewLink");

                // some books don't have their authors specified
                // so this part is added so that the app will continue fetching books
                // event if it didn't find a field called "authors"
                if(currentVolumeInfo.isNull("authors")){
                    Books.add(new Book(title,default_author,date,previewLink));
                }
                else{
                    JSONArray Authors = currentVolumeInfo.getJSONArray("authors");
                    Books.add(new Book(title,concatAuthors(Authors),date,previewLink));
                }



            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);

        }


        return Books;
    }

    /**
     * @param authors is the json array that contains all authors for a single book
     * Sometimes there is more than one author to a book, as the authors field is an array
     * So this function takes the JSON Array authors and concats it into a single string
     */
    private static String concatAuthors(JSONArray authors) {
        StringBuilder authorsBuilder = new StringBuilder();
        for (int i = 0; i < authors.length()-1; i++)
        {
            try {
                authorsBuilder.append(authors.get(i)+" & ");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            authorsBuilder.append(authors.get(authors.length()-1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorsBuilder.toString();
    }

}

