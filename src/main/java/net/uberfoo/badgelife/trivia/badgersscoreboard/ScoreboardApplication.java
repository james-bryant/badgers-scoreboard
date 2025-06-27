package net.uberfoo.badgelife.trivia.badgersscoreboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScoreboardApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parameters params = getParameters();
        String filename = params.getRaw().getFirst();

        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        List<Category> categories = mapper.readValue(new File(filename), new TypeReference<List<Category>>() {});
        System.out.println(categories);

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