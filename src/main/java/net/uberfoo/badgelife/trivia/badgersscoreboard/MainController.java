package net.uberfoo.badgelife.trivia.badgersscoreboard;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.io.IOException;

public class MainController {

    @FXML
    private Button showScoreboardButton;

    @FXML
    private Button loadGameButton;

    @FXML
    private Button createGameButton;

    @FXML
    private Tab gameTab;

    @FXML
    private TextArea gameTextArea;

    @Setter
    private Stage ownerStage;

    private final ObjectProperty<ScoreboardController> scoreboardControllerProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Game> gameProperty = new SimpleObjectProperty<>();

    @FXML
    protected void initialize() {
        gameTab.disableProperty().bind(gameProperty.isNull());

        gameProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                var teams = String.join(", ",
                        newValue.getTeams().stream().map(Team::getName).toArray(String[]::new));
                gameTextArea.setText(String.format("""
                        Teams: %s
                        Categories: %d
                        Questions: %d
                        """, teams, newValue.getQuestionBank().size(),
                        newValue.getQuestionBank().stream().mapToInt(c -> c.getQuestions().size()).sum()));
            }
        });

        showScoreboardButton.disableProperty().bind(scoreboardControllerProperty.isNotNull());
    }

    @FXML
    protected void onCreateGameButton() {
        CreateGameDialog dialog = new CreateGameDialog(ownerStage);
        dialog.showAndWait().ifPresent(gameProperty::set);
    }

    @FXML
    protected void onShowScoreboardButton() {
        scoreboardControllerProperty.set(new ScoreboardController(gameProperty));
        ownerStage.requestFocus();
    }
}
