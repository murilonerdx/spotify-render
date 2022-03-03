package com.murilonerdx.spotifyrender.request;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private Long id;
    private String artist;
    private String music;
    private Integer count;


    public Song(String artist, String music, Integer count) {
        this.artist = artist;
        this.music = music;
        this.count = count;
    }

    public Song() {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
