package com.cf.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class ReviewResult implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String author;
    @Expose
    private String content;
    @Expose
    private String url;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    public ReviewResult() {
    }

    protected ReviewResult(Parcel in) {
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<ReviewResult> CREATOR = new Parcelable.Creator<ReviewResult>() {
        public ReviewResult createFromParcel(Parcel source) {
            return new ReviewResult(source);
        }

        public ReviewResult[] newArray(int size) {
            return new ReviewResult[size];
        }
    };
}
