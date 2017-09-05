package com.lorvent.project.telgujukebox.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHISHEK RAJ on 12/1/2016.
 */

public class Review implements Parcelable {


    public static final Creator<Review> CREATOR
            = new Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private String mAuthor;
    private String mURL;
    private String mContent;


    public Review() {
    }


    public Review(String author, String content, String url) {
        mAuthor = author;
        mURL = url;
        mContent = content;

    }

    private Review(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
        mURL = in.readString();
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getMovieReviewAuthor() {
        return mAuthor;
    }

    public String getMovieReviewContent() {
        return mContent;
    }

    public String getMovieReviewURL() {
        return mURL;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mAuthor);
        out.writeString(mContent);
        out.writeString(mURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}