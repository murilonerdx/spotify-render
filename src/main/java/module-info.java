module com.murilonerdx.spotifyrender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires android.json;
    requires com.fasterxml.jackson.databind;

    opens com.murilonerdx.spotifyrender to javafx.fxml;
    exports com.murilonerdx.spotifyrender;
    exports com.murilonerdx.spotifyrender.request;
    opens com.murilonerdx.spotifyrender.request to javafx.fxml;
}