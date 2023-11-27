package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.BacChungChi;
import com.quanly.trungtamngoaingu.entity.ChungChi;
import com.quanly.trungtamngoaingu.entity.LichThi;
import com.quanly.trungtamngoaingu.payload.request.BacChungChiRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.BacChungChiRepository;
import com.quanly.trungtamngoaingu.repository.ChungChiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bac-chung-chi")
public class BacChungChiController {

    @Autowired
    private BacChungChiRepository bacChungChiRepository;
    @Autowired
    private ChungChiRepository chungChiRepository;
    @PostMapping("/them")
    public ResponseEntity<?> themBacChungChi(@RequestBody BacChungChiRequest bacChungChiRequest) {
        Optional<ChungChi> chungChi = chungChiRepository.findById(bacChungChiRequest.getMaChungChi());
        if (chungChi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
        }
        BacChungChi bacChungChi = new BacChungChi();
        bacChungChi.setChungChi(chungChi.get());
        bacChungChi.setBac(bacChungChiRequest.getBac());
        bacChungChi.setDiemToiThieu(bacChungChiRequest.getDiemToiThieu());
        bacChungChi.setDiemToiDa(bacChungChiRequest.getDiemToiDa());

        bacChungChiRepository.save(bacChungChi);
        return ResponseEntity.ok(new MessageResponse("Thêm bậc chứng chỉ thành công"));
    }


    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<BacChungChi>> layTatCaBacChungChi() {
        return ResponseEntity.ok(bacChungChiRepository.findAll());
    }

    @GetMapping("/lay/{maBacChungChi}")
    public ResponseEntity<?> layBacChungChi(@PathVariable Long maBacChungChi) {
        Optional<BacChungChi> bacChungChi = bacChungChiRepository.findById(maBacChungChi);
        if (bacChungChi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Bậc chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bacChungChi.get());
    }

    @PutMapping("/sua/{maBacChungChi}")
    public ResponseEntity<?> suaBacChungChi(@PathVariable Long maBacChungChi, @RequestBody BacChungChiRequest bacChungChiRequest) {
        Optional<BacChungChi> bacChungChiOptional = bacChungChiRepository.findById(maBacChungChi);
        if (bacChungChiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Bậc chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Optional<ChungChi> chungChi = chungChiRepository.findById(bacChungChiRequest.getMaChungChi());
        if (chungChi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
        }

        BacChungChi bacChungChi = bacChungChiOptional.get();
        bacChungChi.setChungChi(chungChi.get());
        bacChungChi.setBac(bacChungChiRequest.getBac());
        bacChungChi.setDiemToiThieu(bacChungChiRequest.getDiemToiThieu());
        bacChungChi.setDiemToiDa(bacChungChiRequest.getDiemToiDa());

        bacChungChiRepository.save(bacChungChi);
        return ResponseEntity.ok(new MessageResponse("Cập nhật bậc chứng chỉ thành công"));
    }

    @GetMapping("/lay-theo-chung-chi/{maChungChi}")
    public ResponseEntity<?> layBacChungChiTheoChungChi(@PathVariable Long maChungChi) {
        List<BacChungChi> bacChungChiTheoChungChi = bacChungChiRepository.findByChungChi_MaChungChi(maChungChi);
        if (bacChungChiTheoChungChi.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không có bậc chứng chỉ nào cho chứng chỉ này"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bacChungChiTheoChungChi);
    }

    @DeleteMapping("/xoa/{maBacChungChi}")
    public ResponseEntity<?> xoaBacChungChi(@PathVariable Long maBacChungChi) {
        try {
            if (!bacChungChiRepository.existsById(maBacChungChi)) {
                return new ResponseEntity<>(new MessageResponse("Bậc chứng chỉ không tồn tại"), HttpStatus.NOT_FOUND);
            }
            bacChungChiRepository.deleteById(maBacChungChi);
            return ResponseEntity.ok(new MessageResponse("Xóa bậc chứng chỉ thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }
}
