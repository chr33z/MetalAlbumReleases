package com.elegantwalrus.metalalbumreleases;

import android.text.Html;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 27.03.2016.
 */
public class MetalReleaseApi {

    private static final String API_URL = "http://www.metal-archives.com/release/ajax-upcoming/json/1?sEcho=1&iDisplayStart=%d";

    private static final int MAX_RELEASES = 500;

    private static final int RELEASE_INTERVALL_COUNT = 100;

    public interface MetalReleaseResponse {

        void onFinished(JSONObject json);

        void onError();
    }

    private interface VolleyResponse {

        void onFinished(String response);

        void onError(VolleyError error);
    }

    public static void loadLatestReleases(final MetalReleaseResponse delegate) {
        final int repeat = MAX_RELEASES / RELEASE_INTERVALL_COUNT;
        int start = 0;

        final List<String> responsePages = new ArrayList<>();

        for (int i = 0; i < repeat; i++) {
            PostRequest(start, new VolleyResponse() {

                @Override
                public void onFinished(String response) {
                    responsePages.add(response);

                    if(responsePages.size() == repeat) {
                        JSONObject combined = combineJson(responsePages);
                        delegate.onFinished(combined);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e("TAG", "Volley error" + error.getLocalizedMessage());
                }
            });

            start += RELEASE_INTERVALL_COUNT;
        }
    }

    private static void PostRequest(int startNumber, final VolleyResponse delegate) {
        String url = String.format(API_URL, startNumber);

        StringRequest request = new StringRequest (
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        delegate.onFinished(fixEncoding(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        delegate.onError(error);
                }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        MyApplication.getInstance().addToRequestQueue(request);
    }

    private static JSONObject combineJson(List<String> response) {
        JSONObject combined = new JSONObject();
        JSONArray releaseList = new JSONArray();

        for (String page : response) {
            try {
                JSONObject json = new JSONObject(page);
                JSONArray dataArray = json.getJSONArray("aaData");

                if(dataArray != null) {
                    for(int i = 0; i < dataArray.length(); i++) {
                        JSONArray item = dataArray.getJSONArray(i);
                        if(item != null) {
                            releaseList.put(item);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            combined.put("data", releaseList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return combined;
    }

    private static String fixEncoding(String response) {
        try {
            byte[] u = response.toString().getBytes("ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}
