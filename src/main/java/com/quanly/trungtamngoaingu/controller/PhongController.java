package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.LichThi;
import com.quanly.trungtamngoaingu.entity.LopHoc;
import com.quanly.trungtamngoaingu.entity.Phong;
import com.quanly.trungtamngoaingu.payload.request.PhongHocRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.LichHocRepository;
import com.quanly.trungtamngoaingu.repository.LopHocRepository;
import com.quanly.trungtamngoaingu.repository.PhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/phong")
public class PhongController {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LopHocRepository lopHocRepository;
    @GetMapping("/phong-hop-le/{maLop}")
    public ResponseEntity<?> getUnusedRooms(@PathVariable Long maLop) {
        Optional<LopHoc> optLopHoc = lopHocRepository.findById(maLop);
        if (optLopHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lớp học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        LopHoc lopHoc = optLopHoc.get();
        Date ngayBatDau = lopHoc.getKhoaHoc().getNgayBatDau();
        Date ngayKetThuc = lopHoc.getKhoaHoc().getNgayKetThuc();

        List<Phong> unusedRooms = phongRepository.findAvailablePhongHocByLichHocAndKhoaHoc(ngayBatDau, ngayKetThuc, lopHoc.getLichHoc().getKiHieu());
        return ResponseEntity.ok(unusedRooms);
    }

    @PostMapping("/them")
    public ResponseEntity<?> themPhongHoc(@RequestBody PhongHocRequest phongHocRequest) {
        try {
            Phong existingPhong = phongRepository.findByKiHieu(phongHocRequest.getKiHieu());
            if (existingPhong != null) {
                return new ResponseEntity<>(new MessageResponse("Kí hiệu của phòng học đã tồn tại"), HttpStatus.BAD_REQUEST);
            }
            Phong phong = new Phong();
            phong.setTenPhong(phongHocRequest.getTenPhong());
            phong.setSucChua(phongHocRequest.getSucChua());
            phong.setViTri(phongHocRequest.getViTri());
            phong.setKiHieu(phongHocRequest.getKiHieu());
            phong.setLoaiPhong(phongHocRequest.getLoaiPhong());
            phongRepository.save(phong);
            return new ResponseEntity<>(new MessageResponse("Phòng học được thêm thành công"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi thêm phòng học"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/danh-sach/{loai}")
    public ResponseEntity<?> layTatCaPhongHoc(@PathVariable Phong.LoaiPhong loai) {
        try {
            List<Phong> phongs = phongRepository.findAllByLoaiPhong(loai);
            return new ResponseEntity<>(phongs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi lấy phòng"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{maPhongHoc}")
    public ResponseEntity<Phong> layPhongHocTheoId(@PathVariable Long maPhongHoc) {
        Optional<Phong> phongHocData = phongRepository.findById(maPhongHoc);

        if (phongHocData.isPresent()) {
            return new ResponseEntity<>(phongHocData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/cap-nhat/{maPhongHoc}")
    public ResponseEntity<?> capNhatPhongHoc(@PathVariable Long maPhongHoc, @RequestBody PhongHocRequest phongHocRequest) {
        Optional<Phong> phongHocData = phongRepository.findById(maPhongHoc);

        if (phongHocData.isPresent()) {
            Phong phong = phongHocData.get();
            phong.setTenPhong(phongHocRequest.getTenPhong());
            phong.setSucChua(phongHocRequest.getSucChua());
            phong.setViTri(phongHocRequest.getViTri());
            phong.setKiHieu(phong.getKiHieu());
            phong.setLoaiPhong(phongHocRequest.getLoaiPhong());
            phongRepository.save(phong);
            return new ResponseEntity<>(new MessageResponse("Phòng học được cập nhật thành công"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy phòng học"), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/xoa/{maPhongHoc}")
    public ResponseEntity<?> xoaPhongHoc(@PathVariable Long maPhongHoc) {
        try {
            phongRepository.deleteById(maPhongHoc);
            return new ResponseEntity<>(new MessageResponse("Phòng học đã được xóa"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }

}