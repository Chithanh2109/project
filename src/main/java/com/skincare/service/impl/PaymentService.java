package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.Payment;
import com.skincare.model.PaymentStatus;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
 