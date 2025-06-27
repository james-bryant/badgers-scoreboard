package net.uberfoo.badgelife.trivia.badgersscoreboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;

public class MainController {

    private static final Preferences preferences = Preferences.userNodeForPackage(MainController.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @FXML private ListView<String> categoryListView;
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

    private final ObjectProperty<ScoreboardController> scoreboardControllerProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Game> gameProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<RoundState> roundStateProperty = new SimpleObjectProperty<>();
    private final StringProperty categoryNameProperty = new SimpleStringProperty();

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

        gameProperty.addListener((observable, oldValue, newValue) -> {
            wagerGridPane.getChildren().clear();
            if (newValue != null) {
                newValue.getTeams().forEach(team -> {
                    var label = new Label(team.getName());
                    var textField = new TextField();
                    wagerGridPane.addRow(wagerGridPane.getRowCount(), label, textField);
                });
            }
        });

        showScores.disableProperty().bind(roundStateProperty.isNull());

        showCategoriesButton.disableProperty().bind(roundStateProperty.isNull()
                .or(roundStateProperty.isNotEqualTo(RoundState.START)));

        selectCategoryButton.disableProperty().bind(roundStateProperty.isNull()
                .or(roundStateProperty.isNotEqualTo(RoundState.CATEGORY_SELECTION))
                .or(Bindings.isEmpty(categoryListView.getSelectionModel().getSelectedItems())));

        enterWagersButton.disableProperty().bind(roundStateProperty.isNull()
                .or(roundStateProperty.isNotEqualTo(RoundState.WAGERING)));
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

        java.io.File file = fileChooser.showSaveDialog(ownerStage);
        if (file != null) {
            mapper.findAndRegisterModules();
            try {
                Files.write(file.toPath(), mapper.writeValueAsBytes(gameProperty.get()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            preferences.put("LAST_SAVE_GAME_PATH", file.getParentFile().getPath());
        }

    }

    @FXML
    protected void onShowScoreboardButton() {
        scoreboardControllerProperty.set(new ScoreboardController(gameProperty, roundStateProperty));
        roundStateProperty.setValue(RoundState.START);
        ownerStage.requestFocus();
    }

    @FXML
    protected void onShowCategoriesButton() {
        gameProperty.get().getQuestionBank().forEach(category -> {
            categoryListView.getItems().add(category.getName());
        });
        roundStateProperty.setValue(RoundState.CATEGORY_SELECTION);
    }

    @FXML
    protected void onSelectCategoryButton() {
        categoryNameProperty.setValue((String) categoryListView.getSelectionModel().getSelectedItem());
        roundStateProperty.setValue(RoundState.WAGERING);
    }
}
