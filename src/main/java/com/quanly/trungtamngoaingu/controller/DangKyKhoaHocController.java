package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.DangKyKhoaHocRequest;
import com.quanly.trungtamngoaingu.payload.request.LopHocRequest;
import com.quanly.trungtamngoaingu.payload.response.LopHocResponse;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.DangKyKhoaHocRepository;
import com.quanly.trungtamngoaingu.repository.HocVienRepository;
import com.quanly.trungtamngoaingu.repository.KhoaHocRepository;
import com.quanly.trungtamngoaingu.repository.ThongBaoService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/dang-ky-khoa-hoc")
public class DangKyKhoaHocController {

    @Autowired
    private DangKyKhoaHocRepository dangKyKhoaHocRepository;

    @Autowired
    private HocVienRepository hocVienRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllDangKyKhoaHoc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayDangKy") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) String tenDangNhap,
            @RequestParam(required = false) Long maKhoaHoc,
            @RequestParam(required = false) Boolean manage
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<DangKyKhoaHoc> spec = (root, criteriaQuery, criteriaBuilder) -> {
            if (!searchTerm.isEmpty()) {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("khoaHoc").get("tenKhoaHoc"), pattern),
                        criteriaBuilder.like(root.get("hocVien").get("taiKhoan").get("tenDangNhap"), pattern)
                );
            } else {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true
            }
        };
        if (maKhoaHoc != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("khoaHoc").get("maKhoaHoc"), maKhoaHoc);
            });
        }
        if (tenDangNhap != null && !tenDangNhap.isEmpty()) { // isEmpty() được sử dụng thay vì so sánh với chuỗi rỗng
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("hocVien").get("taiKhoan").get("tenDangNhap"), "%" + tenDangNhap + "%");
            });
        }

        if (Boolean.TRUE.equals(manage)) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                CriteriaBuilder.In<DangKyKhoaHoc.TrangThaiDangKyHoc> inClause = criteriaBuilder.in(root.get("trangThaiDangKyHoc"));
                inClause.value(DangKyKhoaHoc.TrangThaiDangKyHoc.DA_DUYET); // Chuyển đổi từ String sang Enum
                inClause.value(DangKyKhoaHoc.TrangThaiDangKyHoc.DA_PHAN_LOP); // Chuyển đổi từ String sang Enum
                return inClause;
            });
        }

        Page<DangKyKhoaHoc> dangKyKhoaHocPage = dangKyKhoaHocRepository.findAll(spec, paging);
        return ResponseEntity.ok(dangKyKhoaHocPage);
    }

    @PostMapping("/them")
    public ResponseEntity<?> themDangKyKhoaHoc(@RequestBody DangKyKhoaHocRequest dangKyKhoaHocRequest) {
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(dangKyKhoaHocRequest.getTenDangNhap());
        Optional<KhoaHoc> khoaHoc = khoaHocRepository.findById(dangKyKhoaHocRequest.getMaKhoaHoc());
        if(hocVien == null){
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy sinh viên với tên đăng nhập " +dangKyKhoaHocRequest.getTenDangNhap()), HttpStatus.NOT_FOUND);
        }
        if(khoaHoc.isEmpty()){
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy khóa học "), HttpStatus.NOT_FOUND);
        }
        java.util.Date ngayHienTai = new java.util.Date();
        //lấy ngày thi đầu tiên

        java.util.Date ngayHoc = khoaHoc.get().getNgayBatDau();

        // Tính ngày có thể bắt đầu đăng ký (ngày thi trừ 2 tháng)
        Calendar cal = Calendar.getInstance();
        cal.setTime(ngayHoc);
        cal.add(Calendar.MONTH, -2);
        java.util.Date ngayBatDauDangKy = cal.getTime();

        if (ngayHienTai.before(ngayBatDauDangKy)) {
            // Nếu ngày hiện tại nằm ngoài vòng 2 tháng trước ngày thi
            return ResponseEntity.ok(new MessageResponse("chuatoihan"));
        }
        if(dangKyKhoaHocRepository.existsByKhoaHoc_MaKhoaHocAndHocVien_MaTaiKhoan(dangKyKhoaHocRequest.getMaKhoaHoc(),hocVien.getMaHocVien())){
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }
        if(Objects.equals(khoaHoc.get().getTrangThai(), "DA_DIEN_RA")){
            return new ResponseEntity<>(new MessageResponse("expired"), HttpStatus.OK);
        }

        DangKyKhoaHoc dangKyKhoaHoc = new DangKyKhoaHoc();
        dangKyKhoaHoc.setHocVien(hocVien);
        dangKyKhoaHoc.setKhoaHoc(khoaHoc.get());
        dangKyKhoaHoc.setTrangThaiDangKyHoc(DangKyKhoaHoc.TrangThaiDangKyHoc.CHUA_DUYET);
        LocalDate currentDate = LocalDate.now();
        dangKyKhoaHoc.setNgayDangKy(Date.valueOf(currentDate));

        dangKyKhoaHocRepository.save(dangKyKhoaHoc);
        return ResponseEntity.ok(new MessageResponse("Đăng ký khóa học thành công"));
    }

    @GetMapping("/lay/{maDangKy}")
    public ResponseEntity<?> layDangKyKhoaHoc(@PathVariable Long maDangKy) {
        Optional<DangKyKhoaHoc> dangKyKhoaHoc = dangKyKhoaHocRepository.findById(maDangKy);
        if (dangKyKhoaHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(dangKyKhoaHoc.get());
    }
    @GetMapping("/lay-theo-khoa-hoc-hoc-vien/{maKhoaHoc}/{tenDangNhap}")
    public ResponseEntity<?> layDangKyKhoaHocByKhoaHocAndHocVien(@PathVariable Long maKhoaHoc,
                                                                 @PathVariable String tenDangNhap) {
        DangKyKhoaHoc dangKyKhoaHoc = dangKyKhoaHocRepository.findByHocVien_TaiKhoan_TenDangNhapAndKhoaHoc_MaKhoaHoc(tenDangNhap, maKhoaHoc);

        if (dangKyKhoaHoc == null) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(dangKyKhoaHoc);
    }
    @PutMapping("/cap-nhat-trang-thai/{maDangKy}")
    public ResponseEntity<?> duyetDangKy(@PathVariable Long maDangKy, @RequestBody DangKyKhoaHocRequest dangKyKhoaHocRequest) {
        Optional<DangKyKhoaHoc> dangKyKhoaHocOptional = dangKyKhoaHocRepository.findById(maDangKy);

        if (dangKyKhoaHocOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        DangKyKhoaHoc dangKyKhoaHoc = dangKyKhoaHocOptional.get();
        dangKyKhoaHoc.setTrangThaiDangKyHoc(dangKyKhoaHocRequest.getTrangThaiDangKyHoc());
        dangKyKhoaHocRepository.save(dangKyKhoaHoc);
        String tieuDe = "Duyệt đăng ký khóa học";
        String nDung = "Đăng ký tham gia khóa học " +
                dangKyKhoaHoc.getKhoaHoc().getTenKhoaHoc()+ " của bạn đã được duyệt.";
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                dangKyKhoaHoc.getHocVien().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Cập nhật đăng ký khóa học thành công"));
    }

    @DeleteMapping("/huy/{maDangKy}")
    public ResponseEntity<?> xoaDangKyKhoaHoc(@PathVariable Long maDangKy) {
        if (!dangKyKhoaHocRepository.existsById(maDangKy)) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        dangKyKhoaHocRepository.deleteById(maDangKy);
        return ResponseEntity.ok(new MessageResponse("Hủy đăng ký khóa học thành công"));
    }
    @DeleteMapping("/huy-theo-khoa-hoc-hoc-vien/{maKhoaHoc}")
    public ResponseEntity<?> xoaDangKyKhoaHocByTenDangNhapVaMaKhoaHoc(@PathVariable Long maKhoaHoc,
                                                                      @RequestParam String tenDangNhap) {
        DangKyKhoaHoc dangKyKhoaHoc = dangKyKhoaHocRepository.findByHocVien_TaiKhoan_TenDangNhapAndKhoaHoc_MaKhoaHoc(tenDangNhap, maKhoaHoc);
        if (dangKyKhoaHoc == null) {
            return new ResponseEntity<>(new MessageResponse("Đăng ký khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        dangKyKhoaHocRepository.deleteById(dangKyKhoaHoc.getMaDangKy());
        return ResponseEntity.ok(new MessageResponse("Hủy đăng ký khóa học thành công"));
    }
    @GetMapping("/danh-sach-hoc-vien-da-dong-hoc-phi/{maKhoaHoc}")
    public ResponseEntity<?> getDanhSachHocVienDaDongHocPhi(@PathVariable Long maKhoaHoc) {
        Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(maKhoaHoc);
        if (khoaHocOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        List<DangKyKhoaHoc> danhSachDangKy = dangKyKhoaHocRepository.findAllByKhoaHocAndTrangThaiDangKyHoc(
                khoaHocOptional.get(), DangKyKhoaHoc.TrangThaiDangKyHoc.DA_DUYET
        );

        List<HocVien> danhSachHocVien = danhSachDangKy.stream()
                .map(DangKyKhoaHoc::getHocVien)
                .collect(Collectors.toList());

        return ResponseEntity.ok(danhSachHocVien);
    }
    @PostMapping("/kiem-tra")
    public ResponseEntity<?> kiemTraDangKyKhoaHoc(@RequestBody DangKyKhoaHocRequest dangKyKhoaHocRequest) {

        Optional<KhoaHoc> optionalHoatDong = khoaHocRepository.findById(dangKyKhoaHocRequest.getMaKhoaHoc());
        if (optionalHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("khoahoc-notfound"), HttpStatus.NOT_FOUND);
        }
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(dangKyKhoaHocRequest.getTenDangNhap());
        if (hocVien ==null) {
            return new ResponseEntity<>(new MessageResponse("hocvien-notfound"), HttpStatus.NOT_FOUND);
        }
        KhoaHoc khoaHoc = optionalHoatDong.get();
        boolean check = dangKyKhoaHocRepository.existsByHocVien_TaiKhoan_TenDangNhapAndKhoaHoc_MaKhoaHoc(dangKyKhoaHocRequest.getTenDangNhap(), khoaHoc.getMaKhoaHoc());
        if(check){
            DangKyKhoaHoc dangKyKhoaHoc = dangKyKhoaHocRepository.findByHocVien_TaiKhoan_TenDangNhapAndKhoaHoc_MaKhoaHoc(dangKyKhoaHocRequest.getTenDangNhap(), khoaHoc.getMaKhoaHoc());
            if(dangKyKhoaHoc != null){
                if(dangKyKhoaHoc.getTrangThaiDangKyHoc() == DangKyKhoaHoc.TrangThaiDangKyHoc.DA_DUYET){
                    return new ResponseEntity<>(new MessageResponse("dadong"), HttpStatus.OK);
                }else if(dangKyKhoaHoc.getTrangThaiDangKyHoc() == DangKyKhoaHoc.TrangThaiDangKyHoc.CHUA_DUYET){
                    return new ResponseEntity<>(new MessageResponse("chuadong"), HttpStatus.OK);
                }
                else if(dangKyKhoaHoc.getTrangThaiDangKyHoc() == DangKyKhoaHoc.TrangThaiDangKyHoc.DA_PHAN_LOP){
                    return new ResponseEntity<>(new MessageResponse("daphanlop"), HttpStatus.OK);
                }
            }
        }else {
            return new ResponseEntity<>(new MessageResponse("false"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("unknown"), HttpStatus.OK);
    }
    @DeleteMapping("/xoa-dang-ky-khoa-hoc-chua-duyet-da-dien-ra")
    public ResponseEntity<?> deleteAllChuaDuyetAndDaDienRa() {
        try {
            dangKyKhoaHocRepository.deleteAllChuaDuyetAndDaDienRa();
            return ResponseEntity.ok(new MessageResponse("Deleted successfully"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
    }

}
