package UTH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import UTH.entity.ChuyenVien;
import UTH.repository.ChuyenVienRepository;
import java.util.List;

@Service
public class ChuyenVienService {
    @Autowired
    private ChuyenVienRepository chuyenVienRepository;

    public ChuyenVien taoChuyenVien(String ten, String chuyenMon) {
        ChuyenVien chuyenVien = new ChuyenVien();
        chuyenVien.setTen(ten);
        chuyenVien.setChuyenMon(chuyenMon);
        return chuyenVienRepository.save(chuyenVien);
    }

    public ChuyenVien layChuyenVien(Long id) {
        return chuyenVienRepository.findById(id).orElse(null);
    }

    public List<ChuyenVien> layTatCaChuyenVien() {
        return chuyenVienRepository.findAll();
    }

    // ... các phương thức khác (cập nhật, xóa, ...)
}