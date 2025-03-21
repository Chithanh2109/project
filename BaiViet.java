package com.skincare.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bai_viet")
public class BaiViet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tieuDe;
    
    @Column(length = 500)
    private String moTaNgan;
    
    @Column(length = 10000, nullable = false)
    private String noiDung;
    
    @Column
    private String hinhAnh;
    
    @ManyToOne
    @JoinColumn(name = "tac_gia_id", nullable = false)
    private NguoiDung tacGia;
    
    @Column(nullable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();
    
    @Column
    private LocalDateTime ngayCapNhat;
    
    @Column(nullable = false)
    private boolean xuatBan = false;
    
    @Column
    private LocalDateTime ngayXuatBan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiBaiViet loaiBaiViet = LoaiBaiViet.TIN_TUC;
    
    public enum LoaiBaiViet {
        TIN_TUC, KINH_NGHIEM, KHUYEN_MAI, BLOG
    }
} 