package net.uberfoo.badgelife.trivia.badgersscoreboard.teams;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

import java.util.List;

@Data
public class Team {

    private static final int STARTING_SCORE = 1000;

    private final String name;

    private final List<Score> scores;

    public int getTotalScore() {
        return scores.stream()
                .mapToInt(Score::getScore)
                .sum() + STARTING_SCORE;
    }

    public void addScore(Score score) {
        scores.add(score);
    }

}
