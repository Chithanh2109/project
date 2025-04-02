package com.skincare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChiTietDonDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_dat_id", nullable = false)
    private DonDatDichVu donDatDichVu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", nullable = false)
    private DichVu dichVu;
    
    private int soLuong;
    
    private BigDecimal donGia;
    
    private BigDecimal thanhTien;
    
    @Column(columnDefinition = "TEXT")
    private String ghiChu;
    
    private boolean daHoanThanh;
    
    @PrePersist
    public void prePersist() {
        if (thanhTien == null && donGia != null && soLuong > 0) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }
} 