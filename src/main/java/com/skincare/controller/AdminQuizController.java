package com.skincare.controller;

import com.skincare.model.*;
import com.skincare.service.QuizService;
import com.skincare.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/quiz")
public class AdminQuizController {
    
    private final QuizService quizService;
    private final ServiceService serviceService;
    
    @Autowired
    public AdminQuizController(QuizService quizService, ServiceService serviceService) {
        this.quizService = quizService;
        this.serviceService = serviceService;
    }
    
    @GetMapping
    public String quizList(Model model) {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        return "admin/quiz/list";
    }
    
    @GetMapping("/create")
    public String createQuizForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "admin/quiz/edit";
    }
    
    @GetMapping("/{id}")
    public String editQuizForm(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại: " + id));
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quizService.getQuestionsForQuiz(id));
        return "admin/quiz/edit";
    }
    
    @PostMapping
    public String saveQuiz(@ModelAttribute Quiz quiz, RedirectAttributes redirectAttributes) {
        try {
            Quiz savedQuiz = quizService.saveQuiz(quiz);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu bài trắc nghiệm thành công!");
            return "redirect:/admin/quiz/" + savedQuiz.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu bài trắc nghiệm: " + e.getMessage());
            return "redirect:/admin/quiz";
        }
    }
    
    @PostMapping("/{quizId}/questions")
    public String addQuestion(@PathVariable Long quizId, 
                             @ModelAttribute QuizQuestion question,
                             RedirectAttributes redirectAttributes) {
        try {
            quizService.addQuestionToQuiz(quizId, question);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm câu hỏi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm câu hỏi: " + e.getMessage());
        }
        return "redirect:/admin/quiz/" + quizId;
    }
    
    @GetMapping("/{quizId}/questions/{questionId}")
    public String editQuestion(@PathVariable Long quizId, 
                              @PathVariable Long questionId,
                              Model model) {
        QuizQuestion question = quizService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại: " + questionId));
        
        model.addAttribute("quiz", quizService.getQuizById(quizId).orElseThrow());
        model.addAttribute("question", question);
        model.addAttribute("skinConcerns", quizService.getAllSkinConcerns());
        return "admin/quiz/question";
    }
    
    @PostMapping("/{quizId}/questions/{questionId}/options")
    public String addOption(@PathVariable Long quizId, 
                           @PathVariable Long questionId,
                           @ModelAttribute QuizOption option,
                           @RequestParam(required = false) List<Long> concernIds,
                           RedirectAttributes redirectAttributes) {
        try {
            // Thêm các vấn đề về da liên quan nếu có
            if (concernIds != null && !concernIds.isEmpty()) {
                for (Long concernId : concernIds) {
                    SkinConcern concern = quizService.getSkinConcernById(concernId)
                            .orElseThrow(() -> new RuntimeException("Vấn đề về da không tồn tại: " + concernId));
                    option.getSkinConcerns().add(concern);
                }
            }
            
            quizService.addOptionToQuestion(questionId, option);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm lựa chọn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm lựa chọn: " + e.getMessage());
        }
        return "redirect:/admin/quiz/" + quizId + "/questions/" + questionId;
    }
    
    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Quiz quiz = quizService.getQuizById(id).orElseThrow();
            quiz.setActive(!quiz.isActive());
            quizService.updateQuiz(quiz);
            
            String status = quiz.isActive() ? "kích hoạt" : "vô hiệu hóa";
            redirectAttributes.addFlashAttribute("successMessage", "Đã " + status + " bài trắc nghiệm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/quiz";
    }
    
    @GetMapping("/reports")
    public String quizReports(Model model) {
        // Thống kê kết quả trắc nghiệm
        model.addAttribute("totalResults", quizService.getTotalQuizResults());
        model.addAttribute("resultsByDay", quizService.getQuizResultStatsByDay());
        model.addAttribute("popularConcerns", quizService.getPopularSkinConcerns());
        model.addAttribute("recommendedServices", quizService.getPopularRecommendedServices());
        
        return "admin/quiz/reports";
    }
} 