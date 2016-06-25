package com.elegantwalrus.metalalbumreleases.sharedpreferences;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Chris on 13.05.2016.
 */
@SharedPref
public interface GlobalPreferences {

    long lastUpdated();
}
