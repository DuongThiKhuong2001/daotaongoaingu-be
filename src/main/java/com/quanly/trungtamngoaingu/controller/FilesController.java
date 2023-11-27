package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import com.quanly.trungtamngoaingu.entity.TaiLieu;
import com.quanly.trungtamngoaingu.payload.request.FileInfo;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.FilesStorageService;
import com.quanly.trungtamngoaingu.repository.TaiKhoanRepository;
import com.quanly.trungtamngoaingu.repository.TaiLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(value = "*")
public class FilesController {

    @Autowired
    FilesStorageService storageService;
    @Autowired
    private TaiLieuRepository taiLieuRepository;
    //tải ảnh lên server
    @PostMapping(value = "/upload/{maTaiLieu}")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @PathVariable Long maTaiLieu) {
        Optional<TaiLieu> taiLieu = taiLieuRepository.findById(maTaiLieu);
        if(taiLieu.isEmpty()){
            return new ResponseEntity<>(new MessageResponse("Tài liệu không tồn tại"), HttpStatus.NOT_FOUND);
        }
        String oldFile = taiLieu.get().getFileTaiLieu();
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String randomFileName = UUID.randomUUID().toString() + fileExtension;
        taiLieu.get().setFileTaiLieu(randomFileName);
        String message = "";
        if (oldFile != null) {
            storageService.delete(oldFile);
        }
        try {
            storageService.saveRandom(file, randomFileName);
            taiLieuRepository.save(taiLieu.get());
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return new ResponseEntity<>(taiLieu.get().getFileTaiLieu(), HttpStatus.OK);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }


    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/files/{filename:.+}")
    public ResponseEntity<MessageResponse> deleteFile(@PathVariable String filename) {
        String message = "";

        try {
            boolean existed = storageService.delete(filename);

            if (existed) {
                message = "Delete the file successfully: " + filename;
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            }

            message = "The file does not exist!";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(message));
        }
    }
}
