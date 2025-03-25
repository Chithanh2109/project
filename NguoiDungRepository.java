package com.skincare.service.repository;

import com.skincare.service.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    
    Optional<NguoiDung> findByEmail(String email);
    
    boolean existsByTenDangNhap(String tenDangNhap);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM NguoiDung u WHERE u.vaiTro = com.skincare.service.model.NguoiDung.VaiTro.CHUYEN_VIEN AND u.kichHoat = true")
    List<NguoiDung> findAllChuyenVien();
    
    @Query("SELECT u FROM NguoiDung u WHERE u.vaiTro = com.skincare.service.model.NguoiDung.VaiTro.NHAN_VIEN AND u.kichHoat = true")
    List<NguoiDung> findAllNhanVien();
    
    List<NguoiDung> findByVaiTroAndKichHoat(NguoiDung.VaiTro vaiTro, boolean kichHoat);
} 