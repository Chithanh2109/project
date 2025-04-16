package com.skincare.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skincare.dto.ResultsByDay;
import com.skincare.dto.ServiceStat;
import com.skincare.dto.SkinConcernStat;
import com.skincare.model.Quiz;
import com.skincare.model.QuizOption;
import com.skincare.model.QuizQuestion;
import com.skincare.model.QuizResult;
import com.skincare.model.SkinConcern;
import com.skincare.model.User;
import com.skincare.repository.QuizOptionRepository;
import com.skincare.repository.QuizQuestionRepository;
import com.skincare.repository.QuizRepository;
import com.skincare.repository.QuizResultRepository;
import com.skincare.repository.ServiceRepository;
import com.skincare.repository.SkinConcernRepository;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizOptionRepository optionRepository;
    private final QuizResultRepository resultRepository;
    private final SkinConcernRepository concernRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository,
                          QuizQuestionRepository questionRepository,
                          QuizOptionRepository optionRepository,
                          QuizResultRepository resultRepository,
                          SkinConcernRepository concernRepository,
                          ServiceRepository serviceRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.resultRepository = resultRepository;
        this.concernRepository = concernRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Quiz getActiveQuiz() {
        return quizRepository.findByActiveTrue().orElse(null);
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    public Optional<QuizResult> getQuizResultById(Long id) {
        return resultRepository.findById(id);
    }

    @Override
    public Optional<QuizQuestion> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public Optional<SkinConcern> getSkinConcernById(Long id) {
        return concernRepository.findById(id);
    }

    @Override
    @Transactional
    public QuizResult processQuizResults(Long quizId, Map<String, String> answers, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại: " + quizId));
        
        // Phân tích câu trả lời
        Map<SkinConcern, Integer> concernScores = new HashMap<>();
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String questionIdStr = entry.getKey().replace("answer-", "");
            try {
                Long questionId = Long.parseLong(questionIdStr);
                Long optionId = Long.parseLong(entry.getValue());
                
                QuizOption option = optionRepository.findById(optionId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy lựa chọn: " + optionId));
                
                // Xác định các vấn đề về da dựa trên câu trả lời
                // Mỗi lựa chọn sẽ có các vấn đề về da liên quan và điểm số
                if (option.getSkinConcerns() != null && !option.getSkinConcerns().isEmpty()) {
                    for (SkinConcern concern : option.getSkinConcerns()) {
                        concernScores.put(concern, concernScores.getOrDefault(concern, 0) + option.getWeight());
                    }
                }
            } catch (NumberFormatException e) {
                // Bỏ qua các tham số không phải câu trả lời
            }
        }
        
        // Xác định các vấn đề về da có điểm cao nhất
        Set<SkinConcern> identifiedConcerns = concernScores.entrySet().stream()
                .filter(entry -> entry.getValue() >= 2) // Chọn vấn đề với điểm số >= 2
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        
        // Tìm các dịch vụ phù hợp
        Set<com.skincare.model.Service> recommendedServices = findRecommendedServices(identifiedConcerns);
        
        // Tạo bản tóm tắt kết quả
        StringBuilder summary = new StringBuilder();
        summary.append("Dựa trên các câu trả lời của bạn, chúng tôi đã xác định các vấn đề về da sau:\n");
        
        for (SkinConcern concern : identifiedConcerns) {
            // Sử dụng name() của enum thay vì getName()
            summary.append("- ").append(concern.name()).append(": ").append(concern.getDescription()).append("\n");
        }
        
        summary.append("\nChúng tôi đề xuất các dịch vụ sau để giải quyết các vấn đề trên:\n");
        
        for (com.skincare.model.Service service : recommendedServices) {
            // Sử dụng description thay vì getShortDescription()
            summary.append("- ").append(service.getName()).append(": ").append(service.getDescription()).append("\n");
        }
        
        // Lưu kết quả
        QuizResult result = new QuizResult();
        result.setQuiz(quiz);
        result.setUser(user);
        result.setIdentifiedConcerns(identifiedConcerns);
        result.setRecommendedServices(recommendedServices);
        result.setResultSummary(summary.toString());
        result.setCompletedAt(LocalDateTime.now());
        
        return resultRepository.save(result);
    }

    @Override
    public List<QuizQuestion> getQuestionsForQuiz(Long quizId) {
        return questionRepository.findByQuizIdOrderByOrderIndexAsc(quizId);
    }

    @Override
    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        quiz.setCreatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public Quiz updateQuiz(Quiz quiz) {
        quiz.setUpdatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public QuizQuestion addQuestionToQuiz(Long quizId, QuizQuestion question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại: " + quizId));
        
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public QuizOption addOptionToQuestion(Long questionId, QuizOption option) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại: " + questionId));
        
        option.setQuestion(question);
        return optionRepository.save(option);
    }

    @Override
    public Set<com.skincare.model.Service> findRecommendedServices(Set<SkinConcern> concerns) {
        if (concerns.isEmpty()) {
            return new HashSet<>();
        }
        
        // Sửa đổi phương thức để không sử dụng getId() vì SkinConcern là enum
        // Lấy tất cả services và lọc theo concerns
        List<com.skincare.model.Service> allServices = serviceRepository.findAll();
        
        return allServices.stream()
            .filter(service -> 
                service.getSkinConcerns() != null && 
                service.getSkinConcerns().stream()
                    .anyMatch(concerns::contains))
            .collect(Collectors.toSet());
    }

    @Override
    public List<QuizResult> getQuizResultsByUser(User user) {
        return resultRepository.findByUserOrderByCompletedAtDesc(user);
    }

    @Override
    public List<SkinConcern> getAllSkinConcerns() {
        return concernRepository.findAll();
    }
    
    @Override
    public long getTotalQuizResults() {
        return resultRepository.count();
    }
    
    @Override
    public List<ResultsByDay> getQuizResultStatsByDay() {
        Map<LocalDate, Long> resultsByDay = resultRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        result -> result.getCompletedAt().toLocalDate(),
                        Collectors.counting()));
        
        return resultsByDay.entrySet().stream()
                .map(entry -> new ResultsByDay(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ResultsByDay::getDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SkinConcernStat> getPopularSkinConcerns() {
        // Lấy tất cả kết quả trắc nghiệm
        List<QuizResult> allResults = resultRepository.findAll();
        long totalResults = allResults.size();
        
        if (totalResults == 0) {
            return new ArrayList<>();
        }
        
        // Đếm số lần mỗi vấn đề về da xuất hiện
        Map<SkinConcern, Long> concernCounts = new HashMap<>();
        
        for (QuizResult result : allResults) {
            for (SkinConcern concern : result.getIdentifiedConcerns()) {
                concernCounts.put(concern, concernCounts.getOrDefault(concern, 0L) + 1);
            }
        }
        
        // Chuyển đổi thành danh sách thống kê
        return concernCounts.entrySet().stream()
                .map(entry -> {
                    double percentage = (entry.getValue() * 100.0) / totalResults;
                    return new SkinConcernStat(
                            entry.getKey().name(), // Sử dụng name() thay vì getName()
                            entry.getValue(), 
                            Math.round(percentage * 10) / 10.0);
                })
                .sorted((stat1, stat2) -> Long.compare(stat2.getCount(), stat1.getCount()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ServiceStat> getPopularRecommendedServices() {
        // Lấy tất cả kết quả trắc nghiệm
        List<QuizResult> allResults = resultRepository.findAll();
        long totalResults = allResults.size();
        
        if (totalResults == 0) {
            return new ArrayList<>();
        }
        
        // Đếm số lần mỗi dịch vụ được đề xuất
        Map<com.skincare.model.Service, Long> serviceCounts = new HashMap<>();
        
        for (QuizResult result : allResults) {
            for (com.skincare.model.Service service : result.getRecommendedServices()) {
                serviceCounts.put(service, serviceCounts.getOrDefault(service, 0L) + 1);
            }
        }
        
        // Chuyển đổi thành danh sách thống kê
        return serviceCounts.entrySet().stream()
                .map(entry -> {
                    Long count = entry.getValue();
                    int bookingCount = count.intValue();
                    return new ServiceStat(
                            entry.getKey().getId(),
                            entry.getKey().getName(), 
                            bookingCount,
                            BigDecimal.ZERO // Không có thông tin doanh thu
                    );
                })
                .sorted((stat1, stat2) -> Integer.compare(stat2.getBookingCount(), stat1.getBookingCount()))
                .collect(Collectors.toList());
    }
} 