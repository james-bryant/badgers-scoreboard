package net.uberfoo.badgelife.trivia.badgersscoreboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

public class MainController {

    @Setter
    private Stage ownerStage;

    private ScoreboardController scoreboardController;
    private Game game;

    @FXML
    protected void onCreateGameButton() throws IOException {
        CreateGameDialog dialog = new CreateGameDialog(ownerStage);
        game = dialog.showAndWait().orElseThrow();
    }

}
