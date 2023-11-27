package com.quanly.trungtamngoaingu.repository;
import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import com.quanly.trungtamngoaingu.entity.ThongBao;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    @Autowired
    public ThongBaoService(ThongBaoRepository thongBaoRepository) {
        this.thongBaoRepository = thongBaoRepository;
    }

    public ThongBao taoMoiThongBao(TaiKhoan taiKhoan, String tieuDe, String noiDung, ThongBao.TrangThai trangThai) {
        ThongBao thongBao = new ThongBao();
        thongBao.setTaiKhoan(taiKhoan);
        thongBao.setTieuDe(tieuDe);
        thongBao.setNoiDung(noiDung);
        thongBao.setTrangThai(trangThai);
        return thongBaoRepository.save(thongBao);
    }

    public List<ThongBao> layThongBaoDaDocTheoTaiKhoan(Long maTaiKhoan) {
        return thongBaoRepository.findByTaiKhoan_MaTaiKhoanAndTrangThai(maTaiKhoan, ThongBao.TrangThai.DaDoc);
    }
    public ThongBao layThongBaoTheoMaThongBao(Long maThongBao) {
        return thongBaoRepository.findById(maThongBao)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo với mã " + maThongBao));
    }
    public void luuThongBao(ThongBao thongBao) {
        thongBaoRepository.save(thongBao);
    }
    public void xoaThongBaoTheoMa(Long maThongBao) {
        thongBaoRepository.deleteById(maThongBao);
    }
    public List<ThongBao> layThongBaoTheoTaiKhoan(Long maTaiKhoan) {
        return thongBaoRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan);
    }
    public Long soThongBaoChuaDocTheoTaiKhoan(Long maTaiKhoan) {
        return thongBaoRepository.countChuaDocByMaTaiKhoan(maTaiKhoan);
    }
    public void xoaThongBaoDaDocTheoTaiKhoan(Long maTaiKhoan) {
        thongBaoRepository.deleteAllByTaiKhoan_MaTaiKhoanAndTrangThai(maTaiKhoan, ThongBao.TrangThai.DaDoc);
    }
}
