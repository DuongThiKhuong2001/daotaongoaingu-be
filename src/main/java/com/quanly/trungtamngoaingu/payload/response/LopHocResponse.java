package com.quanly.trungtamngoaingu.payload.response;


import com.quanly.trungtamngoaingu.entity.*;

public class LopHocResponse {
    private Long maLop;
    private String tenLop;
    private LichHoc lichHoc;
    private Phong phong;
    private KhoaHoc khoaHoc;
    private int soLuong;
    private int soLuongHocVien;
    private GiaoVien giaoVien;
    private LopHoc.HinhThucHoc hinhThucHoc;

    public LopHoc.HinhThucHoc getHinhThucHoc() {
        return hinhThucHoc;
    }

    public void setHinhThucHoc(LopHoc.HinhThucHoc hinhThucHoc) {
        this.hinhThucHoc = hinhThucHoc;
    }

    public GiaoVien getGiaoVien() {
        return giaoVien;
    }

    public void setGiaoVien(GiaoVien giaoVien) {
        this.giaoVien = giaoVien;
    }


    public Long getMaLop() {
        return maLop;
    }

    public void setMaLop(Long maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public LichHoc getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(LichHoc lichHoc) {
        this.lichHoc = lichHoc;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public KhoaHoc getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(KhoaHoc khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getSoLuongHocVien() {
        return soLuongHocVien;
    }

    public void setSoLuongHocVien(int soLuongHocVien) {
        this.soLuongHocVien = soLuongHocVien;
    }
}

