package com.skincare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quiz_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuizQuestion question;
    
    @Column(nullable = false)
    private String optionText;
    
    // Trọng số của lựa chọn này khi đánh giá các vấn đề về da
    private Integer weight = 1;
    
    @ManyToMany
    @JoinTable(
        name = "option_skin_concern",
        joinColumns = @JoinColumn(name = "option_id"),
        inverseJoinColumns = @JoinColumn(name = "skin_concern_id")
    )
    private Set<SkinConcern> skinConcerns = new HashSet<>();
} 