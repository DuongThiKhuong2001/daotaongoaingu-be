package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.ChungChi;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.ChungChiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chung-chi")
public class ChungChiController {

    @Autowired
    private ChungChiRepository chungChiRepository;

    @PostMapping("/them")
    public ResponseEntity<?> themChungChi(@RequestBody ChungChi chungChi) {
        chungChiRepository.save(chungChi);
        return ResponseEntity.ok(new MessageResponse("Thêm chứng chỉ thành công"));
    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<ChungChi>> layTatCaChungChi() {
        return ResponseEntity.ok(chungChiRepository.findAll());
    }

    @GetMapping("/lay/{maChungChi}")
    public ResponseEntity<?> layChungChi(@PathVariable Long maChungChi) {
        Optional<ChungChi> chungChi = chungChiRepository.findById(maChungChi);
        if (chungChi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(chungChi.get());
    }

    @PutMapping("/sua/{maChungChi}")
    public ResponseEntity<?> suaChungChi(@PathVariable Long maChungChi, @RequestBody ChungChi chungChiMoi) {
        if (!chungChiRepository.existsById(maChungChi)) {
            return new ResponseEntity<>(new MessageResponse("Chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Optional<ChungChi> chungChi = chungChiRepository.findById(maChungChi);
        if(chungChi.isPresent()){
            chungChi.get().setTenChungChi(chungChiMoi.getTenChungChi());
            chungChi.get().setMoTa(chungChiMoi.getMoTa());
            chungChi.get().setLePhiThi(chungChiMoi.getLePhiThi());
            chungChiRepository.save(chungChi.get());
        }
        return ResponseEntity.ok(new MessageResponse("Cập nhật chứng chỉ thành công"));
    }

    @DeleteMapping("/xoa/{maChungChi}")
    public ResponseEntity<?> xoaChungChi(@PathVariable Long maChungChi) {
        try {
            if (!chungChiRepository.existsById(maChungChi)) {
                return new ResponseEntity<>(new MessageResponse("Chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
            }
            chungChiRepository.deleteById(maChungChi);
            return ResponseEntity.ok(new MessageResponse("Xóa chứng chỉ thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }

}
