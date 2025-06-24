package net.uberfoo.badgelife.trivia.badgersscoreboard.questions;

import lombok.Data;

@Data
public class Question {

    private String question;
    private String answer;
    private float payout;
    private Question followupQuestion;

}
