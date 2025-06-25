package net.uberfoo.badgelife.trivia.badgersscoreboard;

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

    @Setter
    private Stage ownerStage;

    private final Stage scoreboardStage = new Stage();

    public ScoreboardController() {
        Label scoreboardLabel = new Label("Bagelife Trivia");

        VBox scoreboardLayout = new VBox(10, scoreboardLabel);
        scoreboardLayout.setAlignment(Pos.CENTER);
        Scene secondaryScene = new Scene(scoreboardLayout);

        // Remove window controls
        scoreboardStage.initStyle(StageStyle.UNDECORATED);

        // Add starting scene
        scoreboardStage.setScene(secondaryScene);

        // Set the stage to full screen
        var screenBounds = Screen.getPrimary().getBounds();
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