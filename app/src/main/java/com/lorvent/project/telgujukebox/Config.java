package com.lorvent.project.telgujukebox;

/**
 * Created by user on 2/8/17.
 */

class Config {

    static final String YOUTUBE_API_KEY = "AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
    static final String LATEST_JUKEBOX_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&playlistId=PLUsLbqK_M40yR_0pgN50r0ZhP-adYrreq&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
    static final String OLD_JUKEBOX_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&playlistId=PLUsLbqK_M40yXsLU7--NB52V4Q-u9-0-a&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
    static final String VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2cstatistics&key="+YOUTUBE_API_KEY+"&id=";
    static final String COMMENT_URL = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&key="+YOUTUBE_API_KEY+"&videoId=";
    static final String ALL_OLD_JUKEBOX_URL="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=25&playlistId=PLUsLbqK_M40yXsLU7--NB52V4Q-u9-0-a&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";
    static final String ALL_LATEST_JUKEBOX_URL="https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&maxResults=25&playlistId=PLUsLbqK_M40yR_0pgN50r0ZhP-adYrreq&key=AIzaSyAgOh3CKuy8d7ntlq7Ga-NrrRzNrKWWYvA";

}
