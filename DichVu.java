package com.skincare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tenDichVu;
    
    @Column(columnDefinition = "TEXT")
    private String moTa;
    
    @Column(nullable = false)
    private BigDecimal gia;
    
    private int thoiGianThucHien;
    
    private String hinhAnh;
    
    @Column(columnDefinition = "TEXT")
    private String noiDungChiTiet;
    
    @ManyToMany(mappedBy = "dichVuThucHien", fetch = FetchType.LAZY)
    private Set<ChuyenVien> chuyenVienThucHien = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "dich_vu_loai_da", joinColumns = @JoinColumn(name = "dich_vu_id"))
    @Column(name = "loai_da")
    private Set<String> phuHopVoiLoaiDa = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "dich_vu_tinh_trang_da", joinColumns = @JoinColumn(name = "dich_vu_id"))
    @Column(name = "tinh_trang_da")
    private Set<String> phuHopVoiTinhTrangDa = new HashSet<>();
    
    @OneToMany(mappedBy = "dichVu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonDat> chiTietDonDat = new ArrayList<>();
    
    @OneToMany(mappedBy = "dichVu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DanhGia> danhGia = new ArrayList<>();
    
    private LocalDateTime ngayTao;
    
    private LocalDateTime ngayCapNhat;
    
    private boolean trangThai;
    
    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        trangThai = true;
    }
    
    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 