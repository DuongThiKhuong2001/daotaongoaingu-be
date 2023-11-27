package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.KhoaHoc;
import com.quanly.trungtamngoaingu.entity.LichHoc;
import com.quanly.trungtamngoaingu.entity.LoaiLop;
import com.quanly.trungtamngoaingu.payload.request.KhoaHocRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.DangKyKhoaHocRepository;
import com.quanly.trungtamngoaingu.repository.KhoaHocRepository;
import com.quanly.trungtamngoaingu.repository.LichHocRepository;
import com.quanly.trungtamngoaingu.repository.LoaiLopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/khoa-hoc")
public class KhoaHocController {

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private DangKyKhoaHocRepository dangKyKhoaHocRepository;
    @Autowired
    private  LoaiLopRepository loaiLopRepository;
    @Autowired
    private LichHocRepository lichHocRepository;

    //lấy danh sách khóa hoc phan trang sap xep ở back end
    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllKhoaHoc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayBatDau") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String trangThai
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);
        Specification<KhoaHoc> spec = null;

        if (!searchTerm.isEmpty()) {
            spec = (root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.like(root.get("tenKhoaHoc"), pattern);
            };
        }

        if (!trangThai.isEmpty()) {
            Specification<KhoaHoc> trangThaiSpec = (root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("trangThai"), trangThai);
            };

            if (spec != null) {
                spec = spec.and(trangThaiSpec);
            } else {
                spec = trangThaiSpec;
            }
        }

        if (spec == null) {
            spec = (root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true
            };
        }

        Page<KhoaHoc> khoaHocPage = khoaHocRepository.findAll(spec, paging);
        return ResponseEntity.ok(khoaHocPage);
    }


    @GetMapping("/lay/{maKhoaHoc}")
    public ResponseEntity<?> layKhoaHoc(@PathVariable Long maKhoaHoc) {
        Optional<KhoaHoc> khoaHoc = khoaHocRepository.findById(maKhoaHoc);
        if (khoaHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(khoaHoc.get());
    }

    @PutMapping("/sua/{maKhoaHoc}")
    public ResponseEntity<?> suaKhoaHoc(@PathVariable Long maKhoaHoc, @RequestBody KhoaHocRequest khoaHocRequest) {
        Optional<KhoaHoc> khoaHocOptional = khoaHocRepository.findById(maKhoaHoc);

        if (!khoaHocOptional.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        KhoaHoc khoaHoc = khoaHocOptional.get();

        Optional<LoaiLop> loaiLopOptional = loaiLopRepository.findById(khoaHocRequest.getMaLoaiLop());

        if (loaiLopOptional.isPresent()) {
            // Cập nhật thông tin của khóa học
            khoaHoc.setLoaiLop(loaiLopOptional.get());
            khoaHoc.setTenKhoaHoc(khoaHocRequest.getTenKhoaHoc());
            khoaHoc.setNgayKetThuc(khoaHocRequest.getNgayKetThuc());
            khoaHoc.setNgayBatDau(khoaHocRequest.getNgayBatDau());

            // Lưu cập nhật vào cơ sở dữ liệu
            khoaHocRepository.save(khoaHoc);

            return ResponseEntity.ok(new MessageResponse("Cập nhật khóa học thành công"));
        } else {
            return new ResponseEntity<>(new MessageResponse("Cập nhật khóa học không thành công"), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/them")
    public ResponseEntity<?> them(@RequestBody KhoaHocRequest khoaHocRequest) {
        boolean existingKH = khoaHocRepository.existsByTenKhoaHocAndNgayBatDauAndNgayKetThuc(khoaHocRequest.getTenKhoaHoc(), khoaHocRequest.getNgayBatDau(),khoaHocRequest.getNgayKetThuc());
        if (existingKH) {
            return new ResponseEntity<>(new MessageResponse("Khóa học đã tồn tại"), HttpStatus.BAD_REQUEST);
        }
        KhoaHoc khoaHoc = new KhoaHoc();

        Optional<LoaiLop> loaiLopOptional = loaiLopRepository.findById(khoaHocRequest.getMaLoaiLop());

        if (loaiLopOptional.isPresent()) {
            // Cập nhật thông tin của khóa học
            khoaHoc.setLoaiLop(loaiLopOptional.get());
            khoaHoc.setTenKhoaHoc(khoaHocRequest.getTenKhoaHoc());
            khoaHoc.setNgayKetThuc(khoaHocRequest.getNgayKetThuc());
            khoaHoc.setNgayBatDau(khoaHocRequest.getNgayBatDau());

            // Lưu cập nhật vào cơ sở dữ liệu
            khoaHocRepository.save(khoaHoc);

            return ResponseEntity.ok(new MessageResponse("Thêm khóa học thành công"));
        } else {
            return new ResponseEntity<>(new MessageResponse("Thêm khóa học không thành công"), HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/xoa/{maKhoaHoc}")
    public ResponseEntity<?> xoaKhoaHoc(@PathVariable Long maKhoaHoc) {
        if (!khoaHocRepository.existsById(maKhoaHoc)) {
            return new ResponseEntity<>(new MessageResponse("Khóa học không tồn tại"), HttpStatus.NOT_FOUND);
        }

        // Kiểm tra ràng buộc hoặc liên kết trước khi xóa
        if (dangKyKhoaHocRepository.existsByKhoaHoc_MaKhoaHoc(maKhoaHoc)) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
        
        khoaHocRepository.deleteById(maKhoaHoc);
        return ResponseEntity.ok(new MessageResponse("Xóa khóa học thành công"));
    }


}

