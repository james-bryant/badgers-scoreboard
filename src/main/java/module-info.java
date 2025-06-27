module net.uberfoo.badgelife.trivia.badgersscoreboard {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires lombok;
    requires java.prefs;

    opens net.uberfoo.badgelife.trivia.badgersscoreboard to javafx.fxml;
    opens net.uberfoo.badgelife.trivia.badgersscoreboard.questions to com.fasterxml.jackson.databind;
    opens net.uberfoo.badgelife.trivia.badgersscoreboard.teams to com.fasterxml.jackson.databind;
    exports net.uberfoo.badgelife.trivia.badgersscoreboard;
}