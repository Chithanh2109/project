package UTH.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private KhachHang khachHang;
    @ManyToOne
    private ChuyenVien chuyenVien;
    @ManyToOne
    private DichVu dichVu;
    private LocalDateTime thoiGianBatDau;
    // ... các thuộc tính khác, getters, setters, constructors
}