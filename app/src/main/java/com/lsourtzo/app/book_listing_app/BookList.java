package com.lsourtzo.app.book_listing_app;

/**
 * Created by lsourtzo on 14/05/2017.
 */

public class BookList {

    private String mTitle;
    private String mSubTitle;
    private String mDetails;
    private String mAuthors;
    private String mPublisher;
    private String mPublished;
    private String mLink;
    private String mImageURL;

    // title is for title , textd is for the main text, sorttext is for sortversion text in button and imageId its for image
    public BookList(String title, String subTitle, String details, String authors, String ImageResourceId, String publisher, String published, String link) {
        mTitle = title;
        mSubTitle = subTitle;
        mDetails = details;
        mAuthors = authors;
        mPublisher = publisher;
        mPublished = published;
        mLink = link;
        mImageURL = ImageResourceId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public String getdetailedText() {
        return mDetails;
    }

    public String getAuthorName() {
        return mAuthors;
    }

    public String getPublisherName() {
        return mPublisher;
    }

    public String getPublishedDate() {
        return mPublished;
    }

    public String getlinkText() {
        return mLink;
    }

    public String getImageResourceId() {
        return mImageURL;
    }

}
