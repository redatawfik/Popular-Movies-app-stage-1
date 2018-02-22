package com.popular_movies_app_st_1.app.utilities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String API_KEY_PARAM = "api_key";

    //this is fake API Key
    private static final String API_KEY = "123456789";


    /**
     * Builds the URL used to talk to the movies server using type of sorting. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param sortBy The Order that movies will be sorted by .
     * @return The URL to use to query the movies server.
     */
    //===========================================
    public static URL buildUrl(String sortBy) {

        Uri buildUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (Exception e) {
            Log.v(TAG, "Build URL " + e);
        }

        return url;
    }
    //=======================================================


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
