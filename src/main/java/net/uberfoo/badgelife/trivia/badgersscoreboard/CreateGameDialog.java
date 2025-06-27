package net.uberfoo.badgelife.trivia.badgersscoreboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

public class CreateGameDialog extends Dialog<Game> {

    private static final Preferences preferences = Preferences.userNodeForPackage(CreateGameDialog.class);

    @FXML
    private Label questionsFileLabel;

    @FXML
    private ListView<String> teamsList;

    @FXML
    private Button chooseQuestionsFileButton;

    @FXML
    private Button addTeamButton;

    @FXML
    private Button removeTeamButton;

    @FXML
    private TextField teamTextBox;

    @FXML
    private ButtonType createButton;

    public CreateGameDialog(Stage ownerStage) {

        // Load the FXML layout for the dialog
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("create-game-dialog.fxml"));
        fxmlLoader.setController(this);
        try {
            setDialogPane(fxmlLoader.load());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        chooseQuestionsFileButton.setOnAction(e -> { onChooseFileButton(); });
        addTeamButton.setOnAction(e -> { onAddTeamButton(); });
        removeTeamButton.setOnAction(e -> { onRemoveTeamButton(); });

        setResultConverter(x -> {
            if (x == createButton) {
                // The create button was pressed, return the game object
                var mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                List<Category> categories = null;
                try {
                    categories = mapper.readValue(new File(questionsFileLabel.getText()), new TypeReference<List<Category>>() {});
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println(categories);

                List<Team> teams = new LinkedList<>();
                for (String teamName : teamsList.getItems()) {
                    if (teamName.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Team names cannot be empty.");
                        alert.showAndWait();
                    }
                    teams.add(new Team(teamName, new ArrayList<>()));
                }
                return new Game(teams, categories);
            } else {
                // If any other button is pressed, return null
                return null;
            }
        });

        getDialogPane().lookupButton(createButton).disableProperty()
                .bind(Bindings.isEmpty(teamsList.getItems()).or(questionsFileLabel.textProperty().isEmpty()));

        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        setTitle("Create Game");
        setHeaderText("Create a new game");
        initOwner(ownerStage);
    }

    protected void onChooseFileButton() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select File");
        fileChooser.initialDirectoryProperty()
                .setValue(Path.of(preferences.get("LAST_PATH", System.getProperty("user.home"))).toFile());

        java.io.File file = fileChooser.showOpenDialog(getOwner());
        if (file != null) {
            questionsFileLabel.setText(file.getAbsolutePath());
            preferences.put("LAST_PATH", file.getParentFile().getPath());
        }

    }

    @FXML
    protected void onAddTeamButton() {
        teamsList.getItems().add(teamTextBox.getText());
    }

    @FXML
    protected void onRemoveTeamButton() {
        teamsList.getItems().remove(teamsList.getSelectionModel().getSelectedItem());
    }
}
