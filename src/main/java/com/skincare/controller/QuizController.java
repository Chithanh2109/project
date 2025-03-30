package com.skincare.controller;

import com.skincare.model.Quiz;
import com.skincare.model.QuizResult;
import com.skincare.model.User;
import com.skincare.service.QuizService;
import com.skincare.service.ServiceService;
import com.skincare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {
    
    private final QuizService quizService;
    private final UserService userService;
    private final ServiceService serviceService;
    
    @Autowired
    public QuizController(QuizService quizService, UserService userService, ServiceService serviceService) {
        this.quizService = quizService;
        this.userService = userService;
        this.serviceService = serviceService;
    }
    
    @GetMapping
    public String showQuiz(Model model) {
        Quiz activeQuiz = quizService.getActiveQuiz();
        if (activeQuiz == null) {
            return "redirect:/";
        }
        
        model.addAttribute("quiz", activeQuiz);
        return "quiz/start";
    }
    
    @GetMapping("/{quizId}")
    public String showQuizQuestions(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        if (quiz == null || !quiz.isActive()) {
            return "redirect:/quiz";
        }
        
        model.addAttribute("quiz", quiz);
        return "quiz/questions";
    }
    
    @PostMapping("/{quizId}/submit")
    public String submitQuiz(@PathVariable Long quizId, 
                            @RequestParam Map<String, String> answers,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = null;
        if (authentication != null) {
            currentUser = userService.findByUsername(authentication.getName());
        }
        
        QuizResult result = quizService.processQuizResults(quizId, answers, currentUser);
        redirectAttributes.addFlashAttribute("quizResultId", result.getId());
        return "redirect:/quiz/results/" + result.getId();
    }
    
    @GetMapping("/results/{resultId}")
    public String showQuizResults(@PathVariable Long resultId, Model model) {
        QuizResult result = quizService.getQuizResultById(resultId).orElse(null);
        if (result == null) {
            return "redirect:/quiz";
        }
        
        model.addAttribute("result", result);
        model.addAttribute("recommendedServices", result.getRecommendedServices());
        return "quiz/results";
    }
} 