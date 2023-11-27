package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import com.quanly.trungtamngoaingu.entity.ThongBao;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.TaiKhoanRepository;
import com.quanly.trungtamngoaingu.repository.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import com.quanly.trungtamngoaingu.sercurity.Helpers;
import java.util.List;

@RestController
@RequestMapping("/api/thong-bao")
@CrossOrigin(value = "*")
public class ThongBaoController {
    @Autowired
    private ThongBaoService thongBaoService;
//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private Helpers helpers;
    @GetMapping()
    public ResponseEntity<?> layThongBaoTheoNguoiDungId(HttpServletRequest httpServletRequest) {
        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);
        Long maTk =  currentUser.getMaTaiKhoan();
        List<ThongBao> thongBaos = thongBaoService.layThongBaoTheoTaiKhoan(maTk);
        return ResponseEntity.ok(thongBaos);
    }
    //lấy số thông báo chưa đọc
    @GetMapping("/chua-doc")
    public ResponseEntity<Long> demThongBaoChuaDoc(HttpServletRequest httpServletRequest) {
        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);
        Long maTk =  currentUser.getMaTaiKhoan();
        Long soLuongChuaDoc = thongBaoService.soThongBaoChuaDocTheoTaiKhoan(maTk);
        return ResponseEntity.ok(soLuongChuaDoc);
    }
    @PutMapping("/trang-thai/{maThongBao}")
    public ResponseEntity<?> datTrangThaiThongBao(@PathVariable Long maThongBao) {
        ThongBao thongBao = thongBaoService.layThongBaoTheoMaThongBao(maThongBao);
        thongBao.setTrangThai(ThongBao.TrangThai.DaDoc);
        thongBaoService.luuThongBao(thongBao);
       // messagingTemplate.convertAndSendToUser(thongBao.getTaiKhoan().getTenDangNhap(), "/queue/messages", "update-status");
        return ResponseEntity.ok(new MessageResponse("đã đọc"));
    }

    @DeleteMapping("/xoa/{maThongBao}")
    public ResponseEntity<?> xoaThongBao(@PathVariable Long maThongBao) {
        thongBaoService.xoaThongBaoTheoMa(maThongBao);
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<?> xoaTatCaThongBaoTheoNguoiDungId(HttpServletRequest httpServletRequest) {
        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);
        Long maTk =  currentUser.getMaTaiKhoan();
        List<ThongBao> thongBaos = thongBaoService.layThongBaoDaDocTheoTaiKhoan(maTk);
        if (!thongBaos.isEmpty()) {
            thongBaoService.xoaThongBaoDaDocTheoTaiKhoan(maTk);
            return  new ResponseEntity<>(new MessageResponse("Đã xóa các thông báo cho người dùng"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MessageResponse("Not_Found"), HttpStatus.NOT_FOUND);
        }
    }
}
