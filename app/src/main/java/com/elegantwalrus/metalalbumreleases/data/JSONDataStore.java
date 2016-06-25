package com.elegantwalrus.metalalbumreleases.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Chris on 13.05.2016.
 */
public class JSONDataStore {

    private static final String TAG = JSONDataStore.class.getSimpleName();

    public static final String FILE_NAME = "release_json";

    public static void writeToStore(Context context, JSONObject json) throws IOException {
        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(json.toString().getBytes());
        fos.close();

        Log.d(TAG, "Releases written to data store...");
    }

    public static JSONObject readFromStore(Context context) throws IOException, JSONException {
        FileInputStream in = context.openFileInput(FILE_NAME);
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        inputStreamReader.close();
        bufferedReader.close();

        Log.d(TAG, "Releases read from data store...");

        return new JSONObject(sb.toString());
    }
}
