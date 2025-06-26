package net.uberfoo.badgelife.trivia.badgersscoreboard;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;

import java.io.IOException;

public class ScoreboardController {

    private final Stage scoreboardStage = new Stage();
    private final ObjectProperty<Game> gameProperty;

    public ScoreboardController(ObjectProperty<Game> gameProperty) {
        this.gameProperty = gameProperty;
        Label scoreboardLabel = new Label("Bagelife Trivia");

        VBox scoreboardLayout = new VBox(10, scoreboardLabel);
        scoreboardLayout.setAlignment(Pos.CENTER);
        Scene secondaryScene = new Scene(scoreboardLayout);

        // Remove window controls
        scoreboardStage.initStyle(StageStyle.UNDECORATED);

        // Add starting scene
        scoreboardStage.setScene(secondaryScene);

        // Find a secondary screen if present, otherwise use primary
        Screen screen = Screen.getScreens().stream().filter(s -> !s.equals(Screen.getPrimary()))
                .findFirst().orElse(Screen.getPrimary());

        // Set the stage to full screen
        var screenBounds = screen.getBounds();
        scoreboardStage.setX(screenBounds.getMinX());
        scoreboardStage.setY(screenBounds.getMinY());
        scoreboardStage.setWidth(screenBounds.getWidth());
        scoreboardStage.setHeight(screenBounds.getHeight());

        scoreboardLabel.setStyle(String.format(
                "-fx-font-size: %dpx; -fx-font-family: 'Sans-Serif'; -fx-text-alignment: center;"
                , Math.round(screenBounds.getHeight() / 5))
        );

        scoreboardStage.show();
    }

}