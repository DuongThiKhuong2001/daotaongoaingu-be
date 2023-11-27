package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.LichThiRequest;
import com.quanly.trungtamngoaingu.payload.response.HocVienResponse;
import com.quanly.trungtamngoaingu.payload.response.LichThiResponse;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.payload.response.NhapDiemResponse;
import com.quanly.trungtamngoaingu.repository.*;
import com.quanly.trungtamngoaingu.sercurity.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/lich-thi")
public class LichThiController {

    @Autowired
    private LichThiRepository lichThiRepository;

    @Autowired
    private KyThiRepository kyThiRepository;
    @Autowired
    private PhongRepository phongRepository;
    @Autowired
    private DangKyThiRepository dangKyThiRepository;
    @Autowired
    private FilesStorageService storageService;
    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<LichThi>> layTatCaLichThi() {
        return ResponseEntity.ok(lichThiRepository.findAll());
    }

    @GetMapping("/lay/{maLichThi}")
    public ResponseEntity<?> layLichThi(@PathVariable Long maLichThi) {
        Optional<LichThi> lichThi = lichThiRepository.findById(maLichThi);
        if (lichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(lichThi.get());
    }

    @GetMapping("/lay-theo-ky-thi/{maKyThi}")
    public ResponseEntity<?> layLichThiKyThi(@PathVariable Long maKyThi) {
        Optional<KyThi> kyThi = kyThiRepository.findById(maKyThi);
        if (kyThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Kỳ thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<LichThi> lichThiList = lichThiRepository.findByKyThi(kyThi.get());
        if (lichThiList.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        // Create the response
        List<LichThiResponse> responses = new ArrayList<>();
        for (LichThi lt : lichThiList) {
            LichThiResponse response = new LichThiResponse();
            response.setMaLichThi(lt.getMaLichThi());
            response.setKyThi(lt.getKyThi());
            response.setNgayThi(lt.getNgayThi());
            response.setPhong(lt.getPhong());
            response.setCaThi(lt.getCaThi());

            // Set the registration count
            int count = dangKyThiRepository.countByLichThiId(lt.getMaLichThi());
            response.setSoLuongDangKy(count);

            responses.add(response);
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/them")
    public ResponseEntity<?> themLichThi(@RequestBody LichThiRequest lichThiRequest) {
        Optional<KyThi> kyThi = kyThiRepository.findById(lichThiRequest.getMaKyThi());
        Optional<Phong> phong = phongRepository.findById(lichThiRequest.getMaPhong());
        if (kyThi.isEmpty() || phong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Kỳ thi hoặc phòng không tồn tại"), HttpStatus.NOT_FOUND);
        }

        // Kiểm tra xem có lịch thi nào trùng ngày, phòng và ca thi không
        List<LichThi> trungLichThi = lichThiRepository.findByNgayThiAndPhongAndCaThi(
                lichThiRequest.getNgayThi(),
                phong.get(),
                lichThiRequest.getCaThi()
        );
        if (!trungLichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Đã có lịch thi trùng ngày, phòng và ca thi"), HttpStatus.BAD_REQUEST);
        }

        LichThi lichThi = new LichThi();
        lichThi.setCaThi(lichThiRequest.getCaThi());
        lichThi.setPhong(phong.get());
        lichThi.setNgayThi(lichThiRequest.getNgayThi());
        lichThi.setKyThi(kyThi.get());
        lichThiRepository.save(lichThi);

        return ResponseEntity.ok(new MessageResponse("Thêm lịch thi thành công"));
    }

    // Các hàm khác không thay đổi

    @PutMapping("/sua/{maLichThi}")
    public ResponseEntity<?> suaLichThi(@PathVariable Long maLichThi, @RequestBody LichThiRequest lichThiRequest) {
        if (!lichThiRepository.existsById(maLichThi)) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Optional<LichThi> lichThiOptional = lichThiRepository.findById(maLichThi);
        if(lichThiOptional.isPresent()){
            LichThi lichThi=lichThiOptional.get();
            Optional<KyThi> kyThi = kyThiRepository.findById(lichThiRequest.getMaKyThi());
            Optional<Phong> phong = phongRepository.findById(lichThiRequest.getMaPhong());
            if (kyThi.isEmpty() || phong.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Kỳ thi hoặc phòng không tồn tại"), HttpStatus.NOT_FOUND);
            }
            List<LichThi> trungLichThi = lichThiRepository.findByNgayThiAndPhongAndCaThi(
                    lichThiRequest.getNgayThi(),
                    phong.get(),
                    lichThiRequest.getCaThi()
            );
            if (!trungLichThi.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Đã có lịch thi trùng ngày, phòng và ca thi"), HttpStatus.BAD_REQUEST);
            }
            lichThi.setCaThi(lichThiRequest.getCaThi());
            lichThi.setPhong(phong.get());
            lichThi.setNgayThi(lichThiRequest.getNgayThi());
            lichThi.setKyThi(kyThi.get());
            lichThiRepository.save(lichThi);
            lichThiRepository.save(lichThi);
        }
        return ResponseEntity.ok(new MessageResponse("Cập nhật lịch thi thành công"));
    }

    @DeleteMapping("/xoa/{maLichThi}")
    public ResponseEntity<?> xoaLichThi(@PathVariable Long maLichThi) {
        try {
            if (!lichThiRepository.existsById(maLichThi)) {
                return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
            }
            lichThiRepository.deleteById(maLichThi);
            return ResponseEntity.ok(new MessageResponse("Xóa lịch thi thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/phong-thi-trong/{maLichThi}")
    public ResponseEntity<?> timPhongTrong(@PathVariable Long maLichThi) {
        Optional<LichThi> lichThi = lichThiRepository.findById(maLichThi);
        if ( lichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }
        List<Phong> phongTrong = phongRepository.findUnusedExamRoomsByDateAndCaThi(lichThi.get().getNgayThi(), lichThi.get().getCaThi());
        return new ResponseEntity<>(phongTrong, HttpStatus.OK);
    }
    @PutMapping("/cap-nhat-phong/{maLichThi}")
    public ResponseEntity<?> capNhatPhong(@PathVariable Long maLichThi, @RequestBody LichThiRequest lichThiRequest) {
        Optional<LichThi> existingLichThi = lichThiRepository.findById(maLichThi);
        if (existingLichThi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Optional<Phong> newPhong = phongRepository.findById(lichThiRequest.getMaPhong());
        if (newPhong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Phòng không tồn tại"), HttpStatus.NOT_FOUND);
        }

        int count = lichThiRepository.countByPhongAndNgayThiAndCaThi(
                newPhong.get().getMaPhong(),
                existingLichThi.get().getNgayThi(),
                existingLichThi.get().getCaThi()
        );

        if (count > 0) {
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }

        LichThi lichThiToUpdate = existingLichThi.get();
        lichThiToUpdate.setPhong(newPhong.get());
        lichThiRepository.save(lichThiToUpdate);

        return ResponseEntity.ok(new MessageResponse("Cập nhật phòng thi thành công"));
    }
    @GetMapping("/{maLichThi}/hoc-vien-diem-danh")
    public ResponseEntity<List<HocVienResponse>> getHocViensByMaLichThi(@PathVariable Long maLichThi) {
        List<HocVien> hocViens = dangKyThiRepository.findHocVienByMaLichThi(maLichThi);

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
    @PutMapping("/sua-file/{maLichThi}")
    public ResponseEntity<?> suaFileDiemDanh(@PathVariable Long maLichThi, @RequestParam("file") MultipartFile file) {
        Optional<LichThi> lichThiOptional = lichThiRepository.findById(maLichThi);
        if (lichThiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }

        String newFileName = null;
        try {
            // Xóa tệp cũ (nếu có)
            String oldFile = lichThiOptional.get().getFileDiemDanh();
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
            LichThi lichThi = lichThiOptional.get();
            lichThi.setFileDiemDanh(newFileName);

            lichThiRepository.save(lichThi);
            return ResponseEntity.ok(lichThi);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not update file for HoatDongNgoaiTruong. Error: " + e.getMessage()));
        }
    }
    @GetMapping("/{maLichThi}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long maLichThi) {
        Optional<LichThi> lichThiOptional = lichThiRepository.findById(maLichThi);
        if (lichThiOptional.isPresent()) {
            try {
                String fileName = lichThiOptional.get().getFileDiemDanh();
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

    @GetMapping("/{maLichThi}/ten-file")
    public ResponseEntity<?> getFileName(@PathVariable Long maLichThi) {
        Optional<LichThi> lichThiOptional = lichThiRepository.findById(maLichThi);
        if (lichThiOptional.isPresent()) {
            try {
                String fileName = lichThiOptional.get().getFileDiemDanh();
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

    //danh sách học viên cần nhập điểm
    @GetMapping("/{maLichThi}/ds-hoc-vien-nhap-diem")
    public ResponseEntity<?> getDanhSachHocVienLichThi(@PathVariable Long maLichThi) {
        try {
            Optional<LichThi> lichThiOptional = lichThiRepository.findById(maLichThi);
            if (lichThiOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Lịch thi không tồn tại"), HttpStatus.NOT_FOUND);
            }
            // Tìm danh sách học viên của một lịch thi
            List<HocVien> hocViens = dangKyThiRepository.findHocVienByMaLichThi(maLichThi);

            // Tạo danh sách chứa thông tin điểm của học viên
            List<NhapDiemResponse> responses = new ArrayList<>();
            //kiểm tra sai danh sách

            // Lặp qua danh sách học viên và lấy thông tin điểm từ DangKyThi
            for (HocVien hocVien : hocViens) {
                // Tìm thông tin đăng ký thi của học viên cho lịch thi cụ thể
                DangKyThi dangKyThi = dangKyThiRepository.findByHocVienAndKyThi(hocVien,lichThiOptional.get().getKyThi());

                if (dangKyThi != null) {
                    // Tạo một đối tượng NhapDiemResponse
                    NhapDiemResponse response = new NhapDiemResponse();
                    response.setTenDangNhap(hocVien.getTaiKhoan().getTenDangNhap());
                    response.setMaDangKy(dangKyThi.getMaDangKyThi().toString());
                    response.setHoTen((hocVien.getTaiKhoan().getHoTen()));
                    responses.add(response);
                }
            }

            // Trả danh sách responses trong phản hồi HTTP
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            // Xử lý lỗi và trả về thông báo lỗi nếu có
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
