package com.quanly.trungtamngoaingu.controller;


import com.quanly.trungtamngoaingu.entity.LoaiLop;
import com.quanly.trungtamngoaingu.entity.TaiLieu;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.FilesStorageService;
import com.quanly.trungtamngoaingu.repository.LoaiLopRepository;
import com.quanly.trungtamngoaingu.repository.TaiLieuRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/tai-lieu")
@CrossOrigin(value = "*")
public class TaiLieuController {
    @Autowired
    private FilesStorageService storageService;
    @Autowired
    private TaiLieuRepository taiLieuRepository;
    @Autowired
    private LoaiLopRepository loaiLopRepository;
    @Autowired
    private Helpers helpers;

    // Lấy danh sách tất cả tài liệu
    @GetMapping
    public ResponseEntity<List<TaiLieu>> getAllTaiLieu() {
        return ResponseEntity.ok(taiLieuRepository.findAll());
    }

    @GetMapping("/lay-theo-loai-lop/{maLoaiLop}")
    public ResponseEntity<?> getAllTaiLieuByLoaiLop(@PathVariable Long maLoaiLop) {
        try {
            List<TaiLieu> taiLieus = taiLieuRepository.findAllByLoaiLop_MaLoaiLop(maLoaiLop);

            // Update each TaiLieu object's fileTaiLieu attribute with its original file name
            for (TaiLieu taiLieu : taiLieus) {
                String currentFileName = taiLieu.getFileTaiLieu();
                String originalFileName = extractOriginalFileName(currentFileName);
                taiLieu.setFileTaiLieu(originalFileName);
            }

            return ResponseEntity.ok(taiLieus);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi lấy tài liệu"), HttpStatus.BAD_REQUEST);
        }
    }


    // Lấy thông tin một tài liệu theo ID
    @GetMapping("/{maTaiLieu}")
    public ResponseEntity<?> getTaiLieuById(@PathVariable Long maTaiLieu) {
        Optional<TaiLieu> taiLieu = taiLieuRepository.findById(maTaiLieu);
        if (taiLieu.isPresent()) {
            return ResponseEntity.ok(taiLieu.get());
        } else {
            return new ResponseEntity<>(new MessageResponse("Tài liệu không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }

    // Tạo mới một tài liệu
    @PostMapping
    public ResponseEntity<?> createTaiLieu(@RequestParam("file") MultipartFile file, @RequestParam("maLoaiLop") Long maLoaiLop) {
        try {
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));

            // Convert baseName to non-accented version
            baseName = Helpers.createSlug(baseName);

            // Create a timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            String newFileName = timestamp + "_" + baseName + fileExtension;

            storageService.saveRandom(file, newFileName);
            Optional<LoaiLop> loaiLop = loaiLopRepository.findById(maLoaiLop);
            if (loaiLop.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
            }
            TaiLieu newTaiLieu = new TaiLieu();
            newTaiLieu.setFileTaiLieu(newFileName);
            newTaiLieu.setLoaiLop(loaiLop.get());

            return ResponseEntity.ok(taiLieuRepository.save(newTaiLieu));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not create TaiLieu. Error: " + e.getMessage()));
        }
    }


    // Cập nhật thông tin một tài liệu và tải lên file mới (nếu có)
    @PutMapping("/{maTaiLieu}")
    public ResponseEntity<?> updateTaiLieu(@PathVariable Long maTaiLieu, @RequestParam("file") MultipartFile file, @RequestParam("maLoaiLop") Long maLoaiLop) {
        Optional<TaiLieu> taiLieu = taiLieuRepository.findById(maTaiLieu);
        if (taiLieu.isPresent()) {
            try {
                TaiLieu currentTaiLieu = taiLieu.get();

                if (file != null) {
                    String oldFile = currentTaiLieu.getFileTaiLieu();
                    if (oldFile != null) {
                        storageService.delete(oldFile);
                    }

                    String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
                    String fileExtension = getFileExtension(originalFileName);
                    String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));

                    // Tạo một chuỗi timestamp
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    String timestamp = LocalDateTime.now().format(formatter);

                    String newFileName = timestamp + "_" + baseName + fileExtension;

                    storageService.saveRandom(file, newFileName);
                    currentTaiLieu.setFileTaiLieu(newFileName);
                }

                // Set loaiLop dựa trên maLoaiLop
                Optional<LoaiLop> loaiLop = loaiLopRepository.findById(maLoaiLop);
                if (loaiLop.isPresent()) {
                    currentTaiLieu.setLoaiLop(loaiLop.get());
                } else {
                    return new ResponseEntity<>(new MessageResponse("Loại lớp không tồn tại"), HttpStatus.NOT_FOUND);
                }

                return ResponseEntity.ok(taiLieuRepository.save(currentTaiLieu));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not update TaiLieu. Error: " + e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Tài liệu không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }


    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    // Xóa một tài liệu
    @DeleteMapping("/{maTaiLieu}")
    public ResponseEntity<?> deleteTaiLieu(@PathVariable Long maTaiLieu) {
        Optional<TaiLieu> taiLieu = taiLieuRepository.findById(maTaiLieu);
        if (taiLieu.isPresent()) {
            try {
                // Delete the associated file
                String fileName = taiLieu.get().getFileTaiLieu();
                storageService.delete(fileName); // Assuming storageService has a delete method

                // Delete the TaiLieu object from the repository
                taiLieuRepository.deleteById(maTaiLieu);

                return new ResponseEntity<>(new MessageResponse("Xóa tài liệu và file thành công"), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(new MessageResponse("Lỗi khi xóa tài liệu hoặc file: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Tài liệu không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{maTaiLieu}/ten")
    public ResponseEntity<?> getOriginalFileName(@PathVariable Long maTaiLieu) {
        Optional<TaiLieu> taiLieu = taiLieuRepository.findById(maTaiLieu);
        if (taiLieu.isPresent()) {
            try {
                String currentFileName = taiLieu.get().getFileTaiLieu();
                String originalFileName = extractOriginalFileName(currentFileName);
                return ResponseEntity.ok(originalFileName);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not extract original filename. Error: " + e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Tài liệu không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }

    private String extractOriginalFileName(String currentFileName) {
        int underscoreIndex = currentFileName.indexOf("_");
        if (underscoreIndex > 0 && underscoreIndex < currentFileName.length() - 1) {
            return currentFileName.substring(underscoreIndex + 1);
        }
        return currentFileName;
    }

    @GetMapping("/{maTaiLieu}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long maTaiLieu) {
        Optional<TaiLieu> taiLieu = taiLieuRepository.findById(maTaiLieu);
        if (taiLieu.isPresent()) {
            try {
                String fileName = taiLieu.get().getFileTaiLieu();
                Resource file = storageService.load(fileName); // Giả định storageService có một hàm load() để lấy file dựa trên tên

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                        .body(file);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Tài liệu không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }
}

