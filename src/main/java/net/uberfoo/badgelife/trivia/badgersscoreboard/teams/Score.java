package net.uberfoo.badgelife.trivia.badgersscoreboard.teams;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.uberfoo.badgelife.trivia.badgersscoreboard.questions.Question;

@Data
@AllArgsConstructor
public class Score {

    private boolean correct;
    private Question question;
    private int wagered;

    public Score() {}

    public int score() {
        if (correct) {
            return Math.round(question.getPayout() * wagered);
        }
        return -wagered;
    }

}
