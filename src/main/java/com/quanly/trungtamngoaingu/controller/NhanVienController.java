package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.NhanVien;
import com.quanly.trungtamngoaingu.entity.VaiTro;
import com.quanly.trungtamngoaingu.payload.request.VaiTroNhanVienRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.NhanVienRepository;
import com.quanly.trungtamngoaingu.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tai-khoan/nhan-vien")
public class NhanVienController {
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private VaiTroRepository vaiTroRepository;
    @PostMapping("/them-vai-tro/{nhanVienId}")
    public ResponseEntity<?> themVaiTroChoNhanVien(@PathVariable Long nhanVienId, @RequestBody VaiTroNhanVienRequest vaiTroIdsDTO) {
        // Xác thực và kiểm tra quyền ở đây

        Optional<NhanVien> nhanVienOptional = nhanVienRepository.findById(nhanVienId);

        if (nhanVienOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("nhanvien-notfound"), HttpStatus.NOT_FOUND);
        }

        NhanVien nhanVien = nhanVienOptional.get();

        List<Long> vaiTroIds = vaiTroIdsDTO.getVaiTroIds();

        if (vaiTroIds == null || vaiTroIds.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("not-vaitro"), HttpStatus.BAD_REQUEST);
        }

        try {
            // Lấy danh sách vai trò dựa trên vaiTroIds
            List<VaiTro> vaiTroList = vaiTroRepository.findAllById(vaiTroIds);

            if (vaiTroList.isEmpty() || vaiTroList.size() != vaiTroIds.size()) {
                return new ResponseEntity<>(new MessageResponse("not-vaitro"), HttpStatus.BAD_REQUEST);
            }

            // Gán danh sách vai trò cho nhân viên
            nhanVien.setVaiTros(new HashSet<>(vaiTroList));

            // Lưu thay đổi vào cơ sở dữ liệu
            nhanVienRepository.save(nhanVien);

        } catch (Exception e) {
            // Xử lý lỗi và rollback giao dịch nếu cần
            return new ResponseEntity<>(new MessageResponse("Lỗi trong quá trình thêm vai trò"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(new MessageResponse("Đã thêm vai trò cho nhân viên thành công"));
    }
    @GetMapping("/vai-tro/{tenDangNhap}")
    public ResponseEntity<?> layVaiTroCuaNhanVien(@PathVariable String tenDangNhap) {
        NhanVien nhanVienOptional = nhanVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

        if (nhanVienOptional==null) {
            return new ResponseEntity<>(new MessageResponse("nhanvien-notfound"), HttpStatus.NOT_FOUND);
        }
        Set<VaiTro> vaiTros = nhanVienOptional.getVaiTros();

        return ResponseEntity.ok(vaiTros);
    }
    @GetMapping("/danh-sach-nhan-vien-cua-vai-tro/{tenVaiTro}")
    public ResponseEntity<?> layNhanVienCuaVaiTro(@PathVariable String tenVaiTro) {
        VaiTro vaiTroOptional = vaiTroRepository.findByTenVaiTro(tenVaiTro);

        if (vaiTroOptional== null) {
            return new ResponseEntity<>(new MessageResponse("vaitro-notfound"), HttpStatus.NOT_FOUND);
        }

        Set<NhanVien> nhanViens = vaiTroOptional.getNhanViens();
        return ResponseEntity.ok(nhanViens);
    }

    @GetMapping("/danh-sach-vai-tro")
    public ResponseEntity<?> layAllVaiTro() {
        List<VaiTro> vaiTroOptional = vaiTroRepository.findAll();
        return ResponseEntity.ok(vaiTroOptional);
    }
    @PostMapping("/them-vai-tro")
    public ResponseEntity<?> themVaiTro(@RequestBody VaiTro vaiTro) {
        try {
            VaiTro newVaiTro = vaiTroRepository.save(vaiTro);
            return ResponseEntity.ok(newVaiTro);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi thêm vai trò mới"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/cap-nhat-vai-tro/{maVaiTro}")
    public ResponseEntity<?> capNhatVaiTro(@PathVariable Long maVaiTro, @RequestBody VaiTro vaiTroUpdate) {
        Optional<VaiTro> vaiTroOptional = vaiTroRepository.findById(maVaiTro);

        if (vaiTroOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vai tro not found"), HttpStatus.NOT_FOUND);
        }

        VaiTro vaiTro = vaiTroOptional.get();
        vaiTro.setTenVaiTro(vaiTroUpdate.getTenVaiTro());
        vaiTro.setMoTa(vaiTroUpdate.getMoTa());

        try {
            vaiTroRepository.save(vaiTro);
            return ResponseEntity.ok(new MessageResponse("Cập nhật vai trò thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật vai trò"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/xoa-vai-tro/{maVaiTro}")
    public ResponseEntity<?> xoaVaiTro(@PathVariable Long maVaiTro) {
        Optional<VaiTro> vaiTroOptional = vaiTroRepository.findById(maVaiTro);

        if (vaiTroOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vai trò không tồn tại"), HttpStatus.NOT_FOUND);
        }

        VaiTro vaiTro = vaiTroOptional.get();

        if (!vaiTro.getNhanViens().isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }

        try {
            vaiTroRepository.deleteById(maVaiTro);
            return ResponseEntity.ok(new MessageResponse("Xóa vai trò thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi xóa vai trò"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lay-vai-tro/{maVaiTro}")
    public ResponseEntity<?> layVaiTro(@PathVariable Long maVaiTro) {
        Optional<VaiTro> vaiTro = vaiTroRepository.findById(maVaiTro);
        if (vaiTro.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Vai trò không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(vaiTro.get());
    }
}
