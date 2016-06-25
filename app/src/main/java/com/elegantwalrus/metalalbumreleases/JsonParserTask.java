package com.elegantwalrus.metalalbumreleases;

import android.os.AsyncTask;
import android.util.Log;

import com.elegantwalrus.metalalbumreleases.data.ReleaseData;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 27.03.2016.
 */
public class JsonParserTask extends AsyncTask<JSONObject, Double, List<ReleaseData>> {

    public interface TaskListener {

        void onPostExecute(List<ReleaseData> releaseData);

        void onProgressUpdate(double percentage);
    }

    private TaskListener delegate;

    public JsonParserTask(TaskListener delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<ReleaseData> doInBackground(JSONObject... params) {
        List<ReleaseData> releaseData = new ArrayList<>();

        for (JSONObject object : params) {
            try {
                JSONArray jsonArray = object.getJSONArray("data");

                for (int i=0; i < jsonArray.length(); i++) {
                    JSONArray details = jsonArray.getJSONArray(i);
                    ReleaseData release = parseRelease(details);

                    releaseData.add(release);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return releaseData;
    }

    private ReleaseData parseRelease(JSONArray details) {
        ReleaseData release = new ReleaseData();

        try {
            String linkContent = details.getString(0);
            Document link =  Jsoup.parse(linkContent);
            release.setBand(link.text());
            release.setBandUrl(link.attr("href"));
        } catch(Exception e) {
            Log.e("BAND", e.getLocalizedMessage());
        }

        try {
            String linkContent = details.getString(1);
            Document link =  Jsoup.parse(linkContent);
            release.setRelease(link.text());
            release.setReleaseUrl(link.attr("href"));
        } catch(Exception e) {
            Log.e("RELEASE", e.getLocalizedMessage());
        }

        try {
            release.setReleaseType(details.getString(2));
        } catch(Exception e) {
            Log.e("TYPE", e.getLocalizedMessage());
        }

        try {
            release.setGenre(details.getString(3));
        } catch(Exception e) {
            Log.e("GENRE", e.getLocalizedMessage());
        }

        try {
            String date = parseDate(details.getString(4));
            release.setDate(date);
        } catch(Exception e) {
            Log.e("DATE", e.getLocalizedMessage());
        }

        return release;
    }

    private String parseDate(String dateString) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d',' yyyy").withLocale(Locale.US);
        String text = dateString.replaceAll("(\\d)st", "$1");
        text = text.replaceAll("(\\d)nd", "$1");
        text = text.replaceAll("(\\d)rd", "$1");
        text = text.replaceAll("(\\d)th", "$1");
        DateTime dt = fmt.parseDateTime(text);

        //Log.d("DATE", String.format("Date: %s, Time: %s", text, String.valueOf(dt.getMillis())));

        return String.valueOf(dt.getMillis());
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);

        delegate.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(List<ReleaseData> releaseData) {
        super.onPostExecute(releaseData);

        delegate.onPostExecute(releaseData);
    }
}
