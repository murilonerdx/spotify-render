package com.murilonerdx.spotifyrender;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.json.JSONException;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SpotifyController {
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(12);

    @FXML
    public Button button1;

    @FXML
    public PasswordField authorizationCode;

    @FXML
    public void say() throws Exception {
        Requests.CLIENT_ID = getProp().getProperty("client.id");
        Requests.CLIENT_SECRET = getProp().getProperty("client.secret");
        Requests.AUTH_CODE = authorizationCode.getText();
        Requests.getAccessTokenCurrentlyPlaying();
        executor.scheduleAtFixedRate(() -> {
            try {
                Requests.discordToken(getProp().getProperty("discord.token"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @FXML
    public void stop() throws JSONException {
        System.exit(0);
    }

    public void openUrl() throws Exception {
        try {
            URI uri = URI.create("https://accounts.spotify.com/authorize?response_type=code&client_id=" + getProp().getProperty("client.id") + "&scope=user-read-currently-playing&redirect_uri=http://localhost");
            Desktop.getDesktop().browse(uri);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProp() throws IOException {
        Properties props = new Properties();
        FileInputStream file = new FileInputStream(
                "src/main/resources/system.config");
        props.load(file);
        return props;
    }
}