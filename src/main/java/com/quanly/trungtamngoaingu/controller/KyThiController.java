package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.KyThiRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ky-thi")
public class KyThiController {
    @Autowired
    private GiaoVienRepository giaoVienRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private KyThiRepository kyThiRepository;
    @Autowired
    private KyThiService kyThiService;
    @Autowired
    private ChungChiRepository chungChiRepository;
    @Autowired
    private LichThiRepository lichThiRepository;
    @Autowired
    private PhongRepository phongRepository;
    @GetMapping("/danh-sach-ngay")
    public ResponseEntity<List<LocalDate>> getDaysOfMonth(@RequestParam(name = "nam") int year,
                                                          @RequestParam(name = "thang") int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<LocalDate> daysOfMonth = new ArrayList<>();

        // Lấy số ngày trong tháng
        int days = yearMonth.lengthOfMonth();
        // Tạo danh sách các ngày
        for (int i = 1; i <= days; i++) {
            LocalDate day = LocalDate.of(year, month, i);
            daysOfMonth.add(day);
        }
        return ResponseEntity.ok(daysOfMonth);
    }
    @PostMapping("/them")
    public ResponseEntity<?> them(@RequestBody KyThiRequest kyThiRequest) {
        return kyThiService.themKyThiVaLichThi(kyThiRequest);
    }
    @PutMapping("/sua/{maKyThi}")
    public ResponseEntity<?> sua(@PathVariable Long maKyThi, @RequestBody KyThiRequest kyThiRequest) {
        return kyThiService.suaKyThiVaLichThi(maKyThi, kyThiRequest);
    }

    @GetMapping("/tat-ca")
    public ResponseEntity<List<KyThi>> layTatCaKyThi() {
        Sort sort = Sort.by(Sort.Order.desc("namThi"), Sort.Order.desc("thangThi"));
        List<KyThi> tatCaKyThi = kyThiRepository.findAll(sort);
        return ResponseEntity.ok(tatCaKyThi);
    }

    @GetMapping("/lay/{maKyThi}")
    public ResponseEntity<?> layKyThi(@PathVariable Long maKyThi) {
        Optional<KyThi> kyThi = kyThiRepository.findById(maKyThi);
        if(kyThi.isEmpty()){
            return new ResponseEntity<>(new MessageResponse("Kỳ thi không tồn tại"), HttpStatus.NOT_FOUND);

        }
        return ResponseEntity.ok(kyThi.get());
    }
    @GetMapping("/lay-danh-sach-theo-thang-nam")
    public ResponseEntity<?> getAllKhoaHocByMonthAndYear(
            @RequestParam int thang,
            @RequestParam int nam
    ) {
        Sort sort = Sort.by(Sort.Order.desc("namThi"), Sort.Order.desc("thangThi"));

        List<KyThi> kyThis = kyThiRepository.findByThangThiAndNamThi(thang, nam, sort);
        return ResponseEntity.ok(kyThis);
    }
    @GetMapping("/lay-danh-sach-theo-chung-chi")
    public ResponseEntity<?> getAllKhoaHocByChungChi(@RequestParam Long maChungChi) {
        Sort sort = Sort.by(Sort.Order.desc("namThi"), Sort.Order.desc("thangThi"));

        List<KyThi> kyThis = kyThiRepository.findByChungChi_MaChungChi(maChungChi, sort);
        return ResponseEntity.ok(kyThis);
    }


