package com.skincare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @Column(nullable = false)
    private String questionText;
    
    private Integer orderIndex;
    
    @Enumerated(EnumType.STRING)
    private QuestionType questionType = QuestionType.SINGLE_CHOICE;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<QuizOption> options = new HashSet<>();
    
    public enum QuestionType {
        SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT_INPUT
    }
} 