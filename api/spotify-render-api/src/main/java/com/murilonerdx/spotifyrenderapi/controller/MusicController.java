package com.murilonerdx.spotifyrenderapi.controller;

import com.murilonerdx.spotifyrenderapi.model.Song;
import com.murilonerdx.spotifyrenderapi.model.User;
import com.murilonerdx.spotifyrenderapi.repository.SongRepository;
import com.murilonerdx.spotifyrenderapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/music/spotify-render/")
public class MusicController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping()
    public User save(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/user/{name}")
    public User findByUser(@PathVariable String name) {
        return userRepository.findByName(name);
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/song/{artist}")
    public Song findByArtist(@PathVariable String artist) {
        return songRepository.findByArtist(artist);
    }

    @PutMapping("/user/update/{name}")
    public User update(@PathVariable String name, @RequestBody User user) {
        return userRepository.save(user);
    }


}
