package com.murilonerdx.spotifyrender;

import com.murilonerdx.spotifyrender.request.Song;
import com.murilonerdx.spotifyrender.request.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.murilonerdx.spotifyrender.ApiRequest.*;
import static com.murilonerdx.spotifyrender.SpotifyController.getProp;

public class Requests {

    public static String CLIENT_ID;
    public static String CLIENT_SECRET;
    public static String AUTH_CODE;
    public static String ACCESS_TOKEN;
    public static String REFRESH_TOKEN;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static String name;
    private static String artist;

    public static List<Song> temp = new LinkedList<Song>();

    public static Map<String, Map<String, Integer>> listeningArtist = new HashMap<String, Map<String, Integer>>();


    public static void getAccessTokenCurrentlyPlaying() throws Exception {

        String url = "https://accounts.spotify.com/api/token/";

        String params = "grant_type=authorization_code&redirect_uri=http://localhost&client_id="
                + CLIENT_ID
                + "&client_secret="
                + CLIENT_SECRET
                + "&scope=user-read-currently-playing&code="
                + AUTH_CODE;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("POST", HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                .header("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());
            ACCESS_TOKEN = jsonObject.get("access_token").toString();
            REFRESH_TOKEN = jsonObject.get("refresh_token").toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void refreshTokenCurrentlyPlaying() {

        String url = "https://accounts.spotify.com/api/token/";

        String refreshParams = "grant_type=refresh_token&redirect_uri=http://localhost&client_id="
                + CLIENT_ID
                + "&client_secret="
                + CLIENT_SECRET
                + "&scope=user-read-currently-playing&refresh_token="
                + REFRESH_TOKEN;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("POST", HttpRequest.BodyPublishers.ofString(refreshParams, StandardCharsets.UTF_8))
                .header("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());
            ACCESS_TOKEN = jsonObject.get("access_token").toString();
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }


    private static String getCurrentlyPlaying() {

        try {

            String url = "https://api.spotify.com/v1/me/player/currently-playing";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (!response.body().isEmpty()) {

                if (response.body().contains("The access token expired")) {
                    refreshTokenCurrentlyPlaying();

                } else {

                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject item = jsonObject.getJSONObject("item");
                        name = item.getString("name") + " - ";
                        artist = item.getJSONArray("artists").getJSONObject(0).getString("name");
                        name = name.replaceAll("\"", "");
                        getListening();

                    } catch (JSONException ignore) {
                        name = "no music playing";
                        artist = "";
                    }
                }

            } else {
                name = "no music playing";
                artist = "";
            }

            return "{\"custom_status\":{\"text\":\"" + name + artist + "\",\"emoji_name\":\"\uD83D\uDCBF\"}}";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void getListening() throws Exception {
        User byUser;
        List<Song> songs;
        List<User> users = getUsers();
        String name = getUserWithProps().getName();
        int size = temp.size();
        boolean b = false;


        boolean usersFound = users.stream().anyMatch(x -> x.getName().equals(name));

        if (usersFound) {
            byUser = findByUser(getUserWithProps()).get();
            songs = byUser.getSongs();
        } else {
            songs = new ArrayList<>();
            byUser = getUserWithProps();
        }
        Song song1 = createSong();

        boolean a = songs.stream().anyMatch(x->x.getMusic().equals(song1.getMusic()));
        if(size > 2){
            b = temp.get(size - 1).equals(temp.get(size - 2));
        }else if(size == 0){
            temp.add(song1);
            size = 1;
        }

        if (!a) {
            getListeningPut(usersFound);
        } else if(!b){
            for (Song song : songs) {
                if(song.getMusic().equals(song1.getMusic()) && !Objects.equals(temp.get(size - 1).getMusic(), song1.getMusic())){
                    update(createModelMusicRequestUpdate(song, song.getCount() + 1, byUser));
                }
            }
        }
        temp.add(song1);
    }

    private static User createModelMusicRequestUpdate(Song song, int i, User user) throws Exception {
        if (user != null) {
            user.getSongs().forEach((x) -> {
                if (x.getMusic().equals(song.getMusic())) {
                    x.setCount(i);
                }
            });
            return user;
        }
        return null;
    }

    private static User createModelMusicRequestSave(String artist, String music, int i) throws Exception {
        User user = getUserWithProps();
        Song song = new Song();

        song.setId(null);
        song.setArtist(artist);
        song.setMusic(music);
        song.setCount(i);

        user.getSongs().add(song);
        return user;
    }


    private static User getUserWithProps() {
        String username = null;
        try {
            username = getProp().getProperty("user.name");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.requireNonNull(username).isBlank()) {
            try {
                throw new Exception("Usuario no arquivo de configuração está vazio");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new User(null, username);
    }

    private static void getListeningPut(boolean usersFound) throws Exception {
        save(createModelMusicRequestSave(artist, getReplaceName(), 1), usersFound);
    }

    private static List<User> getUsers() throws IOException, InterruptedException {
        return listUsers();
    }

    private static void update(User user) throws Exception {
        updateMusics(user);
    }

    public static void save(User user, boolean usersFound) throws Exception {
        Song song1 = createSong();
        if (usersFound) {
            user = findByUser(user).get();
            user.getSongs().add(song1);

        } else {
            user = getUserWithProps();
        }
        saveMusic(user);
    }

    private static String getReplaceName() {
        return name.replace("-", "").trim();
    }

    private static Song createSong() {
        String music = Requests.name.replace("-", "").trim();
        String artist = Requests.artist.trim();
        return new Song(artist, music, 0);
    }

    public static void discordToken(String token) throws Exception {

        String url = "https://discord.com/api/v9/users/@me/settings";

        String data = getCurrentlyPlaying();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .build();


        try {
            System.out.println(HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()));
            System.out.println(listeningArtist);

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}