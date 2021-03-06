package com.elegantwalrus.metalalbumreleases.comperator;

import com.elegantwalrus.metalalbumreleases.data.ReleaseData;

import java.util.Comparator;

/**
 * Created by Chris on 27.03.2016.
 */
public class ReleaseDateComparator implements Comparator<ReleaseData> {

    @Override
    public int compare(ReleaseData lhs, ReleaseData rhs) {
        return lhs.getDate().compareTo(rhs.getDate());
    }
}
