package com.skincare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skincare.model.Quiz;
import com.skincare.model.QuizQuestion;
import com.skincare.model.QuizResult;
import com.skincare.model.User;
import com.skincare.service.QuizService;
import com.skincare.service.ServiceService;
import com.skincare.service.UserService;

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
    
    @GetMapping("/{id}")
    public String showQuiz(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Quiz quiz = quizService.getQuizById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài trắc nghiệm"));
            
            if (!quiz.getActive()) {
                return "redirect:/quizzes";
            }
            
            List<QuizQuestion> questions = quizService.getQuestionsForQuiz(id);
            
            model.addAttribute("quiz", quiz);
            model.addAttribute("questions", questions);
            
            return "quiz/take-quiz";
        } catch (Exception e) {
            return "redirect:/quizzes";
        }
    }
    
    @PostMapping("/{id}/submit")
    public String submitQuiz(@PathVariable Long id, 
                             @RequestParam Map<String, String> formData,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        try {
            User user = null;
            if (authentication != null) {
                user = userService.findByUsername(authentication.getName())
                        .orElse(null);
            }
            
            QuizResult result = quizService.processQuizResults(id, formData, user);
            
            return "redirect:/quiz/results/" + result.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/quiz/" + id;
        }
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