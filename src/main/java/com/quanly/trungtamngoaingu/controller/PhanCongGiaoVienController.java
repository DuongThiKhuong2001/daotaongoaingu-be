package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.GiaoVienGacThiRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;

import com.quanly.trungtamngoaingu.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/phan-cong-giao-vien")
public class PhanCongGiaoVienController {

    @Autowired
    private PhanCongGiaoVienRepository phanCongGiaoVienRepository;
    @Autowired
    private GiaoVienRepository giaoVienRepository;

    @Autowired
    private LichThiRepository lichThiRepository;
    @Autowired
    private LopHocRepository lopHocRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @PostMapping("/them-gac-thi")
    public ResponseEntity<?> themGiaoVienGacThi(@RequestBody GiaoVienGacThiRequest request) {
        Optional<GiaoVien> giaoVien = giaoVienRepository.findById(request.getMaGiaoVien());
        Optional<LichThi> lichThi = lichThiRepository.findById(request.getMaLichThi());

        if (giaoVien.isEmpty() || lichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        LichThi lichThiRequest = lichThi.get();
        if (lichThi.get().getPhong() == null) {
            return new ResponseEntity<>(new MessageResponse("phongnull"), HttpStatus.OK);
        }
        List<PhanCongGiaoVien> existingAssignments = phanCongGiaoVienRepository.findByGiaoVien_MaTaiKhoanAndLichThi_NgayThiAndLichThi_CaThiAndLoaiPhanCong(
                request.getMaGiaoVien(),
                lichThiRequest.getNgayThi(),
                    lichThiRequest.getCaThi(),
                PhanCongGiaoVien.LoaiPhanCong.Gac_Thi
            );

        if (!existingAssignments.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên đã được phân công gác thi tại phòng khác trong cùng ngày và ca"), HttpStatus.BAD_REQUEST);
        }
        PhanCongGiaoVien phanCongGiaoVien = new PhanCongGiaoVien();
        phanCongGiaoVien.setGiaoVien(giaoVien.get());
        phanCongGiaoVien.setLichThi(lichThi.get());
        phanCongGiaoVien.setLoaiPhanCong(PhanCongGiaoVien.LoaiPhanCong.Gac_Thi);
        phanCongGiaoVienRepository.save(phanCongGiaoVien);
        String tieuDe = "Phân công gác thi";
        String nDung = "Bạn được phân công gác thi cho kỳ thi " +
                lichThi.get().getKyThi().getChungChi().getTenChungChi() +
                lichThi.get().getKyThi().getThangThi()+"/"+
                lichThi.get().getKyThi().getNamThi()+ " vào "+
                lichThi.get().getNgayThi() +" ca "+
                lichThi.get().getCaThi() + " ở "+
                lichThi.get().getPhong().getTenPhong()+ " tại "+
                lichThi.get().getPhong().getViTri()
        ;
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                giaoVien.get().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Thêm giáo viên gác thi thành công"));
    }

    @GetMapping("/lay-gv-gac-thi-theo-lich-thi/{maLichThi}")
    public ResponseEntity<?> layGiaoVienGacThiTheoLichThi(@PathVariable Long maLichThi) {
        List<GiaoVien> giaoViens = phanCongGiaoVienRepository.findByLichThi_MaLichThiAndLoaiPhanCong(maLichThi, PhanCongGiaoVien.LoaiPhanCong.Gac_Thi)
                .stream()
                .map(PhanCongGiaoVien::getGiaoVien)
                .collect(Collectors.toList());

        if (giaoViens.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("null"), HttpStatus.OK);
        }

        return ResponseEntity.ok(giaoViens);
    }
    @GetMapping("/lay-theo-ten-dang-nhap-gac-thi/{tenDangNhap}")
    public ResponseEntity<?> layLichThiTheoTenDangNhapGacThi(@PathVariable String tenDangNhap) {
        GiaoVien giaoVien = giaoVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (giaoVien ==null) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<LichThi> dsLichThi = phanCongGiaoVienRepository.findLichThiByGiaoVienAndLoaiPhanCongGT(giaoVien.getMaTaiKhoan());


        return ResponseEntity.ok(dsLichThi);
    }

