package com.quanly.trungtamngoaingu.payload.request;

import java.util.Date;

public class KhoaHocRequest {
    private String tenKhoaHoc;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Long maLoaiLop;

    // Các getters và setters

    public String getTenKhoaHoc() {
        return tenKhoaHoc;
    }

    public void setTenKhoaHoc(String tenKhoaHoc) {
        this.tenKhoaHoc = tenKhoaHoc;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }


    public Long getMaLoaiLop() {
        return maLoaiLop;
    }

    public void setMaLoaiLop(Long maLoaiLop) {
        this.maLoaiLop = maLoaiLop;
    }
}
