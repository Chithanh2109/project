package com.simplebeauty.repository;

import com.simplebeauty.model.Appointment;
import com.simplebeauty.model.AppointmentStatus;
import com.simplebeauty.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUser(User user);
    List<Appointment> findByUserOrderByAppointmentTimeDesc(User user);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);
}