    @GetMapping("/lay-danh-sach-nam")
    public ResponseEntity<?> layDanhSachNamKhoaHoc() {
        List<Integer> danhSachNam = kyThiRepository.findDistinctAndOrderByNamThi();
        return ResponseEntity.ok(danhSachNam);
    }
    @DeleteMapping("/xoa/{maKyThi}")
    @Transactional
    public ResponseEntity<?> xoaKyThi(@PathVariable Long maKyThi) {
        try {
            // Kiểm tra xem kỳ thi có tồn tại hay không
            Optional<KyThi> kyThiOptional = kyThiRepository.findById(maKyThi);
            if (kyThiOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Không tìm thấy kỳ thi"), HttpStatus.NOT_FOUND);
            }

            // Xóa lịch thi của kỳ thi
           lichThiRepository.deleteAllByKyThi(kyThiOptional.get());

            // Xóa kỳ thi
            kyThiRepository.delete(kyThiOptional.get());

            return ResponseEntity.ok(new MessageResponse("Xóa kỳ thi thành công"));
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
    }
    @GetMapping("/lay-danh-sach-con-han")
    public ResponseEntity<?> layDanhSachConHan() {
        Sort sort = Sort.by(Sort.Order.desc("namThi"), Sort.Order.desc("thangThi"));

        List<KyThi> kyThiConHan = kyThiRepository.findByHanDangKy("Con_Han", sort);
        if (kyThiConHan.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không có kỳ thi nào còn hạn"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(kyThiConHan);
    }
    @GetMapping("/lay-danh-sach-con-han-theo-chung-chi")
    public ResponseEntity<?> layDanhSachConHanTheoChungChi(@RequestParam Long maChungChi) {
        Sort sort = Sort.by(Sort.Order.desc("namThi"), Sort.Order.desc("thangThi"));
        List<KyThi> kyThiConHan = kyThiRepository.findByHanDangKyAndChungChi_MaChungChi("Con_Han", maChungChi, sort);
        if (kyThiConHan.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không có kỳ thi nào còn hạn và thuộc chứng chỉ này"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(kyThiConHan);
    }
    @GetMapping("/lay-danh-sach-con-han-theo-thang-nam")
    public ResponseEntity<?> layDanhSachConHanTheoThangNam(
            @RequestParam(name = "thang") Integer thang,
            @RequestParam(name = "nam") Integer nam) {
        Sort sort = Sort.by(Sort.Order.desc("namThi"), Sort.Order.desc("thangThi"));

        List<KyThi> kyThiConHan = kyThiRepository.findByHanDangKyAndThangThiAndNamThi("Con_Han", thang, nam, sort);

        return ResponseEntity.ok(kyThiConHan);
    }
    @PutMapping("/them-giao-vien/{maKyThi}/{maTaiKhoan}")
    public ResponseEntity<?> themGiaoVien(@PathVariable Long maKyThi, @PathVariable Long maTaiKhoan) {
        Optional<KyThi> kyThiOptional = kyThiRepository.findById(maKyThi);
        if (kyThiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Kỳ thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Optional<GiaoVien> giaoVienOptional = giaoVienRepository.findById(maTaiKhoan);
        if (giaoVienOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên không tồn tại"), HttpStatus.NOT_FOUND);
        }

        KyThi kyThi = kyThiOptional.get();
        GiaoVien giaoVien = giaoVienOptional.get();

        // Thêm giáo viên vào danh sách giáo viên ra đề cho kỳ thi
        kyThi.getGiaoViens().add(giaoVien);

        kyThiRepository.save(kyThi);
        return ResponseEntity.ok(new MessageResponse("Đã thêm giáo viên ra đề cho kỳ thi thành công"));
    }

    //=======phan them moi=======//
    // thêm giáo viên ra đề cho kỳ thi
    @PutMapping("/them-giao-vien-ra-de/{maKyThi}/{maTaiKhoan}")
    public ResponseEntity<?> themGiaoVienRaDe(@PathVariable Long maKyThi,
                                              @PathVariable Long maTaiKhoan) {
        Optional<KyThi> kyThiOptional = kyThiRepository.findById(maKyThi);
        if (kyThiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Kỳ thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Optional<GiaoVien> giaoVienOptional = giaoVienRepository.findById(maTaiKhoan);
        if (giaoVienOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên không tồn tại"), HttpStatus.NOT_FOUND);
        }

        KyThi kyThi = kyThiOptional.get();
        GiaoVien giaoVien = giaoVienOptional.get();

        // Thêm giáo viên vào danh sách giáo viên ra đề cho kỳ thi
        kyThi.getGiaoViens().add(giaoVien);

        kyThiRepository.save(kyThi);
        String tieuDe = "Phân công ra đề";
        String nDung = "Bạn được phân công ra đề cho kỳ thi " +
                kyThi.getChungChi().getTenChungChi() +
                kyThi.getThangThi()+"/"+
                kyThi.getNamThi()
                ;
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                giaoVien.getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Đã thêm giáo viên ra đề cho kỳ thi thành công"));
    }
    //hàm lấy danh sách giáo viên đã phân công ra đề
    @GetMapping("/danh-sach-giao-vien-ra-de/{maKyThi}")
    public ResponseEntity<?> layDanhSachGiaoVienRaDe(@PathVariable Long maKyThi) {
        Optional<KyThi> kyThiOptional = kyThiRepository.findById(maKyThi);
        if (kyThiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Kỳ thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        KyThi kyThi = kyThiOptional.get();
        Set<GiaoVien> giaoViens = kyThi.getGiaoViens();

        return ResponseEntity.ok(giaoViens);
    }
    // hàm lấy danh sách kỳ thi cần ra đề của một giáo viên (dùng tenDangNhap) để lấy
    @GetMapping("/danh-sach-ky-thi-cua-giao-vien/{tenDangNhap}")
    public ResponseEntity<?> layDanhSachKyThiCuaGiaoVien(@PathVariable String tenDangNhap) {
        GiaoVien giaoVienOptional = giaoVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);
        if (giaoVienOptional== null) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<KyThi> kyThis = kyThiRepository.findAllByGiaoVien(giaoVienOptional.getMaTaiKhoan());
        return ResponseEntity.ok(kyThis);
    }
    //hàm lấy danh sách giáo viên hợp lệ để phân công ra đề
    @GetMapping("/ds-giao-vien-hop-le/{maKyThi}")
    public ResponseEntity<?> layDanhSachGiaoVienHopLeChoKyThi(@PathVariable Long maKyThi) {
        List<GiaoVien> giaoViens = giaoVienRepository.findAvailableTeachersForExam(maKyThi);
        return ResponseEntity.ok(giaoViens);
    }
    //xóa một giáo viên khỏi kỳ thi
    @DeleteMapping("/xoa-giao-vien/{maKyThi}/{maTaiKhoan}")
    @Transactional
    public ResponseEntity<?> xoaGiaoVienRaDe(@PathVariable Long maKyThi, @PathVariable Long maTaiKhoan) {
        try {
            // Tìm kỳ thi
            Optional<KyThi> kyThiOptional = kyThiRepository.findById(maKyThi);
            if (kyThiOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Kỳ thi không tồn tại"), HttpStatus.NOT_FOUND);
            }
            KyThi kyThi = kyThiOptional.get();

            // Tìm giáo viên
            Optional<GiaoVien> giaoVienOptional = giaoVienRepository.findById(maTaiKhoan);
            if (giaoVienOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Giáo viên không tồn tại"), HttpStatus.NOT_FOUND);
            }
            GiaoVien giaoVien = giaoVienOptional.get();

            // Xóa giáo viên ra khỏi danh sách giáo viên ra đề của kỳ thi
            if (kyThi.getGiaoViens().contains(giaoVien)) {
                kyThi.getGiaoViens().remove(giaoVien);
                kyThiRepository.save(kyThi); // Lưu thay đổi
                return ResponseEntity.ok(new MessageResponse("Đã xóa giáo viên ra khỏi danh sách giáo viên ra đề cho kỳ thi"));
            } else {
                return new ResponseEntity<>(new MessageResponse("Giáo viên này không phải là người ra đề cho kỳ thi này"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi trong quá trình xử lý: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
