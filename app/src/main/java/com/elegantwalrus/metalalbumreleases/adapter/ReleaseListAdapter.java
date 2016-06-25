package com.elegantwalrus.metalalbumreleases.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elegantwalrus.metalalbumreleases.R;
import com.elegantwalrus.metalalbumreleases.data.ReleaseData;
import com.elegantwalrus.metalalbumreleases.sharedpreferences.GlobalPreferences;

import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 27.03.2016.
 */
public class ReleaseListAdapter extends BaseAdapter {

    private List<ReleaseData> data;

    private Context context;


    public ReleaseListAdapter(Context context, List<ReleaseData> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<ReleaseData> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return (data != null) ? data.size() : 0;
    }

    @Override
    public ReleaseData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.release_list_item, null);
        }

        ReleaseData release = getItem(position);

        ((TextView) convertView.findViewById(R.id.text_band)).setText(release.getBand());
        ((TextView) convertView.findViewById(R.id.text_release)).setText(release.getRelease());
        ((TextView) convertView.findViewById(R.id.text_genre)).setText(release.getGenre());

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d',' yyyy").withLocale(Locale.getDefault());
        long millis = Long.parseLong(release.getDate());
        String dateString = fmt.print(new DateTime(millis));

        return convertView;
    }
}
