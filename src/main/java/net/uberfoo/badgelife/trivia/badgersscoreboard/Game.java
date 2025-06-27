package net.uberfoo.badgelife.trivia.badgersscoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Game {

    private List<Team> teams;
    private List<Category> questionBank;

    public Game() {
        teams = new ArrayList<>();
        questionBank = new ArrayList<>();
    }

}
