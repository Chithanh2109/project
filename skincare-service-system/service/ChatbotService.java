package com.skincare.service;

import com.skincare.model.ChatMessage;
import com.skincare.model.ChatSession;
import com.skincare.repository.ChatMessageRepository;
import com.skincare.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final NotificationService notificationService;

    @Transactional
    public ChatSession createSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setStatus("ACTIVE");
        session.setCreatedAt(LocalDateTime.now());
        session.setLastMessageAt(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    @Transactional
    public ChatMessage sendMessage(Long sessionId, String content, boolean isUser) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên chat"));

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setContent(content);
        message.setIsUser(isUser);
        message.setCreatedAt(LocalDateTime.now());

        session.setLastMessageAt(LocalDateTime.now());
        sessionRepository.save(session);

        return messageRepository.save(message);
    }

    @Transactional
    public ChatMessage processUserMessage(Long sessionId, String content) {
        // Lưu tin nhắn của người dùng
        ChatMessage userMessage = sendMessage(sessionId, content, true);

        // Xử lý tin nhắn và tạo phản hồi
        String response = generateResponse(content);
        return sendMessage(sessionId, response, false);
    }

    private String generateResponse(String userMessage) {
        // TODO: Tích hợp với AI/ML để tạo phản hồi thông minh
        // Hiện tại chỉ xử lý các câu hỏi cơ bản
        userMessage = userMessage.toLowerCase();
        
        if (userMessage.contains("giờ làm việc")) {
            return "Chúng tôi làm việc từ 8:00 - 20:00 các ngày trong tuần.";
        } else if (userMessage.contains("giá dịch vụ")) {
            return "Bạn có thể xem bảng giá dịch vụ tại: /services";
        } else if (userMessage.contains("đặt lịch")) {
            return "Bạn có thể đặt lịch trực tiếp trên website hoặc gọi điện thoại cho chúng tôi.";
        } else if (userMessage.contains("hủy lịch")) {
            return "Để hủy lịch, vui lòng liên hệ với chúng tôi trước ít nhất 24 giờ.";
        } else if (userMessage.contains("cảm ơn")) {
            return "Cảm ơn bạn đã liên hệ với chúng tôi. Nếu cần hỗ trợ thêm, đừng ngần ngại liên hệ.";
        } else {
            return "Xin lỗi, tôi không hiểu câu hỏi của bạn. Vui lòng liên hệ nhân viên tư vấn để được hỗ trợ.";
        }
    }

    @Transactional
    public void transferToHuman(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên chat"));

        session.setStatus("WAITING_HUMAN");
        sessionRepository.save(session);

        // Thông báo cho admin
        notificationService.sendSystemNotification(
                1L, // Admin ID
                "Yêu cầu hỗ trợ mới",
                "Có yêu cầu hỗ trợ mới từ khách hàng trong phiên chat #" + sessionId
        );
    }

    @Transactional
    public void closeSession(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên chat"));

        session.setStatus("CLOSED");
        session.setClosedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    public List<ChatMessage> getSessionMessages(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên chat"));

        return messageRepository.findBySessionOrderByCreatedAtAsc(session);
    }

    public List<ChatSession> getUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByLastMessageAtDesc(userId);
    }

    public List<ChatSession> getActiveSessions() {
        return sessionRepository.findByStatusOrderByLastMessageAtDesc("ACTIVE");
    }

    public List<ChatSession> getWaitingSessions() {
        return sessionRepository.findByStatusOrderByLastMessageAtDesc("WAITING_HUMAN");
    }

    public Map<String, Object> getChatStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        List<ChatSession> sessions = sessionRepository.findByCreatedAtBetween(startDate, endDate);

        // Thống kê theo trạng thái
        Map<String, Long> statusCount = new HashMap<>();
        statusCount.put("ACTIVE", sessions.stream()
                .filter(s -> "ACTIVE".equals(s.getStatus()))
                .count());
        statusCount.put("WAITING_HUMAN", sessions.stream()
                .filter(s -> "WAITING_HUMAN".equals(s.getStatus()))
                .count());
        statusCount.put("CLOSED", sessions.stream()
                .filter(s -> "CLOSED".equals(s.getStatus()))
                .count());

        // Thống kê số lượng tin nhắn
        long totalMessages = messageRepository.countBySession_CreatedAtBetween(startDate, endDate);

        statistics.put("totalSessions", sessions.size());
        statistics.put("statusCount", statusCount);
        statistics.put("totalMessages", totalMessages);

        return statistics;
    }
} 