    @PostMapping("/check-giao-vien-day/{maGiaoVien}/{maLichThi}")
    public ResponseEntity<?> checkGiaoVienDay(@PathVariable Long maGiaoVien, @PathVariable Long maLichThi) {
        Optional<GiaoVien> giaoVien = giaoVienRepository.findById(maGiaoVien);
        Optional<LichThi> lichThi = lichThiRepository.findById(maLichThi);
        if (giaoVien.isEmpty() || lichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không có giáo viên và lịch thi"), HttpStatus.NOT_FOUND);
        }
        if(checkGiaoVienTeachingOnExamDate(giaoVien.get(),lichThi.get())){
            return new ResponseEntity<>(new MessageResponse("true"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("false"), HttpStatus.OK);
    }
    public boolean checkGiaoVienTeachingOnExamDate(GiaoVien giaoVien, LichThi lichThi) {
        List<LopHoc> lopHocs = lopHocRepository.findByGiaoVien(giaoVien); // giả sử bạn có phương thức này trong repository
        for (LopHoc lopHoc : lopHocs) {
            if (lopHoc.getLichHoc() == null) {
                continue;
            }
            LichHoc lichHoc = lopHoc.getLichHoc();
            KhoaHoc khoaHoc = lopHoc.getKhoaHoc();
            List<Map<String, Object>> schedule = generateSchedule(lichHoc.getKiHieu(), khoaHoc.getNgayBatDau(), khoaHoc.getNgayKetThuc());

            for (Map<String, Object> session : schedule) {
                Date ngayHoc = (Date) session.get("ngay");
                String buoiHoc = (String) session.get("buoi");
                if (ngayHoc.equals(lichThi.getNgayThi())) {
                    if (lichThi.getCaThi().toString().equalsIgnoreCase(buoiHoc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Map<String, Object>> generateSchedule(String kiHieu, Date startDate, Date endDate) {
        List<Map<String, Object>> schedule = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        String buoi = kiHieu.substring(0, kiHieu.length() - 3);
        String thu = kiHieu.substring(kiHieu.length() - 3);

        Set<Integer> daysOfWeek = new HashSet<>();
        for (char c : thu.toCharArray()) {
            daysOfWeek.add(Character.getNumericValue(c));
        }

        while (cal.getTime().before(endDate) || cal.getTime().equals(endDate)) {
            if (daysOfWeek.contains(cal.get(Calendar.DAY_OF_WEEK))) {
                Map<String, Object> session = new HashMap<>();
                session.put("ngay", cal.getTime());
                session.put("buoi", buoi.equals("CASANG") ? "Sang" : buoi.equals("CACHIEU") ? "Chieu" : "Toi");
                schedule.add(session);
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return schedule;
    }
    @GetMapping("/lay-giao-vien-gac-thi-hop-le/{maLichThi}")
    public ResponseEntity<?> layGiaoVienGacThiHopLe(@PathVariable Long maLichThi) {
        Optional<LichThi> existingLichThi = lichThiRepository.findById(maLichThi);
        if (existingLichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        List<GiaoVien> allGiaoViens = giaoVienRepository.findAll();
        List<GiaoVien> assignedGiaoViens = phanCongGiaoVienRepository.findGiaoViensAssignedOnSameDateAndShift(existingLichThi.get().getNgayThi(), existingLichThi.get().getCaThi(), PhanCongGiaoVien.LoaiPhanCong.Gac_Thi);

        allGiaoViens.removeAll(assignedGiaoViens);

        return ResponseEntity.ok(allGiaoViens);
    }
    @DeleteMapping("/xoa-gac-thi/{maGiaoVien}/{maLichThi}")
    public ResponseEntity<?> xoaGiaoVienGacThi(@PathVariable Long maGiaoVien, @PathVariable Long maLichThi) {
        Optional<GiaoVien> giaoVien = giaoVienRepository.findById(maGiaoVien);
        Optional<LichThi> lichThi = lichThiRepository.findById(maLichThi);

        if (giaoVien.isEmpty() || lichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        List<PhanCongGiaoVien> assignments = phanCongGiaoVienRepository.findByGiaoVien_MaTaiKhoanAndLichThi_MaLichThi(maGiaoVien, maLichThi);

        if(assignments.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("null"), HttpStatus.OK);
        }

        phanCongGiaoVienRepository.deleteAll(assignments);

        return new ResponseEntity<>(new MessageResponse("Đã xóa giáo viên gác thi thành công"), HttpStatus.OK);
    }
    @PostMapping("/them-len-diem")
    public ResponseEntity<?> themGiaoVienLenDiem(@RequestBody GiaoVienGacThiRequest request) {
        Optional<GiaoVien> giaoVien = giaoVienRepository.findById(request.getMaGiaoVien());
        Optional<LichThi> lichThi = lichThiRepository.findById(request.getMaLichThi());

        if (giaoVien.isEmpty() || lichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        if (lichThi.get().getPhong() == null) {
            return new ResponseEntity<>(new MessageResponse("phongnull"), HttpStatus.OK);
        }
        PhanCongGiaoVien phanCongGiaoVien = new PhanCongGiaoVien();
        phanCongGiaoVien.setGiaoVien(giaoVien.get());
        phanCongGiaoVien.setLichThi(lichThi.get());
        phanCongGiaoVien.setLoaiPhanCong(PhanCongGiaoVien.LoaiPhanCong.Len_Diem);
        phanCongGiaoVienRepository.save(phanCongGiaoVien);
        String tieuDe = "Phân công lên điểm";
        String nDung = "Bạn được phân công lên điểm cho kỳ thi " +
                lichThi.get().getKyThi().getChungChi().getTenChungChi() +
                lichThi.get().getKyThi().getThangThi()+"/"+
                lichThi.get().getKyThi().getNamThi()+ " vào "+
                lichThi.get().getNgayThi() +" ca "+
                lichThi.get().getCaThi() + " ở "+
                lichThi.get().getPhong().getTenPhong()+ " tại "+
                lichThi.get().getPhong().getViTri()
                ;
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                giaoVien.get().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Thêm giáo viên ln điểm thành công"));
    }

    @GetMapping("/lay-gv-len-diem-theo-lich-thi/{maLichThi}")
    public ResponseEntity<?> layGiaoVienLenDiemTheoLichThi(@PathVariable Long maLichThi) {
        List<GiaoVien> giaoViens = phanCongGiaoVienRepository.findByLichThi_MaLichThiAndLoaiPhanCong(maLichThi, PhanCongGiaoVien.LoaiPhanCong.Len_Diem)
                .stream()
                .map(PhanCongGiaoVien::getGiaoVien)
                .collect(Collectors.toList());

        if (giaoViens.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("null"), HttpStatus.OK);
        }

        return ResponseEntity.ok(giaoViens);
    }
    @GetMapping("/lay-theo-ten-dang-nhap-len-diem/{tenDangNhap}")
    public ResponseEntity<?> layTheoTenDangNhapLenDiem(@PathVariable String tenDangNhap) {
        List<PhanCongGiaoVien> dsLichThi = phanCongGiaoVienRepository.findByGiaoVien_TaiKhoan_TenDangNhapAndLoaiPhanCong(tenDangNhap, PhanCongGiaoVien.LoaiPhanCong.Len_Diem);
        if (dsLichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("null"), HttpStatus.OK);
        }
        return ResponseEntity.ok(dsLichThi);
    }
    @GetMapping("/lay-lich-thi-theo-ten-dang-nhap-len-diem/{tenDangNhap}")
    public ResponseEntity<?> layLichThiTheoTenDangNhapLenDiem(@PathVariable String tenDangNhap) {
        GiaoVien giaoVien = giaoVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (giaoVien ==null) {
            return new ResponseEntity<>(new MessageResponse("Giáo viên hoặc lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<LichThi> dsLichThi = phanCongGiaoVienRepository.findLichThiByGiaoVienAndLoaiPhanCong(giaoVien.getMaTaiKhoan());

        return ResponseEntity.ok(dsLichThi);
    }

    @GetMapping("/lay-giao-vien-len-diem-hop-le/{maLichThi}")
    public ResponseEntity<?> layGiaoVienLenDiemHopLe(@PathVariable Long maLichThi) {
        Optional<LichThi> existingLichThi = lichThiRepository.findById(maLichThi);
        if (existingLichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        List<GiaoVien> allGiaoViens = giaoVienRepository.findAll();
        List<GiaoVien> assignedGiaoViens = phanCongGiaoVienRepository.findGiaoViensAssignedOnSameDateAndShift(existingLichThi.get().getNgayThi(), existingLichThi.get().getCaThi(), PhanCongGiaoVien.LoaiPhanCong.Len_Diem);

        allGiaoViens.removeAll(assignedGiaoViens);

        return ResponseEntity.ok(allGiaoViens);
    }
    @GetMapping("/lay-ngay-hoc-theo-lop/{maLopHoc}")
    public ResponseEntity<?> layNgayHocTheoLop(@PathVariable Long maLopHoc) {
        Optional<LopHoc> lopHoc = lopHocRepository.findById(maLopHoc);
        if (lopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        LichHoc lichHoc = lopHoc.get().getLichHoc();
        if (lichHoc == null) {
            return new ResponseEntity<>(new MessageResponse("Lớp học này không có lịch học"), HttpStatus.OK);
        }

        KhoaHoc khoaHoc = lopHoc.get().getKhoaHoc();
        List<Map<String, Object>> schedule = generateSchedule(lichHoc.getKiHieu(), khoaHoc.getNgayBatDau(), khoaHoc.getNgayKetThuc());

        return ResponseEntity.ok(schedule);
    }
}
