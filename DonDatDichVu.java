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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DonDatDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String maDon;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuyen_vien_id")
    private ChuyenVien chuyenVien; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_check_in_id")
    private NhanVien nhanVienCheckIn;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_check_out_id")
    private NhanVien nhanVienCheckOut;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_phan_cong_id")
    private NhanVien nhanVienPhanCong;
    
    @OneToMany(mappedBy = "donDatDichVu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonDat> chiTietDonDat = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime ngayDat;
    
    private LocalDateTime ngayThucHien;
    
    private LocalDateTime thoiGianCheckIn;
    
    private LocalDateTime thoiGianCheckOut;
    
    private LocalDateTime thoiGianHoanThanh;
    
    @Column(nullable = false)
    private BigDecimal tongTien;
    
    private BigDecimal giamGia;
    
    private BigDecimal thanhTien;
    
    @Enumerated(EnumType.STRING)
    private TrangThaiDon trangThai;
    
    @Enumerated(EnumType.STRING)
    private PhuongThucThanhToan phuongThucThanhToan;
    
    private boolean daThanhToan;
    
    @Column(columnDefinition = "TEXT")
    private String ghiChu;
    
    @Column(columnDefinition = "TEXT")
    private String ketQuaThucHien;
    
    private String lyDoHuy;
    
    private LocalDateTime ngayHuy;
    
    public enum TrangThaiDon {
        CHO_XAC_NHAN,
        DA_XAC_NHAN,
        DANG_THUC_HIEN,
        HOAN_THANH,
        DA_HUY
    }
    
    public enum PhuongThucThanhToan {
        TIEN_MAT,
        THE_TIN_DUNG,
        CHUYEN_KHOAN,
        VI_DIEN_TU
    }
    
    @PrePersist
    public void prePersist() {
        if (ngayDat == null) {
            ngayDat = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = TrangThaiDon.CHO_XAC_NHAN;
        }
        if (maDon == null) {
            maDon = "DH" + System.currentTimeMillis();
        }
    }
} 