package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.KyThiRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.ChungChiRepository;
import com.quanly.trungtamngoaingu.repository.KyThiRepository;
import com.quanly.trungtamngoaingu.repository.LichThiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KyThiService {

    @Autowired
    private KyThiRepository kyThiRepository;

    @Autowired
    private ChungChiRepository chungChiRepository;

    @Autowired
    private LichThiRepository lichThiRepository;
    @Autowired
    private PhanCongGiaoVienRepository phanCongGiaoVienRepository;
    @Transactional
    public ResponseEntity<?> themKyThiVaLichThi(KyThiRequest kyThiRequest) {
        Optional<ChungChi> chungChi = chungChiRepository.findById(kyThiRequest.getMaChungChi());
        if (chungChi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Chứng không tồn tại"), HttpStatus.NOT_FOUND);
        }
        boolean check = kyThiRepository.existsByThangThiAndNamThiAndChungChi(kyThiRequest.getThangThi(), kyThiRequest.getNamThi(), chungChi.get());
        if (check) {
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }

        // Lưu đối tượng KyThi và lấy ID sau khi lưu
        KyThi kyThi = new KyThi();
        kyThi.setThangThi(kyThiRequest.getThangThi());
        kyThi.setNamThi(kyThiRequest.getNamThi());
        kyThi.setSoLuongDuocDangKy(kyThiRequest.getSoLuongDuocDangKy());
        kyThi.setChungChi(chungChi.get());
        kyThi = kyThiRepository.save(kyThi); // Lưu và cập nhật ID

        Set<LichThi> lichThiSet = new HashSet<>();
        if (kyThiRequest.getDanhSachNgayThi() != null) {
            for (LocalDate ngay : kyThiRequest.getDanhSachNgayThi()) {
                createAndSaveLichThi(lichThiSet, ngay, kyThi); // Truyền kyThi đã có ID vào
            }
        }

        kyThi.setLichThis(lichThiSet);
        kyThiRepository.save(kyThi); // Lưu lại kyThi sau khi thêm lichThi


        return ResponseEntity.ok(new MessageResponse("Thêm kỳ thi và lịch thi thành công"));
    }
    @Transactional
    public ResponseEntity<?> suaKyThiVaLichThi(Long maKyThi, KyThiRequest kyThiRequest) {
        // Kiểm tra xem kỳ thi có tồn tại hay không
        KyThi kyThi = kyThiRepository.findById(maKyThi)
                .orElseThrow(() -> new EntityNotFoundException("Kỳ thi không tồn tại"));

        // Kiểm tra sự tồn tại của chứng chỉ
        ChungChi chungChi = chungChiRepository.findById(kyThiRequest.getMaChungChi())
                .orElseThrow(() -> new EntityNotFoundException("Chứng chỉ không tồn tại"));

        // Cập nhật thông tin kỳ thi
        Set<Date> ngayThiHienCo = kyThi.getLichThis().stream()
                .map(LichThi::getNgayThi)
                .collect(Collectors.toSet());

        // Danh sách ngày thi từ yêu cầu
        Set<LocalDate> ngayThiMoi = new HashSet<>(kyThiRequest.getDanhSachNgayThi());
        int checkNgayThi = 0;
        // So sánh danh sách ngày thi
        if (ngayThiHienCo.equals(ngayThiMoi)) {
            checkNgayThi = 1;
        }
        if (checkNgayThi == 1) {
            kyThi.setThangThi(kyThiRequest.getThangThi());
            kyThi.setNamThi(kyThiRequest.getNamThi());
            kyThi.setChungChi(chungChi);
            kyThi.setSoLuongDuocDangKy(kyThiRequest.getSoLuongDuocDangKy());

            kyThiRepository.save(kyThi);

            return ResponseEntity.ok(new MessageResponse("Chỉnh sửa kỳ thi và lịch thi thành công"));
        } else {
            // Kiểm tra và xử lý xoá lịch thi
            if (phanCongGiaoVienRepository.existsByLichThi_KyThi(kyThi)) {
                return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
            } else {
                kyThi.setThangThi(kyThiRequest.getThangThi());
                kyThi.setNamThi(kyThiRequest.getNamThi());
                kyThi.setChungChi(chungChi);
                kyThi.setSoLuongDuocDangKy(kyThiRequest.getSoLuongDuocDangKy());
                lichThiRepository.deleteAllByKyThi(kyThi);

                Set<LichThi> lichThiSet = new HashSet<>();
                if (kyThiRequest.getDanhSachNgayThi() != null) {
                    for (LocalDate ngay : kyThiRequest.getDanhSachNgayThi()) {
                        createAndSaveLichThi(lichThiSet, ngay, kyThi);
                    }
                }

                kyThi.setLichThis(lichThiSet);
                kyThiRepository.save(kyThi);

                return ResponseEntity.ok(new MessageResponse("Chỉnh sửa kỳ thi và lịch thi thành công"));
            }


        }

    }

    private void createAndSaveLichThi(Set<LichThi> lichThiSet, LocalDate ngay, KyThi kyThi) {

        // Logic for morning exam
        LichThi lichThiSang = new LichThi();
        lichThiSang.setKyThi(kyThi);
        lichThiSang.setNgayThi(java.sql.Date.valueOf(ngay));
        lichThiSang.setCaThi(LichThi.CaThi.Sang);
        lichThiRepository.save(lichThiSang);
        lichThiSet.add(lichThiSang);

        // Logic for afternoon exam
        LichThi lichThiChieu = new LichThi();
        lichThiChieu.setKyThi(kyThi);
        lichThiChieu.setNgayThi(java.sql.Date.valueOf(ngay));
        lichThiChieu.setCaThi(LichThi.CaThi.Chieu);
        lichThiRepository.save(lichThiChieu);
        lichThiSet.add(lichThiChieu);
    }
}
