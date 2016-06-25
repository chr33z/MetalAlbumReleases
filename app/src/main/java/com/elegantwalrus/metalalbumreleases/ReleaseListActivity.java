package com.elegantwalrus.metalalbumreleases;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.applidium.headerlistview.HeaderListView;
import com.elegantwalrus.metalalbumreleases.adapter.ReleaseListSectionAdapter;
import com.elegantwalrus.metalalbumreleases.data.JSONDataStore;
import com.elegantwalrus.metalalbumreleases.data.ReleaseData;
import com.elegantwalrus.metalalbumreleases.sharedpreferences.GlobalPreferences_;
import com.elegantwalrus.metalalbumreleases.util.Constants;
import com.elegantwalrus.metalalbumreleases.view.FontAwesomeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EActivity(R.layout.activity_release_list)
@OptionsMenu(R.menu.main_menu)
public class ReleaseListActivity extends AppCompatActivity {

    private final static String TAG = ReleaseListActivity.class.getSimpleName();

    @ViewById(R.id.list)
    HeaderListView mList;

    @ViewById(R.id.search_input)
    EditText mSearchBar;

    @ViewById(R.id.btn_search_input_delete)
    FontAwesomeView mSearchInputDelete;

    @ViewById(R.id.container_progress)
    RelativeLayout mProgressContainer;

    @Pref
    GlobalPreferences_ pref;

    private ReleaseListSectionAdapter mAdapter;

    @AfterViews
    public void onContent() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(mAdapter != null) {
                    mAdapter.setSearchTerm(s.toString());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReleaseData();
    }

    private void getReleaseData() {
        long lastUpdate = pref.lastUpdated().get();
        long now = DateTime.now().getMillis();

        if(lastUpdate != 0 && now - lastUpdate < Constants.TIME_UNTIL_UPDATE) {
            try {
                Log.d(TAG, "Release data found in data store...");
                Log.d(TAG, "Try to load releases from data store...");

                JSONObject json = JSONDataStore.readFromStore(this);
                processJSON(json);

                Log.d(TAG, "Try to load releases from data store... finished");

            } catch (IOException | JSONException e) {
                Log.d(TAG, "Try to load releases from data store... error");

                e.printStackTrace();
                getReleaseDatesFromApi();
            }
        }
        else {
            Log.d(TAG, "No release data found in data store...");
            getReleaseDatesFromApi();
        }
    }

    private void getReleaseDatesFromApi() {
        Log.d(TAG, "Try to load releases from api... ");

        MetalReleaseApi.loadLatestReleases(new MetalReleaseApi.MetalReleaseResponse() {
            @Override
            public void onFinished(JSONObject json) {
                processJSON(json);
                Log.d(TAG, "Try to load releases from api... finished");
            }

            @Override
            public void onError() {
                Log.e(TAG, "Error retrieving releases from api...");
            }
        });
    }

    private void setListData(List<ReleaseData> data) {
        List<ReleaseData> preparedData = removeDuplicates(data);

        if(mAdapter == null) {
            mAdapter = new ReleaseListSectionAdapter(this, preparedData, adapterClickListener);
            mList.setAdapter(mAdapter);
        } else {
            mAdapter.setData(preparedData);
            mAdapter.notifyDataSetChanged();
        }
    }

    private ReleaseListSectionAdapter.ClickListener adapterClickListener = new ReleaseListSectionAdapter.ClickListener() {
        @Override
        public void onHeaderClick() {
            final SectionsDialog dialog = new SectionsDialog();
            dialog.setSections(mAdapter.getSections());
            dialog.setOnClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getBaseContext(), "Item position: " + position, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    int absolutePosition = mAdapter.getTotalSectionPosition(position);
                    mList.getListView().setSelection(absolutePosition);
                }
            });
            dialog.show(getSupportFragmentManager(), "section_dialog");
        }
    };

    private List<ReleaseData> removeDuplicates(List<ReleaseData> data){
        List<ReleaseData> preparedData = new ArrayList<>();
        Set<String> elements = new HashSet<>();

        for (ReleaseData item : data) {
            String id = item.getBand()+item.getRelease();

            if(elements.contains(id)){
                continue;
            } else {
                elements.add(id);
                preparedData.add(item);
            }
        }

        return preparedData;
    }

    private void processJSON(JSONObject json) {
        try {
            JSONDataStore.writeToStore(this, json);
            pref.edit().lastUpdated().put(DateTime.now().getMillis()).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParserTask task = new JsonParserTask(new JsonParserTask.TaskListener() {

            @Override
            public void onPostExecute(List<ReleaseData> releaseData) {
                setListData(releaseData);
                mProgressContainer.animate().alpha(0).setDuration(500).start();
                mSearchBar.setEnabled(true);
                mSearchInputDelete.setEnabled(true);
            }

            @Override
            public void onProgressUpdate(double percentage) {

            }
        });
        task.execute(json);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_favorites:
                showFavortiesActivity();
                break;
            case R.id.menu_about:
                showAboutActivity();
                break;
            default:
                // do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavortiesActivity() {

    }

    private void showAboutActivity() {
        AboutActivity_.intent(this).start();
    }

    @Click(R.id.btn_search_input_delete)
    void onSearchInputDelete() {
        mSearchBar.setText("");
        if(mAdapter != null) {
            mAdapter.setSearchTerm("");
            mAdapter.notifyDataSetChanged();
        }
    }
}
