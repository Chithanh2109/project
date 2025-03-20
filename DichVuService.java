package UTH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import UTH.entity.DichVu;
import UTH.repository.DichVuRepository;
import java.util.List;

@Service
public class DichVuService {
    @Autowired
    private DichVuRepository dichVuRepository;

    public DichVu taoDichVu(String ten, double gia) {
        DichVu dichVu = new DichVu();
        dichVu.setTen(ten);
        dichVu.setGia(gia);
        return dichVuRepository.save(dichVu);
    }

    public DichVu layDichVu(Long id) {
        return dichVuRepository.findById(id).orElse(null);
    }

    public List<DichVu> layTatCaDichVu() {
        return dichVuRepository.findAll();
    }

    // ... các phương thức khác (cập nhật, xóa, ...)
}