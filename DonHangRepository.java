package com.skincare.service.repository;

import com.skincare.service.model.DonHang;
import com.skincare.service.model.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    
    Optional<DonHang> findByMaDonHang(String maDonHang);
    
    Page<DonHang> findByKhachHang(NguoiDung khachHang, Pageable pageable);
    
    List<DonHang> findByKhachHangAndTrangThaiOrderByNgayDatDesc(NguoiDung khachHang, DonHang.TrangThai trangThai);
    
    @Query("SELECT d FROM DonHang d WHERE d.ngayHen BETWEEN :tuNgay AND :denNgay")
    List<DonHang> findByNgayHenBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
    
    @Query("SELECT d FROM DonHang d WHERE d.trangThai = :trangThai")
    Page<DonHang> findByTrangThai(@Param("trangThai") DonHang.TrangThai trangThai, Pageable pageable);
    
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.trangThai = :trangThai")
    Long countByTrangThai(@Param("trangThai") DonHang.TrangThai trangThai);
    
    @Query("SELECT COUNT(d) FROM DonHang d WHERE d.ngayDat BETWEEN :tuNgay AND :denNgay")
    Long countDonHangTrongKhoangThoiGian(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
} 