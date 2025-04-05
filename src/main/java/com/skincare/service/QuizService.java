package com.skincare.service;

import com.skincare.model.*;
import com.skincare.dto.SkinConcernStat;
import com.skincare.dto.ServiceStat;
import com.skincare.dto.ResultsByDay;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface QuizService {
    
    /**
     * Get the currently active quiz
     */
    Quiz getActiveQuiz();
    
    /**
     * Get a quiz by its ID
     */
    Optional<Quiz> getQuizById(Long id);
    
    /**
     * Get all quizzes
     */
    List<Quiz> getAllQuizzes();
    
    /**
     * Get a quiz result by its ID
     */
    Optional<QuizResult> getQuizResultById(Long id);
    
    /**
     * Process quiz answers and generate results
     */
    QuizResult processQuizResults(Long quizId, Map<String, String> answers, User user);
    
    /**
     * Get quiz questions for a quiz
     */
    List<QuizQuestion> getQuestionsForQuiz(Long quizId);
    
    /**
     * Get a question by its ID
     */
    Optional<QuizQuestion> getQuestionById(Long id);
    
    /**
     * Get a skin concern by its ID
     */
    Optional<SkinConcern> getSkinConcernById(Long id);
    
    /**
     * Save a new quiz
     */
    Quiz saveQuiz(Quiz quiz);
    
    /**
     * Update an existing quiz
     */
    Quiz updateQuiz(Quiz quiz);
    
    /**
     * Add a question to a quiz
     */
    QuizQuestion addQuestionToQuiz(Long quizId, QuizQuestion question);
    
    /**
     * Add an option to a question
     */
    QuizOption addOptionToQuestion(Long questionId, QuizOption option);
    
    /**
     * Find services based on identified skin concerns
     */
    Set<Service> findRecommendedServices(Set<SkinConcern> concerns);
    
    /**
     * Get a user's quiz results
     */
    List<QuizResult> getQuizResultsByUser(User user);
    
    /**
     * Get all skin concerns
     */
    List<SkinConcern> getAllSkinConcerns();
    
    /**
     * Get total number of quiz results
     */
    long getTotalQuizResults();
    
    /**
     * Get quiz results statistics by day
     */
    List<ResultsByDay> getQuizResultStatsByDay();
    
    /**
     * Get popular skin concerns identified from quiz results
     */
    List<SkinConcernStat> getPopularSkinConcerns();
    
    /**
     * Get popular recommended services from quiz results
     */
    List<ServiceStat> getPopularRecommendedServices();
} 