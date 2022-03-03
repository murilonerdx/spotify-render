package com.murilonerdx.spotifyrender.request;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String name;

    List<Song> songs = new ArrayList<>();

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
