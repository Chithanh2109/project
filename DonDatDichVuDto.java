package com.skincare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonDatDichVuDto {
    private Long id;
    private String maDon;
    private KhachHangDto khachHang;
    private ChuyenVienDto chuyenVien;
    private String tenNhanVienCheckIn;
    private String tenNhanVienCheckOut;
    private String tenNhanVienPhanCong;
    private List<ChiTietDonDatDto> chiTietDonDat;
    private LocalDateTime ngayDat;
    private LocalDateTime ngayThucHien;
    private LocalDateTime thoiGianCheckIn;
    private LocalDateTime thoiGianCheckOut;
    private LocalDateTime thoiGianHoanThanh;
    private BigDecimal tongTien;
    private BigDecimal giamGia;
    private BigDecimal thanhTien;
    private String trangThai;
    private String phuongThucThanhToan;
    private boolean daThanhToan;
    private String ghiChu;
    private String ketQuaThucHien;
    private String lyDoHuy;
    private LocalDateTime ngayHuy;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietDonDatDto {
        private Long id;
        private DichVuDto dichVu;
        private int soLuong;
        private BigDecimal donGia;
        private BigDecimal thanhTien;
        private String ghiChu;
        private boolean daHoanThanh;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KhachHangDto {
        private Long id;
        private String hoTen;
        private String email;
        private String soDienThoai;
        private String loaiDa;
        private String tinhTrangDa;
    }
} 