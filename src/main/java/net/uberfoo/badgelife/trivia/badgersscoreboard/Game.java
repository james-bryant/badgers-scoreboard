package net.uberfoo.badgelife.trivia.badgersscoreboard;

import lombok.Data;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Category;
import net.uberfoo.badgelife.trivia.badgersscoreboard.teams.Team;

import java.util.List;

@Data
public class Game {

    public final List<Team> teams;
    public final List<Category> questionBank;

}
