package com.quanly.trungtamngoaingu.payload.request;


import com.quanly.trungtamngoaingu.entity.TaiKhoan;

import java.time.Year;
import java.util.Date;

public class TaiKhoanRequest {
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private TaiKhoan.GioiTinh gioiTinh;
    private Date ngaySinh;
    private TaiKhoan.Quyen quyen;



    // Nếu tạo tài khoản cho giáo viên
    private String trinhDo;

    // Nếu tạo tài khoản cho học viên
    private String soDTNguoiThan;
    private int lop;
    private String truongHoc;

    // Nếu tạo tài khoản cho nhân viên

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
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

    public TaiKhoan.Quyen getQuyen() {
        return quyen;
    }

    public void setQuyen(TaiKhoan.Quyen quyen) {
        this.quyen = quyen;
    }


    public String getTrinhDo() {
        return trinhDo;
    }

    public void setTrinhDo(String trinhDo) {
        this.trinhDo = trinhDo;
    }

    public int getLop() {
        return lop;
    }

    public void setLop(int lop) {
        this.lop = lop;
    }

    public String getSoDTNguoiThan() {
        return soDTNguoiThan;
    }

    public void setSoDTNguoiThan(String soDTNguoiThan) {
        this.soDTNguoiThan = soDTNguoiThan;
    }

    public String getTruongHoc() {
        return truongHoc;
    }

    public void setTruongHoc(String truongHoc) {
        this.truongHoc = truongHoc;
    }

}
