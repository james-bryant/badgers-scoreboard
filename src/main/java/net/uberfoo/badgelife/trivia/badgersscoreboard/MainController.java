package net.uberfoo.badgelife.trivia.badgersscoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Question;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Score;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;
import java.util.prefs.Preferences;

public class MainController {

    private static final Preferences preferences = Preferences.userNodeForPackage(MainController.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @FXML private TextArea questionTextArea;
    @FXML private GridPane scoringPane;
    @FXML private TextField saveGameTextField;
    @FXML private ListView<Category> categoryListView;
    @FXML private Button showScores;
    @FXML private GridPane wagerGridPane1;
    @FXML private Button showQuestionButton;
    @FXML private Button enterWagersButton;
    @FXML private Button selectCategoryButton;
    @FXML private Button showCategoriesButton;
    @FXML private GridPane wagerGridPane;
    @FXML private Button showScoreboardButton;
    @FXML private Button loadGameButton;
    @FXML private Button createGameButton;
    @FXML private Button saveGameButton;
    @FXML private Tab gameTab;
    @FXML private TextArea gameTextArea;

    @Setter
    private Stage ownerStage;

    private ToggleGroup scoringToggleGroup;

    private final ObjectProperty<ScoreboardController> scoreboardControllerProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Game> gameProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<RoundState> roundStateProperty = new SimpleObjectProperty<>();
    private final StringProperty categoryNameProperty = new SimpleStringProperty();
    private final ObjectProperty<Question> questionProperty = new SimpleObjectProperty<>();

    @FXML
    protected void initialize() {
        gameTab.disableProperty().bind(gameProperty.isNull());

        gameProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                wagerGridPane.getChildren().clear();
                scoringPane.getChildren().clear();
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

        showScores.disableProperty().bind(roundStateProperty.isNotEqualTo(RoundState.ANSWERING));

        showQuestionButton.disableProperty().bind(roundStateProperty.isNotEqualTo(RoundState.QUESTION_SELECTION));

        showCategoriesButton.disableProperty().bind(roundStateProperty.isNull()
                .or(roundStateProperty.isNotEqualTo(RoundState.START)
                        .and(roundStateProperty.isNotEqualTo(RoundState.SHOW_SCORES))));

        selectCategoryButton.disableProperty().bind(roundStateProperty.isNull()
                .or(roundStateProperty.isNotEqualTo(RoundState.CATEGORY_SELECTION))
                .or(Bindings.isEmpty(categoryListView.getSelectionModel().getSelectedItems())));

        enterWagersButton.disableProperty().bind(roundStateProperty.isNull()
                .or(roundStateProperty.isNotEqualTo(RoundState.WAGERING)));

        saveGameButton.disableProperty().bind(gameProperty.isNull());

        categoryListView.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        scoringToggleGroup = new ToggleGroup();
    }

    @FXML
    protected void onCreateGameButton() {
        CreateGameDialog dialog = new CreateGameDialog(ownerStage);
        dialog.showAndWait().ifPresent(gameProperty::set);
    }

    @FXML
    protected void onLoadGameButton() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select File");
        fileChooser.initialDirectoryProperty()
                .setValue(Path.of(preferences.get("LAST_LOAD_GAME_PATH", System.getProperty("user.home"))).toFile());

        File file = fileChooser.showOpenDialog(ownerStage);
        if (file != null) {
            preferences.put("LAST_LOAD_GAME_PATH", file.getParentFile().getPath());
            try {
                gameProperty.set(mapper.readValue(file, Game.class));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(gameProperty.get());

        }

    }

    @FXML
    protected void onSaveGameButton() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select File");
        fileChooser.initialDirectoryProperty()
                .setValue(Path.of(preferences.get("LAST_SAVE_GAME_PATH", System.getProperty("user.home"))).toFile());

        File file = fileChooser.showSaveDialog(ownerStage);
        if (file != null) {
            try {
                Files.write(file.toPath(), mapper.writeValueAsBytes(gameProperty.get()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            saveGameTextField.setText(file.getAbsolutePath());
            saveGameTextField.setUserData(file);
            preferences.put("LAST_SAVE_GAME_PATH", file.getParentFile().getPath());
        }

    }

    @FXML
    protected void onShowScoreboardButton() {
        scoreboardControllerProperty.set(new ScoreboardController(gameProperty, roundStateProperty, categoryNameProperty, questionProperty));
        roundStateProperty.setValue(RoundState.START);
        ownerStage.requestFocus();
    }

    @FXML
    protected void onShowCategoriesButton() {
        categoryListView.getItems().clear();
        gameProperty.get().getQuestionBank()
                .forEach(category -> categoryListView.getItems().add(category));
        roundStateProperty.setValue(RoundState.CATEGORY_SELECTION);
    }

    @FXML
    protected void onSelectCategoryButton() {
        categoryNameProperty.setValue(categoryListView.getSelectionModel().getSelectedItem().getName());
        wagerGridPane.getChildren().clear();
        gameProperty.get().getTeams().forEach(team -> {
            if (team.totalScore() > 0) {
                var wagerLabel = new Label(team.getName());
                var textField = new TextField();
                textField.setUserData(team);
                wagerLabel.setUserData(team);
                wagerGridPane.addRow(wagerGridPane.getRowCount(), wagerLabel, textField);
            }
        });

        roundStateProperty.setValue(RoundState.WAGERING);
    }

    @FXML
    protected void onEnterWagersButton() {
        List<Question> questions = categoryListView.getSelectionModel().getSelectedItem().getQuestions();
        SecureRandom random = new SecureRandom();
        int i = random.nextInt(questions.size());
        var question = questions.get(i);
        wagerGridPane.getChildren().stream().filter(n -> n instanceof TextField).map(n -> (TextField)n)
                .forEach(tf -> {
            var team = (Team) tf.getUserData();
            try {
                var wager = Integer.parseInt(tf.getText());
                if (wager < 0) {
                    throw new NumberFormatException("Wager cannot be negative");
                }
                if (wager > team.totalScore()) {
                    throw new NumberFormatException("Wager cannot be greater than team's total score");
                }
                var score = new Score(false, question, wager);
                team.addScore(score);

                var scoringRadioButton = new RadioButton(team.getName());
                scoringRadioButton.setUserData(score);
                scoringRadioButton.setToggleGroup(scoringToggleGroup);
                scoringPane.addRow(scoringPane.getRowCount(), scoringRadioButton);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Invalid wager for team " + team.getName() + ": " + ex.getMessage());
                alert.showAndWait();
                throw ex;
            }
        });
        var noScoreRadioButton = new RadioButton("--No Score--");
        noScoreRadioButton.setUserData(null);
        noScoreRadioButton.setToggleGroup(scoringToggleGroup);
        scoringPane.addRow(scoringPane.getRowCount(), noScoreRadioButton);
        questions.remove(i);

        saveGame();

        questionTextArea.setText(question.getQuestion());
        questionProperty.set(question);
        roundStateProperty.setValue(RoundState.QUESTION_SELECTION);
    }

    @FXML
    protected void onShowQuestion() {
        questionTextArea.setText(
                "Question: " + questionProperty.get().getQuestion() + "\n" +
                "Answer: " + questionProperty.get().getAnswer());
        roundStateProperty.setValue(RoundState.ANSWERING);
    }

    @FXML
    protected void onShowScores() {
        var selectedToggle = scoringToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No team selected! Are you sure?");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                saveGame();
                scoringPane.getChildren().clear();
                scoringToggleGroup = new ToggleGroup();
                roundStateProperty.setValue(RoundState.SHOW_SCORES);
            }
            return;
        }

        var score = (Score) selectedToggle.getUserData();
        score.setCorrect(true);
        saveGame();

        scoringPane.getChildren().clear();
        scoringToggleGroup = new ToggleGroup();
        roundStateProperty.setValue(RoundState.SHOW_SCORES);
    }


    private void saveGame() {
        // Persist game state if save path exists
        File file;
        if ((file = (File)saveGameTextField.getUserData()) != null) {
            try {
                Files.write(file.toPath(), mapper.writeValueAsBytes(gameProperty.get()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
