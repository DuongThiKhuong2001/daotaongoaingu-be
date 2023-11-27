package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.LichHocPhongDTO;
import com.quanly.trungtamngoaingu.payload.request.LopHocRequest;
import com.quanly.trungtamngoaingu.payload.response.GiaoVienResponse;
import com.quanly.trungtamngoaingu.payload.response.HocVienResponse;
import com.quanly.trungtamngoaingu.payload.response.LopHocResponse;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.*;
import com.quanly.trungtamngoaingu.sercurity.Helpers;
import com.quanly.trungtamngoaingu.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/lop-hoc")
public class LopHocController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private Helpers helpers;
    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private HocVienRepository hocVienRepository;
    @Autowired
    private PhongRepository phongRepository;
    @Autowired
    private KhoaHocRepository khoaHocRepository;
    @Autowired
    private LichHocRepository lichHocRepository;
    @Autowired
    private GiaoVienRepository giaoVienRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private FilesStorageService storageService;
    //lấy danh sách học viên của một lớp học
    @GetMapping("/{maLop}/hoc-vien")
    public ResponseEntity<?> getHocViensByLopHoc(@PathVariable Long maLop) {
        Optional<LopHoc> lopHoc = lopHocRepository.findById(maLop);
        if (lopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Set<HocVien> hocViens = lopHoc.get().getHocViens();
        return ResponseEntity.ok(hocViens);
    }


    //lay số lượng học vien cua mot lop hoc
    @GetMapping("/{maLop}/so-luong-hoc-vien")
    public ResponseEntity<?> getSoLuongHocVienHienTai(@PathVariable Long maLop) {
        Optional<LopHoc> lopHoc = lopHocRepository.findById(maLop);
        if (lopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        int soLuongHocVienHienTai = lopHoc.get().getSoLuongHocVienHienTai();
        return ResponseEntity.ok(soLuongHocVienHienTai);
    }

    @PutMapping("/{maLop}/cap-nhat-phong-hoc-lich-hoc")
    public ResponseEntity<?> capNhatPhongHoc(
            @PathVariable Long maLop,
            @RequestBody LopHocRequest lopHocRequest) {
        Optional<LichHoc> lichHoc = lichHocRepository.findById(lopHocRequest.getMaLichHoc());
        if (lichHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch học không tồn tại"), HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra xem lớp học có tồn tại không
        Optional<LopHoc> lopHocOpt = lopHocRepository.findById(maLop);
        if (lopHocOpt.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        LopHoc lopHoc = lopHocOpt.get();

        if (lopHocRequest.getHinhThucHoc()== LopHoc.HinhThucHoc.Online) {
            lopHoc.setHinhThucHoc(lopHocRequest.getHinhThucHoc());
            lopHoc.setPhong(null);
            lopHoc.setLichHoc(lichHoc.get());
            lopHocRepository.save(lopHoc);
            return new ResponseEntity<>(new MessageResponse("Cập nhật lịch học thành công"), HttpStatus.OK);
        }

        // Kiểm tra xem phòng học có tồn tại không
        Optional<Phong> phongHocOpt = phongRepository.findById(lopHocRequest.getMaPhong());
        if (!phongHocOpt.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Phòng học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Phong phong = phongHocOpt.get();
        lopHoc.setHinhThucHoc(lopHocRequest.getHinhThucHoc());
        // Cập nhật phòng học và lịch học
        lopHoc.setPhong(phong);
        lopHoc.setLichHoc(lichHoc.get());
        // Lưu lại sự thay đổi
        lopHocRepository.save(lopHoc);

        return new ResponseEntity<>(new MessageResponse("Cập nhật phòng học và lịch học thành công"), HttpStatus.OK);
    }

    @PostMapping("/them-hoc-vien-vao-lop")
    public ResponseEntity<?> addHocVienToLopHoc(@RequestBody LopHocRequest lopHocRequest) {
        Optional<LopHoc> lopHoc = lopHocRepository.findById(lopHocRequest.getMaLop());
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(lopHocRequest.getTenTaiKhoan());

        if (lopHoc.isEmpty() || hocVien == null) {
            return new ResponseEntity<>(new MessageResponse("Lớp học hoặc Học viên không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Set<HocVien> hocViens = lopHoc.get().getHocViens();
        if (hocViens.contains(hocVien)) {
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }
        if (hocViens.size() >= lopHoc.get().getSoLuong()) {
            return new ResponseEntity<>(new MessageResponse("maxed"), HttpStatus.OK);
        }
        String tieuDe = "Sắp lớp học";
        String nDung = "Bạn đã được sắp vào lớp " +
                lopHoc.get().getTenLop() + " của " + lopHoc.get().getKhoaHoc().getTenKhoaHoc();
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                hocVien.getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        lopHoc.get().getHocViens().add(hocVien);

        lopHocRepository.save(lopHoc.get());
        return ResponseEntity.ok(new MessageResponse("Thêm học viên vào lớp học thành công"));
    }

    @PostMapping("/{maLop}/chuyen-hoc-vien")
    public ResponseEntity<?> chuyenHocVien(@PathVariable Long maLop, @RequestBody LopHocRequest lopHocRequest) {
        Optional<LopHoc> lopHocCu = lopHocRepository.findById(maLop);
        Optional<LopHoc> lopHocMoi = lopHocRepository.findById(lopHocRequest.getMaLop());
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(lopHocRequest.getTenTaiKhoan());

        if (lopHocCu.isEmpty() || hocVien == null || lopHocMoi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học hoặc Học viên không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Set<HocVien> hocViens = lopHocMoi.get().getHocViens();
        if (hocViens.size() >= lopHocMoi.get().getSoLuong()) {
            return new ResponseEntity<>(new MessageResponse("maxed"), HttpStatus.OK);
        }
        lopHocCu.get().getHocViens().remove(hocVien);
        lopHocMoi.get().getHocViens().add(hocVien);

        lopHocRepository.save(lopHocCu.get());
        lopHocRepository.save(lopHocMoi.get());
        return ResponseEntity.ok(new MessageResponse("Chuyển học viên thành công"));
    }

    @PostMapping("/them")
    public ResponseEntity<?> themLopHoc(@RequestBody LopHocRequest lopHocRequest) {
        LopHoc lopHoc = new LopHoc();

        if (lopHocRepository.existsByTenLop(lopHocRequest.getTenLop())) {
            return new ResponseEntity<>(new MessageResponse("name-exist"), HttpStatus.OK);
        }
        lopHoc.setTenLop(lopHocRequest.getTenLop());
        lopHoc.setSoLuong(lopHocRequest.getSoLuong());
        lopHoc.setHinhThucHoc(lopHocRequest.getHinhThucHoc());
        //lopHoc.setLichHoc(lichHoc.get());
        // Kiểm tra và thiết lập KhoaHoc
        if (lopHocRequest.getMaKhoaHoc() != null) {
            Optional<KhoaHoc> khoaHocOptional =
                    khoaHocRepository.findById(lopHocRequest.getMaKhoaHoc());
            if (khoaHocOptional.isPresent()) {
                lopHoc.setKhoaHoc(khoaHocOptional.get());
            } else {
                return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.BAD_REQUEST);
            }
        }
        lopHocRepository.save(lopHoc);
        return ResponseEntity.ok(new MessageResponse("Thêm lớp học thành công"));
    }

    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> layDanhSachLopHoc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maLop") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);
        Specification<LopHoc> spec = null;

        if (!searchTerm.isEmpty()) {
            spec = (root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("tenLop"), pattern)
                        // Bạn có thể thêm các trường khác để tìm kiếm ở đây
                );
            };
        }
        if (spec == null) {
            spec = (root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        Page<LopHoc> lopHocPage = lopHocRepository.findAll(spec, paging);
        return ResponseEntity.ok(lopHocPage);
    }

    @GetMapping("/lay/{maLopHoc}")
    public ResponseEntity<?> layLopHoc(@PathVariable Long maLopHoc) {
        Optional<LopHoc> lopHoc = lopHocRepository.findById(maLopHoc);
        if (lopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(lopHoc.get());
    }
    //hàm này thiếu sữa phòng
    @PutMapping("/sua/{maLop}")
    public ResponseEntity<?> suaLopHoc(@PathVariable Long maLop, @RequestBody LopHocRequest lopHocRequest) {
        Optional<LopHoc> lopHocOptional = lopHocRepository.findById(maLop);

        if (!lopHocOptional.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Optional<LichHoc> lichHoc = lichHocRepository.findById(lopHocRequest.getMaLichHoc());
        if (lichHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch học không tồn tại"), HttpStatus.BAD_REQUEST);
        }
        LopHoc lopHoc = lopHocOptional.get();

        lopHoc.setTenLop(lopHocRequest.getTenLop());
        lopHoc.setSoLuong(lopHocRequest.getSoLuong());
        lopHoc.setLichHoc(lichHoc.get());
        // Kiểm tra và thiết lập KhoaHoc
        if (lopHocRequest.getMaKhoaHoc() != null) {
            Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(lopHocRequest.getMaKhoaHoc());
            if (khoaHocOptional.isPresent()) {
                lopHoc.setKhoaHoc(khoaHocOptional.get());
            } else {
                return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.BAD_REQUEST);
            }
        }
        lopHocRepository.save(lopHoc);
        return ResponseEntity.ok(new MessageResponse("Cập nhật lớp học thành công"));
    }

    //lấy lớp học theo khoa hoc
    @GetMapping("/khoa-hoc/{maKhoaHoc}")
    public ResponseEntity<?> layLopHocByKhoaHoc(@PathVariable Long maKhoaHoc) {
        // Tìm khóa học bằng maKhoaHoc
        Optional<KhoaHoc> khoaHoc = khoaHocRepository.findById(maKhoaHoc);
        if (!khoaHoc.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        // Tìm danh sách lớp học theo khóa học
        List<LopHoc> lopHocs = lopHocRepository.findByKhoaHoc_MaKhoaHoc(maKhoaHoc);
        List<LopHocResponse> lopHocResponses = new ArrayList<>();

        for (LopHoc lopHoc : lopHocs) {
            LopHocResponse lopHocResponse = new LopHocResponse();
            lopHocResponse.setMaLop(lopHoc.getMaLop());
            lopHocResponse.setTenLop(lopHoc.getTenLop());
            lopHocResponse.setSoLuong(lopHoc.getSoLuong());
            lopHocResponse.setLichHoc(lopHoc.getLichHoc());
            lopHocResponse.setPhong(lopHoc.getPhong());
            lopHocResponse.setKhoaHoc(lopHoc.getKhoaHoc());
            lopHocResponse.setGiaoVien(lopHoc.getGiaoVien());
            lopHocResponse.setHinhThucHoc(lopHoc.getHinhThucHoc());
            lopHocResponse.setSoLuongHocVien(lopHoc.getHocViens().size()); // Đếm số lượng học viên
            lopHocResponses.add(lopHocResponse);
        }

        return ResponseEntity.ok(lopHocResponses);
    }

    @DeleteMapping("/xoa/{maLopHoc}")
    public ResponseEntity<?> xoaLopHoc(@PathVariable Long maLopHoc) {
        if (!lopHocRepository.existsById(maLopHoc)) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Optional<LopHoc> optLopHoc = lopHocRepository.findById(maLopHoc);

        if (optLopHoc.isPresent()) {
            LopHoc lopHoc = optLopHoc.get();
            if (lopHoc.getHocViens() != null && !lopHoc.getHocViens().isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.CONFLICT);
            }

            lopHocRepository.deleteById(maLopHoc);
            return ResponseEntity.ok(new MessageResponse("Xóa lớp học thành công"));
        } else {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }
    //lấy danh sách lich học có phòng trống
    @GetMapping("/{maLop}/lich-hoc-phong-trong")
    public ResponseEntity<?> getAvailableLichHoc(
            @PathVariable Long maLop) {

        // Lấy thông tin lớp học
        Optional<LopHoc> lopHocOpt = lopHocRepository.findById(maLop);
        if (!lopHocOpt.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        LopHoc lopHoc = lopHocOpt.get();

        // Lấy thông tin khóa học
        KhoaHoc khoaHoc = lopHoc.getKhoaHoc();
        Date ngayBatDau = khoaHoc.getNgayBatDau();
        Date ngayKetThuc = khoaHoc.getNgayKetThuc();

        // Lấy danh sách tất cả lịch học và phòng
        List<LichHoc> allLichHoc = lichHocRepository.findAll();
        List<Phong> allPhong = phongRepository.findAll();

        // Tạo danh sách để lưu lịch học trống
        Set<LichHoc> availableLichHoc = new HashSet<>();

        // Kiểm tra từng lịch học và phòng
        for (LichHoc lichHoc : allLichHoc) {
            for (Phong phong : allPhong) {
                boolean isAvailable = lopHocRepository
                        .findByLichHocAndPhongAndKhoaHoc_NgayBatDauBeforeAndKhoaHoc_NgayKetThucAfter(
                                lichHoc, phong, ngayKetThuc, ngayBatDau).isEmpty();
                if (isAvailable) {
                    availableLichHoc.add(lichHoc);
                }
            }
        }

        // Kiểm tra xem có lịch học nào khả dụng không
        if (availableLichHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("no"), HttpStatus.OK);
        }
        return ResponseEntity.ok(availableLichHoc);
    }
    @GetMapping("/{maLop}/lich-hoc/{maLichHoc}/phong-trong")
    public ResponseEntity<?> getAvailablePhongForLichHoc(
            @PathVariable Long maLop,
            @PathVariable Long maLichHoc) {

        // Lấy thông tin lớp học
        Optional<LopHoc> lopHocOpt = lopHocRepository.findById(maLop);
        if (!lopHocOpt.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        // Lấy thông tin lịch học
        Optional<LichHoc> lichHocOpt = lichHocRepository.findById(maLichHoc);
        if (!lichHocOpt.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Lịch học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        LichHoc lichHoc = lichHocOpt.get();

        // Lấy thông tin khóa học
        KhoaHoc khoaHoc = lopHocOpt.get().getKhoaHoc();
        Date ngayBatDau = khoaHoc.getNgayBatDau();
        Date ngayKetThuc = khoaHoc.getNgayKetThuc();

        // Lấy danh sách tất cả phòng
        List<Phong> allPhong = phongRepository.findAllByLoaiPhong(Phong.LoaiPhong.Hoc);

        // Tạo danh sách để lưu phòng trống
        Set<Phong> availablePhong = new HashSet<>();

        // Kiểm tra từng phòng
        for (Phong phong : allPhong) {
            boolean isAvailable = lopHocRepository
                    .findByLichHocAndPhongAndKhoaHoc_NgayBatDauBeforeAndKhoaHoc_NgayKetThucAfter(
                            lichHoc, phong, ngayKetThuc, ngayBatDau).isEmpty();
            if (isAvailable) {
                availablePhong.add(phong);
            }
        }

        // Kiểm tra xem có phòng nào khả dụng không
        if (availablePhong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không có phòng nào khả dụng"), HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(availablePhong);
    }

    @PostMapping("/{maLop}/them-giao-vien/{maGiaoVien}")
    public ResponseEntity<?> addGiaoVienToLopHoc(
            @PathVariable Long maLop,
            @PathVariable Long maGiaoVien) {

        // Tìm lớp học theo mã lớp
        Optional<LopHoc> lopHocOpt = lopHocRepository.findById(maLop);
        if (!lopHocOpt.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        LopHoc lopHoc = lopHocOpt.get();

        // Tìm giáo viên theo mã giáo viên
        Optional<GiaoVien> giaoVienOptional = giaoVienRepository.findById(maGiaoVien);
        if (giaoVienOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên không tồn tại"), HttpStatus.NOT_FOUND);
        }

        // Kiểm tra xem giáo viên đã dạy lớp nào khác trong cùng kí hiệu lịch học
        List<LopHoc> conflictingLopHoc = lopHocRepository.findByLichHocAndKhoaHoc_NgayBatDauBeforeAndKhoaHoc_NgayKetThucAfterAndGiaoVien(
                lopHoc.getLichHoc(),
                lopHoc.getKhoaHoc().getNgayBatDau(),
                lopHoc.getKhoaHoc().getNgayKetThuc(),
                giaoVienOptional.get()
        );

        if (!conflictingLopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên đã dạy lớp học khác trong cùng kí hiệu lịch học"), HttpStatus.BAD_REQUEST);
        }

        // Thêm giáo viên vào lớp học
        lopHoc.setGiaoVien(giaoVienOptional.get());
        lopHocRepository.save(lopHoc);
        String tieuDe = "Phân công giảng dạy";
        String nDung = "Bạn được phân công giảng dạy cho lớp " +
                lopHoc.getTenLop() + " của khóa "+
                lopHoc.getKhoaHoc().getTenKhoaHoc()
                ;
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                giaoVienOptional.get().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Thêm giáo viên vào lớp học thành công"));
    }
    @GetMapping("/{maLop}/danh-sach-giao-vien-hop-le")
    public ResponseEntity<?> getDanhSachGiaoVienHopLe(@PathVariable Long maLop) {
        // Tìm lớp học theo mã lớp
        Optional<LopHoc> lopHocOpt = lopHocRepository.findById(maLop);
        if (!lopHocOpt.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        if(lopHocOpt.get().getLichHoc() == null){
            return new ResponseEntity<>(new MessageResponse("null"), HttpStatus.OK);
        }
        LopHoc lopHoc = lopHocOpt.get();

        // Lấy kí hiệu lịch học của lớp học
        LichHoc lichHoc = lopHoc.getLichHoc();

        // Lấy ngày bắt đầu và ngày kết thúc của khoá học
        Date ngayBatDau = lopHoc.getKhoaHoc().getNgayBatDau();
        Date ngayKetThuc = lopHoc.getKhoaHoc().getNgayKetThuc();

        // Lấy danh sách giáo viên hợp lệ
        List<GiaoVien> danhSachGiaoVienHopLe = giaoVienRepository.findValidGiaoVienForLopHoc(
                lichHoc.getKiHieu(),
                ngayBatDau,
                ngayKetThuc
        );
        System.out.println(danhSachGiaoVienHopLe);
        List<GiaoVienResponse> giaoVienResponses = danhSachGiaoVienVaSoLuongLopHocHienTai(danhSachGiaoVienHopLe);
        return ResponseEntity.ok(giaoVienResponses);
    }
    public List<GiaoVienResponse> danhSachGiaoVienVaSoLuongLopHocHienTai(List<GiaoVien> danhSachGiaoVien) {
        List<GiaoVienResponse> danhSachGiaoVienResponse = new ArrayList<>();

        for (GiaoVien giaoVien : danhSachGiaoVien) {
            int soLuongLopHocHienTai = tinhSoLuongLopHocHienTai(giaoVien);
            GiaoVienResponse giaoVienResponse = new GiaoVienResponse(giaoVien, soLuongLopHocHienTai);
            danhSachGiaoVienResponse.add(giaoVienResponse);
        }

        return danhSachGiaoVienResponse;
    }
    private int tinhSoLuongLopHocHienTai(GiaoVien giaoVien) {
        int soLuongLopHocHienTai = 0;
        Date ngayHienTai = new Date();

        List<LopHoc> danhSachLopHocCuaGiaoVien = lopHocRepository.findByGiaoVien(giaoVien);

        for (LopHoc lopHoc : danhSachLopHocCuaGiaoVien) {
            if (lopHoc.getKhoaHoc() != null && lopHoc.getKhoaHoc().getNgayBatDau() != null
                    && lopHoc.getKhoaHoc().getNgayKetThuc() != null) {
                if (ngayHienTai.after(lopHoc.getKhoaHoc().getNgayBatDau())
                        && ngayHienTai.before(lopHoc.getKhoaHoc().getNgayKetThuc())) {
                    soLuongLopHocHienTai++;
                }
            }
        }

        return soLuongLopHocHienTai;
    }
    @GetMapping("/so-luong-lop-day-hien-tai-cua-giao-vien/{tenGiaoVien}")
    private int tinhSoLuongLopHocHienTai(@PathVariable String tenGiaoVien) {
        int soLuongLopHocHienTai = 0;
        Date ngayHienTai = new Date();

        List<LopHoc> danhSachLopHocCuaGiaoVien = lopHocRepository.findByGiaoVien_TaiKhoan_TenDangNhap(tenGiaoVien);

        for (LopHoc lopHoc : danhSachLopHocCuaGiaoVien) {
            if (lopHoc.getKhoaHoc() != null && lopHoc.getKhoaHoc().getNgayBatDau() != null
                    && lopHoc.getKhoaHoc().getNgayKetThuc() != null) {
                if (ngayHienTai.after(lopHoc.getKhoaHoc().getNgayBatDau())
                        && ngayHienTai.before(lopHoc.getKhoaHoc().getNgayKetThuc())) {
                    soLuongLopHocHienTai++;
                }
            }
        }

        return soLuongLopHocHienTai;
    }

    //hoc viên
    @GetMapping("/tim-lop-hoc/{tenDangNhap}/{khoaHocId}")
    public ResponseEntity<?> timLopHoc(@PathVariable String tenDangNhap, @PathVariable Long khoaHocId) {
        // Tìm học viên bằng tên đăng nhập
        HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (hocVien == null) {
            return new ResponseEntity<>(new MessageResponse("Khôn tìm thấy học viên!"), HttpStatus.NOT_FOUND);
        }

        // Tìm lớp học của học viên trong khóa học cụ thể
        Optional<LopHoc> lopHocOptional = hocVien.getLopHocs().stream()
                .filter(lopHoc -> lopHoc.getKhoaHoc().getMaKhoaHoc().equals(khoaHocId))
                .findFirst();

        if (lopHocOptional.isPresent()) {
            LopHoc lopHoc = lopHocOptional.get();
            return ResponseEntity.ok(lopHoc);
        } else {
            return new ResponseEntity<>(new MessageResponse("no-content"), HttpStatus.OK);
        }
    }
    //giao viên
    @GetMapping("/lay-lich-hoc-cua-mot-giao-vien/{tenDangNhap}")
    public ResponseEntity<?> lichDayCuaGiaoVien(@PathVariable String tenDangNhap) {
        GiaoVien giaoVien = giaoVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (giaoVien ==null) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<LichHoc> lichDay = lopHocRepository.findLichHocByGiaoVien(giaoVien.getMaTaiKhoan());

        return ResponseEntity.ok(lichDay);
    }
    @GetMapping("/lay-lop-hoc-cua-mot-giao-vien/{tenDangNhap}")
    public ResponseEntity<?> lopHocCuaGiaoVien(@PathVariable String tenDangNhap) {
        GiaoVien giaoVien = giaoVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (giaoVien ==null) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<LopHoc> lichDay = lopHocRepository.findByGiaoVien(giaoVien);
        return ResponseEntity.ok(lichDay);
    }

    @PostMapping("/gui-thong-bao/{maLopHoc}")
    public ResponseEntity<?> guiThongBao(@PathVariable Long maLopHoc, @RequestBody ThongBao request,
                                         HttpServletRequest httpServletRequest) {
        Optional<LopHoc> optLopHoc = lopHocRepository.findById(maLopHoc);
        if (optLopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        LopHoc lopHoc = optLopHoc.get();

        // Kiểm tra xem người gửi có phải là giáo viên của lớp không
        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);
        GiaoVien giaoViens = lopHoc.getGiaoVien();

        if (currentUser != giaoViens.getTaiKhoan()) {
            return new ResponseEntity<>(new MessageResponse("Bạn không thể gửi thông báo cho lớp này!"), HttpStatus.BAD_REQUEST);
        }

        // Lấy danh sách học viên trong lớp
        Set<HocVien> hocViens = lopHoc.getHocViens();

        // Tạo danh sách email cần gửi
        List<String> emailList = new ArrayList<>();

        // Gửi thông báo đến từng học viên và thêm địa chỉ email vào danh sách
        for (HocVien hocVien : hocViens) {
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    hocVien.getTaiKhoan(),
                    request.getTieuDe(), request.getNoiDung(), ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
            emailList.add(hocVien.getTaiKhoan().getEmail());
        }

        // Gửi email đồng thời cho tất cả học viên
        sendEmails(emailList, request);
        return ResponseEntity.ok(new MessageResponse("Gửi thông báo thành công"));

    }
    @Async
    public void sendEmails(List<String> emailList, ThongBao request) {
        for (String email : emailList) {
            emailService.sendEmail(email, request.getTieuDe(), request.getNoiDung());
        }
    }

    //lấy danh sách học viên điểm danh của một lớp học
    @GetMapping("/{maLop}/hoc-vien-diem-danh")
    public ResponseEntity<List<HocVienResponse>> getHocViensByMaLop(@PathVariable Long maLop) {
        Optional<LopHoc> lopHocOptional = lopHocRepository.findById(maLop);

        if (lopHocOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LopHoc lopHoc = lopHocOptional.get();
        Set<HocVien> hocViens = lopHoc.getHocViens();

        List<HocVienResponse> hocVienResponses = hocViens.stream().map(hv -> {
            HocVienResponse response = new HocVienResponse();
            response.setHoTen(hv.getTaiKhoan().getHoTen());
            response.setTenDangNhap(hv.getTaiKhoan().getTenDangNhap());
            response.setSoDienThoai(hv.getTaiKhoan().getSoDienThoai());
            response.setDiaChi(hv.getTaiKhoan().getDiaChi());
            response.setEmail(hv.getTaiKhoan().getEmail());
            response.setGioiTinh(hv.getTaiKhoan().getGioiTinh());
            response.setNgaySinh(hv.getTaiKhoan().getNgaySinh());

            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(hocVienResponses);
    }
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }
    @PutMapping("/sua-file/{maLopHoc}")
    public ResponseEntity<?> suaFileDiemDanh(@PathVariable Long maLopHoc, @RequestParam("file") MultipartFile file) {
        Optional<LopHoc> lopHocOptional = lopHocRepository.findById(maLopHoc);
        if (lopHocOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }

        String newFileName = null;
        try {
            // Xóa tệp cũ (nếu có)
            String oldFile = lopHocOptional.get().getFileDiemDanh();
            if (oldFile != null) {
                storageService.delete(oldFile);
            }

            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            baseName = Helpers.createSlug(baseName);
            // Tạo một chuỗi timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            newFileName = timestamp + "_" + baseName + fileExtension;
            storageService.saveRandom(file, newFileName);

            // Cập nhật tên file mới
            LopHoc lopHoc = lopHocOptional.get();
            lopHoc.setFileDiemDanh(newFileName);

            lopHocRepository.save(lopHoc);
            return ResponseEntity.ok(lopHoc);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not update file for HoatDongNgoaiTruong. Error: " + e.getMessage()));
        }
    }
    @GetMapping("/{maLopHoc}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long maLopHoc) {
        Optional<LopHoc> lopHocOptional = lopHocRepository.findById(maLopHoc);
        if (lopHocOptional.isPresent()) {
            try {
                String fileName = lopHocOptional.get().getFileDiemDanh();
                if (fileName != null) {
                    Resource file = storageService.load(fileName); // Giả định storageService có một hàm load() để lấy file dựa trên tên

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                            .body(file);
                } else {
                    return new ResponseEntity<>(new MessageResponse("Không có tệp được đính kèm cho hoạt động ngoại trường này"), HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoại trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{maLopHoc}/ten-file")
    public ResponseEntity<?> getFileName(@PathVariable Long maLopHoc) {
        Optional<LopHoc> lopHocOptional = lopHocRepository.findById(maLopHoc);
        if (lopHocOptional.isPresent()) {
            try {
                String fileName = lopHocOptional.get().getFileDiemDanh();
                if (fileName != null) {
                    // Lấy tên tài liệu từ tên tệp
                    int underscoreIndex = fileName.indexOf("_");
                    if (underscoreIndex > 0 && underscoreIndex < fileName.length() - 1) {
                        String originalFileName = fileName.substring(underscoreIndex + 1);
                        return ResponseEntity.ok(originalFileName);
                    } else {
                        return new ResponseEntity<>(new MessageResponse("Không thể trích xuất tên tài liệu từ tên tệp"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>(new MessageResponse("Không có tệp được đính kèm cho hoạt động ngoại trường này"), HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not extract original filename. Error: " + e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoại trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{maKhoaHoc}")
    public ResponseEntity<?> getLopHocForHocVienInKhoaHoc(
            @PathVariable Long maKhoaHoc,
            HttpServletRequest httpServletRequest) {
        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);

        LopHoc lopHoc = lopHocRepository.findUniqueLopHocByMaKhoaHocAndMaHocVien(maKhoaHoc, currentUser.getMaTaiKhoan());

        if (lopHoc != null) {
            // Lớp học đã được tìm thấy cho học viên trong khóa học
            return ResponseEntity.ok(lopHoc);
        } else {
            // Không tìm thấy lớp học cho học viên trong khóa học
            return new ResponseEntity<>(new MessageResponse("notfound"), HttpStatus.OK);
        }
    }
}


