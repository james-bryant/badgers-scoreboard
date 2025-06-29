package net.uberfoo.badgelife.trivia.badgersscoreboard.teams;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Team {

    private static final int STARTING_SCORE = 1000;

    private String name;
    private List<Score> scores;

    public Team() {
        this.scores = new java.util.ArrayList<>();
    }

    public int totalScore() {
        return scores.stream()
                .mapToInt(Score::score)
                .sum() + STARTING_SCORE;
    }

    public void addScore(Score score) {
        scores.add(score);
    }

}
