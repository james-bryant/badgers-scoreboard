package net.uberfoo.badgelife.trivia.badgersscoreboard.teams;

import lombok.Data;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Question;

@Data
public class Score {

    private final boolean correct;
    private final Question question;
    private final int wagered;

    public int getScore() {
        if (correct) {
            return Math.round(question.getPayout() * wagered);
        }
        return -wagered;
    }

}
