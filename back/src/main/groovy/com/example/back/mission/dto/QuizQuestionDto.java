package com.example.back.mission.dto;
import com.example.back.domain.QuizQuestion;
import lombok.Getter;
import java.util.List;
@Getter
public class QuizQuestionDto {
    private Long id;
    private String category;
    private String questionText;
    private List<String> options;
    public QuizQuestionDto(QuizQuestion entity) {
        this.id = entity.getId();
        this.category = entity.getCategory();
        this.questionText = entity.getQuestionText();
        this.options = entity.getOptions();
    }
}