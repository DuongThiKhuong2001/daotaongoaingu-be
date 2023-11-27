package com.quanly.trungtamngoaingu.payload.response;

import com.quanly.trungtamngoaingu.entity.KyThi;
import com.quanly.trungtamngoaingu.entity.LichThi;
import com.quanly.trungtamngoaingu.entity.Phong;
import jakarta.persistence.*;

import java.util.Date;

public class LichThiResponse {

        private Long maLichThi;


        private KyThi kyThi;

        private Date ngayThi;


        private Phong phong;
        private LichThi.CaThi caThi;
        private int soLuongDangKy;

    public Long getMaLichThi() {
        return maLichThi;
    }

    public void setMaLichThi(Long maLichThi) {
        this.maLichThi = maLichThi;
    }

    public KyThi getKyThi() {
        return kyThi;
    }

    public void setKyThi(KyThi kyThi) {
        this.kyThi = kyThi;
    }

    public Date getNgayThi() {
        return ngayThi;
    }

    public void setNgayThi(Date ngayThi) {
        this.ngayThi = ngayThi;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public LichThi.CaThi getCaThi() {
        return caThi;
    }

    public void setCaThi(LichThi.CaThi caThi) {
        this.caThi = caThi;
    }

    public int getSoLuongDangKy() {
        return soLuongDangKy;
    }

    public void setSoLuongDangKy(int soLuongDangKy) {
        this.soLuongDangKy = soLuongDangKy;
    }
}
