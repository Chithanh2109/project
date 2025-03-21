package UTH.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import UTH.entity.KhachHang;
import UTH.entity.ChuyenVien;
import UTH.entity.DichVu;
import UTH.entity.LichHen;
import UTH.repository.KhachHangRepository;
import UTH.repository.ChuyenVienRepository;
import UTH.repository.DichVuRepository;
import UTH.repository.LichHenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LichHenService {

    private static final Logger logger = LoggerFactory.getLogger(LichHenService.class);

    @Autowired
    private LichHenRepository lichHenRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private ChuyenVienRepository chuyenVienRepository;
    @Autowired
    private DichVuRepository dichVuRepository;

    public LichHen datLichHen(Long khachHangId, Long chuyenVienId, Long dichVuId, LocalDateTime thoiGianBatDau) {
        try {
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(khachHangId);
            Optional<ChuyenVien> chuyenVienOptional = chuyenVienRepository.findById(chuyenVienId);
            Optional<DichVu> dichVuOptional = dichVuRepository.findById(dichVuId);

            if (khachHangOptional.isPresent() && chuyenVienOptional.isPresent() && dichVuOptional.isPresent()) {
                KhachHang khachHang = khachHangOptional.get();
                ChuyenVien chuyenVien = chuyenVienOptional.get();
                DichVu dichVu = dichVuOptional.get();

                LichHen lichHen = new LichHen();
                lichHen.setKhachHang(khachHang);
                lichHen.setChuyenVien(chuyenVien);
                lichHen.setDichVu(dichVu);
                lichHen.setThoiGianBatDau(thoiGianBatDau);
                return lichHenRepository.save(lichHen);
            } else {
                logger.error("Không tìm thấy KhachHang, ChuyenVien hoặc DichVu.");
                return null;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi đặt lịch hẹn: {}", e.getMessage());
            return null;
        }
    }

    public LichHen xacNhanLichHen(Long lichHenId) {
        try {
            Optional<LichHen> lichHenOptional = lichHenRepository.findById(lichHenId);
            if (lichHenOptional.isPresent()) {
                LichHen lichHen = lichHenOptional.get();
                // Logic xác nhận lịch hẹn (gửi email, thông báo, ...)
                logger.info("Xác nhận lịch hẹn thành công cho LichHen ID: {}", lichHenId);
                return lichHenRepository.save(lichHen);
            } else {
                logger.error("Không tìm thấy LichHen với ID: {}", lichHenId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xác nhận lịch hẹn: {}", e.getMessage());
            return null;
        }
    }

    public LichHen huyLichHen(Long lichHenId) {
        try {
            Optional<LichHen> lichHenOptional = lichHenRepository.findById(lichHenId);
            if (lichHenOptional.isPresent()) {
                LichHen lichHen = lichHenOptional.get();
                // Logic hủy lịch hẹn (gửi email, thông báo, ...)
                logger.info("Hủy lịch hẹn thành công cho LichHen ID: {}", lichHenId);
                return lichHenRepository.save(lichHen);
            } else {
                logger.error("Không tìm thấy LichHen với ID: {}", lichHenId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi hủy lịch hẹn: {}", e.getMessage());
            return null;
        }
    }

    public List<LichHen> layLichHenTheoKhachHang(Long khachHangId) {
        try {
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(khachHangId);
            if (khachHangOptional.isPresent()) {
                KhachHang khachHang = khachHangOptional.get();
                return lichHenRepository.findByKhachHang(khachHang);
            } else {
                logger.error("Không tìm thấy KhachHang với ID: {}", khachHangId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi lấy lịch hẹn theo KhachHang: {}", e.getMessage());
            return null;
        }
    }

    public LichHen hoanThanhLichHen(Long lichHenId) {
        try {
            Optional<LichHen> lichHenOptional = lichHenRepository.findById(lichHenId);
            if (lichHenOptional.isPresent()) {
                LichHen lichHen = lichHenOptional.get();
                // Logic hoàn thành lịch hẹn
                logger.info("Hoàn thành lịch hẹn thành công cho LichHen ID: {}", lichHenId);
                return lichHenRepository.save(lichHen);
            } else {
                logger.error("Không tìm thấy LichHen với ID: {}", lichHenId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi hoàn thành lịch hẹn: {}", e.getMessage());
            return null;
        }
    }

    public List<LichHen> layLichHenTheoChuyenVien(Long chuyenVienId) {
        try {
            Optional<ChuyenVien> chuyenVienOptional = chuyenVienRepository.findById(chuyenVienId);
            if (chuyenVienOptional.isPresent()) {
                ChuyenVien chuyenVien = chuyenVienOptional.get();
                return lichHenRepository.findByChuyenVien(chuyenVien);
            } else {
                logger.error("Không tìm thấy ChuyenVien với ID: {}", chuyenVienId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Lỗi khi lấy lịch hẹn theo ChuyenVien: {}", e.getMessage());
            return null;
        }
    }
}