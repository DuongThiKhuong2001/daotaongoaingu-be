package com.quanly.trungtamngoaingu.payload.request;

import com.quanly.trungtamngoaingu.entity.DangKyKhoaHoc;

import java.util.Date;

public class DangKyKhoaHocRequest {
    private String tenDangNhap;
    private Long maKhoaHoc;
    private DangKyKhoaHoc.TrangThaiDangKyHoc trangThaiDangKyHoc;

    public DangKyKhoaHoc.TrangThaiDangKyHoc getTrangThaiDangKyHoc() {
        return trangThaiDangKyHoc;
    }

    public void setTrangThaiDangKyHoc(DangKyKhoaHoc.TrangThaiDangKyHoc trangThaiDangKyHoc) {
        this.trangThaiDangKyHoc = trangThaiDangKyHoc;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public Long getMaKhoaHoc() {
        return maKhoaHoc;
    }

    public void setMaKhoaHoc(Long maKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
    }
}
