package com.skincare.service.repository;

import com.skincare.service.model.DichVu;
import com.skincare.service.model.LoaiDa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, Long> {
    
    List<DichVu> findByKichHoat(boolean kichHoat);
    
    @Query("SELECT d FROM DichVu d JOIN d.danhSachLoaiDaPhuHop l WHERE l = :loaiDa AND d.kichHoat = true")
    List<DichVu> findByLoaiDaAndKichHoat(@Param("loaiDa") LoaiDa loaiDa);
    
    @Query("SELECT d FROM DichVu d JOIN d.danhSachLoaiDaPhuHop l WHERE l.id IN :loaiDaIds AND d.kichHoat = true")
    List<DichVu> findByLoaiDaIdsAndKichHoat(@Param("loaiDaIds") List<Long> loaiDaIds);
    
    List<DichVu> findByTenDichVuContainingIgnoreCaseAndKichHoat(String tenDichVu, boolean kichHoat);
} 