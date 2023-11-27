package com.quanly.trungtamngoaingu.payload.response;

import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Date;

public class HocVienResponse {
    private String hoTen;
    private String tenDangNhap;
    private String soDienThoai;
    private String email;
    private String diaChi;
    @Enumerated(EnumType.STRING)
    private TaiKhoan.GioiTinh gioiTinh;
    private Date ngaySinh;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public TaiKhoan.GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(TaiKhoan.GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}

