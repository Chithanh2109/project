package UTH.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class DichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ten;
    private double gia;
    // ... các thuộc tính khác, getters, setters, constructors

    @OneToMany(mappedBy = "dichVu")
    private List<LichHen> lichHens;
}
