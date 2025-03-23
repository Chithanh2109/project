package com.skincare.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loai_da")
public class LoaiDa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String tenLoaiDa;
    
    @Column(length = 1000)
    private String moTa;
    
    @ManyToMany(mappedBy = "danhSachLoaiDaPhuHop")
    private Set<DichVu> danhSachDichVu = new HashSet<>();
    
    @OneToMany(mappedBy = "loaiDa")
    private Set<KhachHangLoaiDa> danhSachKhachHang = new HashSet<>();
} 