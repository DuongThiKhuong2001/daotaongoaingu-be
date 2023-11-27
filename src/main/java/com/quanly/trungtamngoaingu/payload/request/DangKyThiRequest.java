package com.quanly.trungtamngoaingu.payload.request;


import com.quanly.trungtamngoaingu.entity.DangKyThi;

import java.util.Date;

public class DangKyThiRequest {
    private Long maHocVien;
    private Long maKyThi;
    private Long maLichThi;
   private String tenTaiKhoan;
    private DangKyThi.TrangThaiDangKyThi trangThaiDangKyThi;

    public Long getMaLichThi() {
        return maLichThi;
    }

    public void setMaLichThi(Long maLichThi) {
        this.maLichThi = maLichThi;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public Long getMaHocVien() {
        return maHocVien;
    }

    public void setMaHocVien(Long maHocVien) {
        this.maHocVien = maHocVien;
    }

    public Long getMaKyThi() {
        return maKyThi;
    }

    public void setMaKyThi(Long maKyThi) {
        this.maKyThi = maKyThi;
    }


    public DangKyThi.TrangThaiDangKyThi getTrangThaiDangKyThi() {
        return trangThaiDangKyThi;
    }

    public void setTrangThaiDangKyThi(DangKyThi.TrangThaiDangKyThi trangThaiDangKyThi) {
        this.trangThaiDangKyThi = trangThaiDangKyThi;
    }
}
