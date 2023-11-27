package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.KhoaHoc;
import com.quanly.trungtamngoaingu.entity.LichHoc;
import com.quanly.trungtamngoaingu.entity.LopHoc;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.KhoaHocRepository;
import com.quanly.trungtamngoaingu.repository.LichHocRepository;
import com.quanly.trungtamngoaingu.repository.LopHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/lich-hoc")
public class LichHocController {

    @Autowired
    private LichHocRepository lichHocRepository;
    @Autowired
    private LopHocRepository lopHocRepository;

    @PostMapping("/them")
    public ResponseEntity<?> themLichHoc(@RequestBody LichHoc lichHoc) {
        if(lichHocRepository.existsByKiHieu(lichHoc.getKiHieu())){
            return ResponseEntity.ok(new MessageResponse("exist"));
        }
        lichHocRepository.save(lichHoc);
        return ResponseEntity.ok(new MessageResponse("Thêm lịch học thành công"));
    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<LichHoc>> layTatCaLichHoc() {
        return ResponseEntity.ok(lichHocRepository.findAll());
    }

    @GetMapping("/lay/{maLichHoc}")
    public ResponseEntity<?> layLichHoc(@PathVariable Long maLichHoc) {
        Optional<LichHoc> lichHoc = lichHocRepository.findById(maLichHoc);
        if (lichHoc.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Lịch học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(lichHoc.get());
    }

    @PutMapping("/sua/{maLichHoc}")
    public ResponseEntity<?> suaLichHoc(@PathVariable Long maLichHoc, @RequestBody LichHoc lichHocMoi) {
        if (!lichHocRepository.existsById(maLichHoc)) {
            return new ResponseEntity<>(new MessageResponse("Lịch học không tồn tại"), HttpStatus.NOT_FOUND);
        }
        Optional<LichHoc> lichHoc = lichHocRepository.findById(maLichHoc);
        if (lichHoc.isPresent()) {
            lichHoc.get().setKiHieu(lichHocMoi.getKiHieu());
            lichHoc.get().setMoTa(lichHocMoi.getMoTa());
            lichHocRepository.save(lichHoc.get());
        }
        return ResponseEntity.ok(new MessageResponse("Cập nhật lịch học thành công"));
    }

    @DeleteMapping("/xoa/{maLichHoc}")
    public ResponseEntity<?> xoaLichHoc(@PathVariable Long maLichHoc) {
        try {
            if (!lichHocRepository.existsById(maLichHoc)) {
                return new ResponseEntity<>(new MessageResponse("Lịch học không tồn tại"), HttpStatus.NOT_FOUND);
            }
            if (lopHocRepository.existsByLichHoc_MaLichHoc(maLichHoc)) {
                return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);

            }

            //thêm kiểm tra khóa ngoại
            lichHocRepository.deleteById(maLichHoc);
            return ResponseEntity.ok(new MessageResponse("Xóa lịch học thành công"));
        }catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);

        }
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

    public List<Map<String, Object>> getScheduleByMaLop(Long maLop) {
        Optional<LopHoc> optionalLopHoc = lopHocRepository.findById(maLop);
        if (optionalLopHoc.isPresent()) {
            LopHoc lopHoc = optionalLopHoc.get();
            if (lopHoc.getLichHoc() == null) {
                return Collections.emptyList();
            }
            LichHoc lichHoc = lopHoc.getLichHoc();
            KhoaHoc khoaHoc = lopHoc.getKhoaHoc();
            return generateSchedule(lichHoc.getKiHieu(), khoaHoc.getNgayBatDau(), khoaHoc.getNgayKetThuc());
        }
        return Collections.emptyList();
    }


}
