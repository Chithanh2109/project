package UTH.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class ChuyenVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ten;
    private String chuyenMon;
    // ... các thuộc tính khác, getters, setters, constructors

    @OneToMany(mappedBy = "chuyenVien")
    private List<LichHen> lichHens;
}
