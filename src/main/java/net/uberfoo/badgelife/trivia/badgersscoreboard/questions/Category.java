package net.uberfoo.badgelife.trivia.badgersscoreboard.questions;

import lombok.Data;

import java.util.List;

@Data
public class Category {

    private String name;
    private List<Question> questions;
    
}
