package com.elegantwalrus.metalalbumreleases;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 16.05.2016.
 */
public class SectionsDialog extends DialogFragment {

    private List<Long> sections;

    private AdapterView.OnItemClickListener listener;

    public SectionsDialog() {

    }

    public SectionsDialog setSections(List<Long> sections) {
        this.sections = sections;
        return this;
    }

    public SectionsDialog setOnClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_list, null);
        builder.setView(view);
        final Dialog dialog = builder.create();

        if(sections != null && sections.size() > 0){
            DialogListAdapter adapter = new DialogListAdapter(getContext(), sections);
            ((ListView) view.findViewById(R.id.list)).setAdapter(adapter);
            ((ListView) view.findViewById(R.id.list)).setOnItemClickListener(listener);

        }
        view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private class DialogListAdapter extends BaseAdapter {

        private final Context context;

        private final List<Long> data;

        private final DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d',' yyyy").withLocale(Locale.getDefault());

        public DialogListAdapter(Context context, List<Long> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Long getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.dialog_list_item, null);
            }

            long millis = getItem(position);
            String date = fmt.print(new DateTime(millis));

            ((TextView)convertView.findViewById(R.id.text)).setText(date);

            return convertView;
        }
    }
}
