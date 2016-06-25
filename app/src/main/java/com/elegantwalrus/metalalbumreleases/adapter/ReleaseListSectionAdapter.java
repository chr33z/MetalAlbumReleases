package com.elegantwalrus.metalalbumreleases.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.headerlistview.SectionAdapter;
import com.elegantwalrus.metalalbumreleases.R;
import com.elegantwalrus.metalalbumreleases.comperator.ReleaseDateComparator;
import com.elegantwalrus.metalalbumreleases.comperator.ReleaseNameComparator;
import com.elegantwalrus.metalalbumreleases.data.ReleaseData;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 30.03.2016.
 */
public class ReleaseListSectionAdapter extends SectionAdapter {


    public interface ClickListener {
        public void onHeaderClick();
    }

    private Context context;

    private List<ReleaseData> data;

    private List<ReleaseData> filteredData;

    private List<Long> sections;

    private List<List<ReleaseData>> rows;

    private String searchTerm;

    private ReleaseDateComparator dateComparator = new ReleaseDateComparator();

    private ReleaseNameComparator nameComparator = new ReleaseNameComparator();

    private final ClickListener listener;

    public ReleaseListSectionAdapter(Context context, List<ReleaseData> data, ClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;

        sections = new ArrayList<>();
        rows = new ArrayList<>();

        filteredData = copyList(data);
        updateAdapter();
    }

    public void setData(List<ReleaseData> data) {
        this.data = data;
        filteredData = copyList(data);

        updateAdapter();
    }

    public void setSearchTerm(String search) {
        this.searchTerm = search;

        if(!searchTerm.equals("")) {
            updateFilteredData();
        } else {
            filteredData.clear();
            filteredData.addAll(data);
        }

        updateAdapter();
    }

    private void updateFilteredData() {
        filteredData.clear();

        for (ReleaseData release : data) {
            if(
                release.getBand().toLowerCase().contains(searchTerm.toLowerCase()) ||
                release.getRelease().toLowerCase().contains(searchTerm.toLowerCase()) ||
                release.getGenre().toLowerCase().contains(searchTerm.toLowerCase())
            ) {
                filteredData.add(release);
            }
        }
    }

    private void updateAdapter() {
        // sort data ascending
        Collections.sort(filteredData, dateComparator);

        sections.clear();
        rows.clear();

        long oldDate = -1;
        List<ReleaseData> rowsOfSection = null;
        for (int i = 0; i <= filteredData.size(); i++) {
            ReleaseData release = null;

            if(i < filteredData.size()) {
                release = filteredData.get(i);
            } else {
                // we do an additional round after we processed all items
                // to save the rows of the last section we filled
                rows.add(rowsOfSection);
                break;
            }

            if(!release.getDate().equals("")) {
                // get release date
                long currentDate = -1;
                try {
                    currentDate = Long.valueOf(release.getDate());
                } catch(Exception e) {
                    Log.e("TAG", e.getLocalizedMessage());
                    continue;
                }

                if(currentDate != oldDate) {
                    // save rows of old date
                    if(rowsOfSection != null) {
                        rows.add(rowsOfSection);
                    }

                    // create new list to fill
                    rowsOfSection = new ArrayList<>();
                    rowsOfSection.add(release);
                    sections.add((currentDate));
                    oldDate = currentDate;
                } else if(currentDate == oldDate && rowsOfSection != null) {
                    rowsOfSection.add(release);

                    if(i == filteredData.size() - 1) {
                        rows.add(rowsOfSection);
                    }
                }
            }
        }

        // sort each section alphabetically
        for (List<ReleaseData> ros : rows) {
            if(ros != null) {
                Collections.sort(ros, nameComparator);
            }
        }

        notifyDataSetChanged();
    }

    private static List<ReleaseData> copyList(List<ReleaseData> source) {
        List<ReleaseData> destiny = new ArrayList<>(source.size());
        for (ReleaseData item : source) {
            destiny.add(new ReleaseData(item));
        }
        return destiny;
    }

    public List<Long> getSections() {
        return sections;
    }

    public int getTotalSectionPosition(int section) {
        if(section == 0) {
            return 0;
        } else if(section == 1) {
            return 1 + rows.get(0).size();
        } else {
            return 1 + rows.get(section - 1).size() + getTotalSectionPosition(section - 1);
        }
    }

    @Override
    public int numberOfSections() {
        return sections.size();
    }

    @Override
    public int numberOfRows(int section) {
        try {
            return rows.get(section) != null ? rows.get(section).size() : 0;
        } catch(ArrayIndexOutOfBoundsException e) {
            return 0;
        } catch(IndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public ReleaseData getRowItem(int section, int row) {
        return rows.get(section) != null ? rows.get(section).get(row) : null;
    }

    @Override
    public boolean hasSectionHeaderView(int section) {
        return true;
    }

    @Override
    public View getRowView(int section, int row, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.release_list_item, null);
        }

        ReleaseData release = getRowItem(section, row);

        ((TextView) convertView.findViewById(R.id.text_band)).setText(release.getBand());
        ((TextView) convertView.findViewById(R.id.text_release)).setText(release.getRelease());
        ((TextView) convertView.findViewById(R.id.text_genre)).setText(release.getGenre());

        return convertView;
    }

    @Override
    public int getSectionHeaderViewTypeCount() {
        return 1;
    }

    @Override
    public int getSectionHeaderItemViewType(int section) {
        return 0;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.release_list_header, null);
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d',' yyyy").withLocale(Locale.getDefault());
        long millis = sections.get(section);
        String dateString = fmt.print(new DateTime(millis));

        ((TextView) convertView.findViewById(R.id.text)).setText(dateString);
        convertView.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onHeaderClick();
            }
        });


        return convertView;
    }

    @Override
    public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
        super.onRowItemClick(parent, view, section, row, id);
        Toast.makeText(context, "Section " + section + " Row " + row, Toast.LENGTH_SHORT).show();
    }
}
