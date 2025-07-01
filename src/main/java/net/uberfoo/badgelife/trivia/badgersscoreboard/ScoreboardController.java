package net.uberfoo.badgelife.trivia.badgersscoreboard;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Question;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Score;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.util.Map;

public class ScoreboardController {

    private final ObjectProperty<Game> gameProperty;
    private final ObjectProperty<RoundState> roundStateProperty;
    private final StringProperty categoryProperty;
    private final ObjectProperty<Question> questionProperty;
    private final ObjectProperty<Map<Team, Score>> wagersProperty;

    public ScoreboardController(ObjectProperty<Game> gameProperty, ObjectProperty<RoundState> roundStateProperty,
                                StringProperty categoryProperty, ObjectProperty<Question> questionProperty,
                                ObjectProperty<Map<Team, Score>> wagersProperty) {
        this.gameProperty = gameProperty;
        this.roundStateProperty = roundStateProperty;
        this.categoryProperty = categoryProperty;
        this.questionProperty = questionProperty;
        this.wagersProperty = wagersProperty;
        Label scoreboardLabel = new Label("Badgelife Trivia");
        scoreboardLabel.setTextFill(Color.WHITE);

        VBox scoreboardLayout = new VBox(0, scoreboardLabel);
        scoreboardLayout.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        scoreboardLayout.setAlignment(Pos.CENTER);
        Scene secondaryScene = new Scene(scoreboardLayout);

        // Remove window controls
        Stage scoreboardStage = new Stage();
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
        scoreboardStage.setResizable(false);

        scoreboardLabel.setStyle(String.format(
                "-fx-font-size: %dpx; -fx-font-family: 'Roboto Slab'; -fx-font-weight: 500; -fx-text-alignment: center;"
                , Math.round(screenBounds.getHeight() / 5))
        );
        scoreboardLabel.setWrapText(true);

        roundStateProperty.addListener((observable, oldValue, newValue) -> {
            switch  (newValue) {
                case START -> scoreboardLabel.setText("Badgelife Trivia");
                case CATEGORY_SELECTION ->  {
                    scoreboardLabel.setText(String.join("\n",
                        gameProperty.get().getQuestionBank().stream()
                                .filter(category -> !category.getQuestions().isEmpty())
                                .map(Category::getName).toList()
                    ));
                    scoreboardLabel.setStyle(String.format(
                            "-fx-font-size: %dpx; -fx-font-family: 'Roboto Slab'; -fx-font-weight: 500; -fx-text-alignment: center;"
                            , Math.round(screenBounds.getHeight() / 10))
                    );
                }
                case WAGERING -> {
                    scoreboardLabel.setText(categoryProperty.getValue());
                    scoreboardLabel.setStyle(String.format(
                            "-fx-font-color: #ffffff; -fx-font-size: %dpx; -fx-font-family: 'Roboto Slab'; -fx-font-weight: 500; -fx-text-alignment: center;"
                            , Math.round(screenBounds.getHeight() / 5))
                    );
                }
                case QUESTION_SELECTION -> {
                    scoreboardLabel.setText(String.join("\n",
                                wagersProperty.get().entrySet().stream()
                                        .map(entry -> entry.getKey().getName() + ": " + entry.getValue().getWagered())
                                        .toList()
                            ));
                    scoreboardLabel.setStyle(String.format(
                            "-fx-font-size: %dpx; -fx-font-family: 'Roboto Slab'; -fx-font-weight: 500; -fx-text-alignment: center;"
                            , Math.round(screenBounds.getHeight() / 10))
                    );
                }
                case ANSWERING -> {
                    var question = questionProperty.get().getQuestion();
                    scoreboardLabel.setText(question);

                    scoreboardLabel.setStyle(String.format(
                            "-fx-font-size: %dpx; -fx-font-family: 'Roboto Slab'; -fx-font-weight: 500; -fx-text-alignment: center;"
                            , Math.round(screenBounds.getHeight() / 8))
                    );
                }
                case SHOW_SCORES -> {
                    gameProperty.get().getTeams().stream().map(team -> team.getName() + ": " + team.totalScore())
                            .reduce((a, b) -> a + "\n" + b).ifPresent(scoreboardLabel::setText);;
                }

            }
        });

        scoreboardStage.show();
    }

}