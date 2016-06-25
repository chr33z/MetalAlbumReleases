package com.elegantwalrus.metalalbumreleases.data;

/**
 * Created by Chris on 27.03.2016.
 */
public class ReleaseData {

    private String band = "";

    private String bandUrl = "";

    private String release = "";

    private String releaseUrl = "";

    private String releaseType = "";

    private String genre = "";

    private String date = "";

    public ReleaseData() {

    }

    public ReleaseData(String band, String bandUrl, String release, String releaseUrl, String releaseType, String genre, String date) {
        this.band = band;
        this.bandUrl = bandUrl;
        this.release = release;
        this.releaseUrl = releaseUrl;
        this.releaseType = releaseType;
        this.genre = genre;
        this.date = date;
    }

    public ReleaseData(ReleaseData release) {
        this.band = release.getBand();
        this.bandUrl = release.getBandUrl();
        this.release = release.getRelease();
        this.releaseUrl = release.getReleaseUrl();
        this.releaseType = release.getReleaseType();
        this.genre = release.getGenre();
        this.date = release.getDate();
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getBandUrl() {
        return bandUrl;
    }

    public void setBandUrl(String bandUrl) {
        this.bandUrl = bandUrl;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getReleaseUrl() {
        return releaseUrl;
    }

    public void setReleaseUrl(String releaseUrl) {
        this.releaseUrl = releaseUrl;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
