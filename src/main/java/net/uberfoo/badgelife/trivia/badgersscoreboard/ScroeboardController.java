package net.uberfoo.badgelife.trivia.badgersscoreboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScroeboardController {

    private Stage scoreboardStage = new Stage();

    @FXML
    private Label welcomeText;

    @FXML
    private VBox welcomeBox;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");

        Label secondaryLabel = new Label("Bagelife Trivia");

        VBox secondaryLayout = new VBox(10, secondaryLabel);
        secondaryLayout.setAlignment(Pos.CENTER);
        Scene secondaryScene = new Scene(secondaryLayout);

        scoreboardStage.initStyle(StageStyle.UNDECORATED); // Remove window controls
        scoreboardStage.setScene(secondaryScene);

        // Set the stage to full screen
        var screenBounds = Screen.getPrimary().getBounds();
        scoreboardStage.setX(screenBounds.getMinX());
        scoreboardStage.setY(screenBounds.getMinY());
        scoreboardStage.setWidth(screenBounds.getWidth());
        scoreboardStage.setHeight(screenBounds.getHeight());

        secondaryLabel.setStyle(String.format(
                "-fx-font-size: %dpx; -fx-font-family: 'Sans-Serif'; -fx-text-alignment: center;"
                , Math.round(screenBounds.getHeight() / 5))
        );

        scoreboardStage.show();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/uberfoo/badgelife/trivia/badgersscoreboard/categories-view.fxml"));
        Scene categoriesScene = new Scene(loader.load());

        ((Stage)welcomeBox.getScene().getWindow()).setScene(categoriesScene);

    }
}