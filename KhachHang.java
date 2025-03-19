package UTH.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ten;
    private String email;
    private String soDienThoai;
    // ... các thuộc tính khác, getters, setters, constructors

    @OneToMany(mappedBy = "khachHang")
    private List<LichHen> lichHens;
}