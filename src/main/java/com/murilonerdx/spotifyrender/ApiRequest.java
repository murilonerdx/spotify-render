package com.murilonerdx.spotifyrender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.murilonerdx.spotifyrender.request.Song;
import com.murilonerdx.spotifyrender.request.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ApiRequest {

    public static void saveMusic(User user) throws IOException, InterruptedException {
        URL url = new URL("http://localhost:8086/music/spotify-render/");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Content-Type", "application/json");
        http.setRequestProperty("Content-Length", "0");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String data = ow.writeValueAsString(user);

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();

    }


    public static void updateMusics(User user) throws IOException, InterruptedException {
        URL url = new URL("http://localhost:8086/music/spotify-render/user/update/" + user.getName());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("PUT");
        http.setDoOutput(true);
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Content-Type", "application/json");

        User byUser = findByUser(user).get();
        List<Song> songs = user.getSongs();

        for (Song song : byUser.getSongs()) {
            for (Song song2 : songs) {
                if (Objects.equals(song.getMusic(), song2.getMusic())) {
                    song.setId(song2.getId());
                }
            }
        }

        User newUser = new User();
        newUser.setId(byUser.getId());
        newUser.setName(byUser.getName());
        newUser.setSongs(user.getSongs());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String data = ow.writeValueAsString(newUser);

        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        OutputStream stream = http.getOutputStream();
        stream.write(out);

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();

    }

    public static List<User> listUsers() throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = new URL("http://localhost:8086/music/spotify-render/users/");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setDoOutput(true);
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Content-Type", "application/json");

        InputStream inputStream = http.getInputStream();

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(
                List.class, User.class);
        List<User> users = objectMapper.readValue(inputStream, collectionType);

        http.disconnect();

        return users;
    }

    public static Optional<User> findByUser(User user) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = new URL("http://localhost:8086/music/spotify-render/user/" + user.getName());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setDoOutput(true);
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Content-Type", "application/json");

        InputStream inputStream = http.getInputStream();

        User userPayload = objectMapper.readValue(inputStream, User.class);

        http.disconnect();

        return Optional.ofNullable(userPayload);
    }
}
