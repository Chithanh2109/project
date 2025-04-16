package com.skincare.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    @ElementCollection
    @CollectionTable(
        name = "option_skin_concern",
        joinColumns = @JoinColumn(name = "option_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "skin_concern")
    private Set<SkinConcern> skinConcerns = new HashSet<>();
} 