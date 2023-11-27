package com.quanly.trungtamngoaingu.payload.request;


import com.quanly.trungtamngoaingu.entity.LichThi;

import java.util.Date;

public class LichThiRequest {
    private Long maKyThi;
    private Date ngayThi;
    private Long maPhong;
    private LichThi.CaThi caThi;

    public Long getMaKyThi() {
        return maKyThi;
    }

    public void setMaKyThi(Long maKyThi) {
        this.maKyThi = maKyThi;
    }

    public Date getNgayThi() {
        return ngayThi;
    }

    public void setNgayThi(Date ngayThi) {
        this.ngayThi = ngayThi;
    }

    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public LichThi.CaThi getCaThi() {
        return caThi;
    }

    public void setCaThi(LichThi.CaThi caThi) {
        this.caThi = caThi;
    }
}
