package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.DangKyThiRequest;
import com.quanly.trungtamngoaingu.payload.response.HocVienResponse;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.*;
import com.quanly.trungtamngoaingu.sercurity.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/dang-ky-thi")
public class DangKyThiController {

    @Autowired
    private DangKyThiRepository dangKyThiRepository;
    @Autowired
    private KyThiRepository kyThiRepository;
    @Autowired
    private FilesStorageService storageService;
    @Autowired
    private HocVienRepository hocVienRepository;
    @Autowired
    private ChungChiRepository chungChiRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private LichThiRepository lichThiRepository;

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<DangKyThi>> layTatCaDangKyThi() {
        List<DangKyThi> sortedDangKyThiList =   dangKyThiRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayDangKy"));
        return ResponseEntity.ok(sortedDangKyThiList);
    }
    @GetMapping("/lay/{maDangKyThi}")
    public ResponseEntity<?> layDangKyThi(@PathVariable Long maDangKyThi) {
        Optional<DangKyThi> dangKyThi = dangKyThiRepository.findById(maDangKyThi);
        if (dangKyThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(dangKyThi.get());
    }
    @GetMapping("/danh-sach-hoc-vien-cua-mot-lich-thi/{maLichThi}")
    public ResponseEntity<?> dsHocVienLichThi(@PathVariable Long maLichThi) {
        List<HocVien> dangKyThi = dangKyThiRepository.findHocVienByMaLichThi(maLichThi);
        return ResponseEntity.ok(dangKyThi);
    }
    @PostMapping("/dang-ky")
    public ResponseEntity<?> themDangKyThi(@RequestBody DangKyThiRequest dangKyThiRequest) {
        Optional<KyThi> kyThi =
                kyThiRepository.findById(dangKyThiRequest.getMaKyThi());

        HocVien hocVienOptional = hocVienRepository.findByTaiKhoan_TenDangNhap(dangKyThiRequest.getTenTaiKhoan());
        if (kyThi.isEmpty() || hocVienOptional == null) {
            return new ResponseEntity<>(new MessageResponse("Kỳ thi hoặc học viên không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Date ngayHienTai = new Date();
        //lấy ngày thi đầu tiên
        LichThi lichThi = lichThiRepository.findFirstByKyThiOrderByNgayThi(kyThi.get());

        if (lichThi == null) {
            // Xử lý nếu không có lịch thi
            return ResponseEntity.ok(new MessageResponse("lichthinull"));
        }

        Date ngayThi = lichThi.getNgayThi();

        // Tính ngày có thể bắt đầu đăng ký (ngày thi trừ 2 tháng)
        Calendar cal = Calendar.getInstance();
        cal.setTime(ngayThi);
        cal.add(Calendar.MONTH, -2);
        Date ngayBatDauDangKy = cal.getTime();

        if (ngayHienTai.before(ngayBatDauDangKy)) {
            // Nếu ngày hiện tại nằm ngoài vòng 2 tháng trước ngày thi
            return ResponseEntity.ok(new MessageResponse("chuatoihan"));
        }
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(ngayThi);
        cal2.add(Calendar.MONTH, -1);
        Date ngayKetThucDangKy = cal2.getTime();
        if (ngayHienTai.after(ngayKetThucDangKy)) {
            // Nếu ngày hiện tại nằm ngoài vòng 2 tháng trước ngày thi
            return ResponseEntity.ok(new MessageResponse("quahan"));
        }
        Long dem = dangKyThiRepository.countByMaKyThi(kyThi.get().getMaKyThi());
        if(dem>=kyThi.get().getSoLuongDuocDangKy()){
            return new ResponseEntity<>(new MessageResponse("full"), HttpStatus.OK);

        }
        DangKyThi dangKyThi = new DangKyThi();
        dangKyThi.setTrangThaiDangKyThi(DangKyThi.TrangThaiDangKyThi.Chua_Duyet);
        Date ngayHienTai1 = new Date();
        dangKyThi.setNgayDangKy(ngayHienTai1);
        dangKyThi.setKyThi(kyThi.get());
        dangKyThi.setHocVien(hocVienOptional);

        dangKyThiRepository.save(dangKyThi);
        return ResponseEntity.ok(new MessageResponse("Thêm đăng ký thi thành công"));
    }
    @PutMapping("/cap-nhat-lich-thi/{maDangKy}")
    public ResponseEntity<?> capNhatLichThi(@RequestBody DangKyThiRequest dangKyThiRequest,
                                            @PathVariable Long maDangKy) {
        Optional<LichThi> lichThi = lichThiRepository.findById(dangKyThiRequest.getMaLichThi());
        Optional<DangKyThi> dangKyThi = dangKyThiRepository.findById(maDangKy);

        if (lichThi.isEmpty() || dangKyThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi hoặc đăng ký thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        if (lichThi.get().getPhong() == null) {
            return new ResponseEntity<>(new MessageResponse("phongnull"), HttpStatus.OK);
        }
        // Kiểm tra số lượng đăng ký hiện tại
        Long soLuongDangKyHienTai = dangKyThiRepository.countByLichThi(lichThi.get());

        // Kiểm tra sức chứa của phòng
        Integer sucChuaPhong = lichThi.get().getPhong().getSucChua();

        if (soLuongDangKyHienTai >= sucChuaPhong) {
            return new ResponseEntity<>(new MessageResponse("full"), HttpStatus.OK);
        }

        dangKyThi.get().setLichThi(lichThi.get());
        dangKyThi.get().setTrangThaiDangKyThi(DangKyThi.TrangThaiDangKyThi.Da_Sap_Lich);
        dangKyThiRepository.save(dangKyThi.get());
        String tieuDe = "Sắp lịch thi";
        String nDung = "Bạn đã được sắp lịch thi thành công. Hãy xem lịch thi của bạn";
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                dangKyThi.get().getHocVien().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Thêm phân bổ thi thành công"));
    }

    @PutMapping("/cap-nhat-trang-thai/{maDangKyThi}")
    public ResponseEntity<?> capNhatTrangThaiDangKyThi(@PathVariable Long maDangKyThi, @RequestBody DangKyThiRequest dangKyThiRequest) {
        // Kiểm tra xem Đăng ký thi có tồn tại không
        Optional<DangKyThi> dangKyThiOptional = dangKyThiRepository.findById(maDangKyThi);
        if (dangKyThiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        try {

            DangKyThi dangKyThi = dangKyThiOptional.get();
            dangKyThi.setTrangThaiDangKyThi(dangKyThiRequest.getTrangThaiDangKyThi());
            dangKyThiRepository.save(dangKyThi);
            String tieuDe = "Duyệt đăng ký thi";
            String nDung = "Đăng ký thi chứng chỉ " +
                    dangKyThi.getKyThi().getChungChi().getTenChungChi() +  dangKyThi.getKyThi().getThangThi() + "/"+
                    dangKyThi.getKyThi().getNamThi()+" của bạn đã được duyệt.";
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    dangKyThi.getHocVien().getTaiKhoan(),
                    tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
            return ResponseEntity.ok(new MessageResponse("Cập nhật trạng thái đăng ký thi thành công"));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new MessageResponse("Trạng thái đăng ký thi không hợp lệ"), HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/huy/{maKyThi}/{ten}")
    public ResponseEntity<?> xoaDangKyThi(@PathVariable Long maKyThi,
                                          @PathVariable String ten) {
        try {

            DangKyThi dangKyThi = dangKyThiRepository.findByHocVien_TaiKhoan_TenDangNhapAndKyThi_MaKyThi(ten, maKyThi);
            if (dangKyThi == null) {
                return new ResponseEntity<>(new MessageResponse("Đăng ký thi không tồn tại"), HttpStatus.NOT_FOUND);
            }
            dangKyThiRepository.deleteById(dangKyThi.getMaDangKyThi());
            return ResponseEntity.ok(new MessageResponse("Huy đăng ký thi thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/xoa-dang-ky-thi-chua-duyet-het-han")
    public ResponseEntity<?> deleteAllChuaDuyetAndHetHan() {
        try {
            dangKyThiRepository.deleteAllChuaDuyetAndHetHan();
            return ResponseEntity.ok(new MessageResponse("Deleted successfully"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
    }

    @GetMapping("/theo-ten-dang-nhap/{tenDangNhap}")
    public ResponseEntity<?> layDangKyTheoTenDangNhap(@PathVariable String tenDangNhap) {
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (hocVien == null) {
            return new ResponseEntity<>(new MessageResponse("Học viên không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayDangKy");

        List<DangKyThi> dangKyThiList = dangKyThiRepository.findByHocVien(hocVien, sort);

        return ResponseEntity.ok(dangKyThiList);
    }
    @GetMapping("/ky-thi-con-han")
    public ResponseEntity<?> layDangKyTheoKyThiConHan() {
        //Sort sort = Sort.by(Sort.Direction.DESC, "ngayDangKy");
        List<DangKyThi> dangKyThiList = dangKyThiRepository.findDangKyThiDaDuyetVaDaSapLich(DangKyThi.TrangThaiDangKyThi.Da_Duyet, DangKyThi.TrangThaiDangKyThi.Da_Sap_Lich);
        return ResponseEntity.ok(dangKyThiList);
    }
    @PostMapping("/kiem-tra")
    public ResponseEntity<?> kiemTraDangKyThi(@RequestBody DangKyThiRequest dangKyThiRequest) {

        Optional<KyThi> optionalKyThi = kyThiRepository.findById(dangKyThiRequest.getMaKyThi());
        if (optionalKyThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("kythi-notfound"), HttpStatus.NOT_FOUND);
        }
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(dangKyThiRequest.getTenTaiKhoan());
        if (hocVien == null) {
            return new ResponseEntity<>(new MessageResponse("hocvien-notfound"), HttpStatus.NOT_FOUND);
        }
        KyThi kyThi = optionalKyThi.get();
        boolean check = dangKyThiRepository.existsByHocVienAndKyThi(hocVien, kyThi);
        if (check) {
            DangKyThi dangKyThi = dangKyThiRepository.findByHocVienAndKyThi(hocVien, kyThi);
            if (dangKyThi != null) {
                if (dangKyThi.getTrangThaiDangKyThi() == DangKyThi.TrangThaiDangKyThi.Da_Duyet) {
                    return new ResponseEntity<>(new MessageResponse("daduyet"), HttpStatus.OK);
                } else if (dangKyThi.getTrangThaiDangKyThi() == DangKyThi.TrangThaiDangKyThi.Chua_Duyet) {
                    return new ResponseEntity<>(new MessageResponse("chuaduyet"), HttpStatus.OK);
                }
                else if (dangKyThi.getTrangThaiDangKyThi() == DangKyThi.TrangThaiDangKyThi.Da_Sap_Lich) {
                    return new ResponseEntity<>(new MessageResponse("dasaplich"), HttpStatus.OK);
                }
                else if (dangKyThi.getTrangThaiDangKyThi() == DangKyThi.TrangThaiDangKyThi.Da_Len_Diem) {
                    return new ResponseEntity<>(new MessageResponse("dalendiem"), HttpStatus.OK);
                }
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("false"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("unknown"), HttpStatus.OK);
    }

    @GetMapping("/{maKyThi}/count")
    public Long countDangKy(@PathVariable Long maKyThi) {
        return dangKyThiRepository.countByMaKyThi(maKyThi);
    }
    @GetMapping("/ky-thi-da-thi")
    public ResponseEntity<?> getKyThiBeforeDate() {
        Date date = new Date();
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayDangKy");
        List<DangKyThi> dangKyThiList = dangKyThiRepository.findByLichThiNgayThiAfter(date, sort);
        return new ResponseEntity<>(dangKyThiList, HttpStatus.OK);
    }
    @GetMapping("/lay-theo-ma-ky-thi-lich-thi-hoc-vien/{maKyThi}/{maLichThi}/{maHocVien}")
    public ResponseEntity<?> layTheoKyThiLTHV(@PathVariable Long maLichThi,
                                              @PathVariable Long maKyThi,
                                              @PathVariable Long maHocVien) {
        DangKyThi dangKyThi = dangKyThiRepository.findByKyThi_MaKyThiAndLichThi_MaLichThiAndHocVien_MaTaiKhoan(maKyThi, maLichThi, maHocVien);

        if (dangKyThi == null) {
            return new ResponseEntity<>(new MessageResponse("null"), HttpStatus.OK);
        }
        return ResponseEntity.ok(dangKyThi);
    }

}
