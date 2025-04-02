package com.skincare.service;

import com.skincare.model.Specialist;
import com.skincare.model.SpecialistSchedule;
import com.skincare.repository.SpecialistRepository;
import com.skincare.repository.SpecialistScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class SpecialistScheduleService {

    private final SpecialistRepository specialistRepository;
    private final SpecialistScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    @Transactional
    public SpecialistSchedule createSchedule(Long specialistId, DayOfWeek dayOfWeek,
                                          LocalTime startTime, LocalTime endTime) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));

        // Kiểm tra xem đã có lịch cho ngày này chưa
        if (scheduleRepository.existsBySpecialistAndDayOfWeek(specialist, dayOfWeek)) {
            throw new RuntimeException("Đã có lịch cho ngày này");
        }

        SpecialistSchedule schedule = new SpecialistSchedule();
        schedule.setSpecialist(specialist);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setActive(true);

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public SpecialistSchedule updateSchedule(Long scheduleId, LocalTime startTime, LocalTime endTime) {
        SpecialistSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc"));

        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deactivateSchedule(Long scheduleId) {
        SpecialistSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc"));

        schedule.setActive(false);
        scheduleRepository.save(schedule);

        // Thông báo cho admin
        notificationService.sendSystemNotification(
                1L, // Admin ID
                "Lịch làm việc thay đổi",
                "Chuyên viên " + schedule.getSpecialist().getFullName() + 
                " đã thay đổi lịch làm việc ngày " + schedule.getDayOfWeek()
        );
    }

    @Transactional
    public void activateSchedule(Long scheduleId) {
        SpecialistSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch làm việc"));

        schedule.setActive(true);
        scheduleRepository.save(schedule);
    }

    public List<SpecialistSchedule> getSpecialistSchedules(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));

        return scheduleRepository.findBySpecialistOrderByDayOfWeekAsc(specialist);
    }

    public List<SpecialistSchedule> getActiveSchedules() {
        return scheduleRepository.findByActiveTrueOrderByDayOfWeekAsc();
    }

    public List<SpecialistSchedule> getSchedulesByDay(DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDayOfWeekAndActiveTrueOrderByStartTimeAsc(dayOfWeek);
    }

    public boolean isSpecialistAvailable(Long specialistId, LocalDate date, LocalTime time) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Optional<SpecialistSchedule> schedule = scheduleRepository
                .findBySpecialistAndDayOfWeekAndActiveTrue(specialist, dayOfWeek);

        if (schedule.isEmpty()) {
            return false;
        }

        return time.isAfter(schedule.get().getStartTime()) && 
               time.isBefore(schedule.get().getEndTime());
    }

    public Map<String, Object> getScheduleStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        List<SpecialistSchedule> schedules = scheduleRepository.findAll();

        // Thống kê theo ngày
        Map<DayOfWeek, Long> dayCount = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayCount.put(day, schedules.stream()
                    .filter(s -> s.getDayOfWeek() == day)
                    .count());
        }

        // Thống kê theo chuyên viên
        Map<String, Long> specialistCount = new HashMap<>();
        schedules.forEach(schedule -> {
            String specialistName = schedule.getSpecialist().getFullName();
            specialistCount.merge(specialistName, 1L, Long::sum);
        });

        // Thống kê giờ làm việc trung bình
        double averageHours = schedules.stream()
                .mapToDouble(s -> s.getEndTime().getHour() - s.getStartTime().getHour())
                .average()
                .orElse(0.0);

        statistics.put("totalSchedules", schedules.size());
        statistics.put("dayCount", dayCount);
        statistics.put("specialistCount", specialistCount);
        statistics.put("averageHours", averageHours);

        return statistics;
    }

    public List<Specialist> getAvailableSpecialists(LocalDate date, LocalTime time) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<SpecialistSchedule> schedules = scheduleRepository
                .findByDayOfWeekAndActiveTrueOrderByStartTimeAsc(dayOfWeek);

        return schedules.stream()
                .filter(schedule -> time.isAfter(schedule.getStartTime()) && 
                                  time.isBefore(schedule.getEndTime()))
                .map(SpecialistSchedule::getSpecialist)
                .toList();
    }
} 