package net.uberfoo.badgelife.trivia.badgersscoreboard;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class ScoreboardApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("/font/roboto_slab/RobotoSlab-VariableFont_wght.ttf"), 12);

        FXMLLoader fxmlLoader = new FXMLLoader(ScoreboardApplication.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<MainController>getController().setOwnerStage(stage);
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Badgelife Trivia Scoreboard");
        stage.setScene(scene);

        // Exit the application when the main window is closed
        stage.setOnCloseRequest(event -> Platform.exit());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}