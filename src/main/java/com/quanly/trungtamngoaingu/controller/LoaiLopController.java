package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.LoaiLop;
import com.quanly.trungtamngoaingu.entity.Phong;
import com.quanly.trungtamngoaingu.entity.TaiLieu;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.FilesStorageService;
import com.quanly.trungtamngoaingu.repository.KhoaHocRepository;
import com.quanly.trungtamngoaingu.repository.LoaiLopRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/loai-lop")
public class LoaiLopController {

    @Autowired
    private LoaiLopRepository loaiLopRepository;
    @Autowired
    private KhoaHocRepository khoaHocRepository;
    @Autowired
    private FilesStorageService storageService;

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<LoaiLop>> layTatCaLoaiLop() {
        List<LoaiLop> loaiLops = loaiLopRepository.findAll();

        // Update each TaiLieu object's fileTaiLieu attribute with its original file name
        for (LoaiLop loaiLop : loaiLops) {
            String currentFileName = loaiLop.getDeCuong();
            String originalFileName = extractOriginalFileName(currentFileName);
            loaiLop.setDeCuong(originalFileName);
        }

        return ResponseEntity.ok(loaiLops);
    }

    @GetMapping("/lay/{maLoaiLop}")
    public ResponseEntity<?> layLoaiLop(@PathVariable Long maLoaiLop) {
        Optional<LoaiLop> loaiLop = loaiLopRepository.findById(maLoaiLop);
        if (loaiLop.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(loaiLop.get());
    }

    @PostMapping("/them")
    public ResponseEntity<?> themLoaiLop(@RequestParam("file") MultipartFile file, @RequestParam("tenLoaiLop") String tenLoaiLop, @RequestParam("hocPhi") Long hocPhi, @RequestParam("tomTatDeCuong") String tomTatDeCuong) {
        if (loaiLopRepository.existsByTenLoaiLop(tenLoaiLop)) {
            return new ResponseEntity<>(new MessageResponse("Tên loại lớp đã tồn tại"), HttpStatus.BAD_REQUEST);
        }

        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        baseName = Helpers.createSlug(baseName);
        // Tạo một chuỗi timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        String newFileName = timestamp + "_" + baseName + fileExtension;

        storageService.saveRandom(file, newFileName);

        LoaiLop newLoaiLop = new LoaiLop();
        newLoaiLop.setTenLoaiLop(tenLoaiLop);
        newLoaiLop.setHocPhi(hocPhi);
        newLoaiLop.setDeCuong(newFileName); // Set tên file đã lưu cho trường deCuong
        newLoaiLop.setTomTatDeCuong(tomTatDeCuong);
        loaiLopRepository.save(newLoaiLop);

        return ResponseEntity.ok(new MessageResponse("Thêm loại lớp thành công"));
    }

    @PutMapping("/sua/{maLoaiLop}")
    public ResponseEntity<?> suaLoaiLop(@PathVariable Long maLoaiLop, @RequestParam("file") Optional<MultipartFile> fileOptional, @RequestParam("tenLoaiLop") String tenLoaiLop, @RequestParam("hocPhi") Long hocPhi,
                                        @RequestParam("tomTatDeCuong") String tomTatDeCuong) {
        Optional<LoaiLop> existingLoaiLop = loaiLopRepository.findById(maLoaiLop);

        if (existingLoaiLop.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
        }

        LoaiLop loaiLop = existingLoaiLop.get();
        loaiLop.setTenLoaiLop(tenLoaiLop);
        loaiLop.setHocPhi(hocPhi);
        loaiLop.setTomTatDeCuong(tomTatDeCuong);
        if (fileOptional.isPresent()) {
            MultipartFile file = fileOptional.get();

            // Xóa file cũ
            storageService.delete(loaiLop.getDeCuong());

            // Lưu file mới
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            baseName = Helpers.createSlug(baseName);
            // Tạo một chuỗi timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            String newFileName = timestamp + "_" + baseName + fileExtension;

            storageService.saveRandom(file, newFileName);
            loaiLop.setDeCuong(newFileName);
        }

        loaiLopRepository.save(loaiLop);

        return ResponseEntity.ok(new MessageResponse("Cập nhật loại lớp thành công"));
    }


    @DeleteMapping("/xoa/{maLoaiLop}")
    public ResponseEntity<?> xoaLoaiLop(@PathVariable Long maLoaiLop) {
        try{
        if (!loaiLopRepository.existsById(maLoaiLop)) {
            return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
        }
        if(khoaHocRepository.existsByLoaiLop_MaLoaiLop(maLoaiLop)){
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);

        }  loaiLopRepository.deleteById(maLoaiLop);
            return ResponseEntity.ok(new MessageResponse("Xóa loại lớp thành công"));
        }
        catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);

        }
        //thêm kiểm tra khóa ngoại

    }
    @GetMapping("/download-de-cuong/{maLoaiLop}")
    public ResponseEntity<?> downloadDeCuong(@PathVariable Long maLoaiLop) {
        Optional<LoaiLop> loaiLopOptional = loaiLopRepository.findById(maLoaiLop);
        if (loaiLopOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
        }

        LoaiLop loaiLop = loaiLopOptional.get();
        String fileName = loaiLop.getDeCuong();

        try {
            Resource file = storageService.load(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Lỗi khi tải xuống đề cương: " + e.getMessage()));
        }
    }
    @GetMapping("/de-cuong-ten/{maLoaiLop}")
    public ResponseEntity<?> getDeCuongTen(@PathVariable Long maLoaiLop) {
        Optional<LoaiLop> loaiLopOptional = loaiLopRepository.findById(maLoaiLop);
        if (loaiLopOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
        }

        LoaiLop loaiLop = loaiLopOptional.get();
        String currentFileName = loaiLop.getDeCuong();
        String originalFileName = extractOriginalFileName(currentFileName); // Sử dụng hàm đã có từ TaiLieuController

        return ResponseEntity.ok(originalFileName);
    }
    private String extractOriginalFileName(String currentFileName) {
        int underscoreIndex = currentFileName.indexOf("_");
        if (underscoreIndex > 0 && underscoreIndex < currentFileName.length() - 1) {
            return currentFileName.substring(underscoreIndex + 1);
        }
        return currentFileName;
    }

}
