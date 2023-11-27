package com.quanly.trungtamngoaingu.payload.response;

import com.quanly.trungtamngoaingu.entity.GiaoVien;

public class GiaoVienResponse {
    private GiaoVien giaoVien;
    private int soLuongLopHocHienTai;

    public GiaoVienResponse(GiaoVien giaoVien, int soLuongLopHocHienTai) {
        this.giaoVien = giaoVien;
        this.soLuongLopHocHienTai = soLuongLopHocHienTai;
    }

    public GiaoVien getGiaoVien() {
        return giaoVien;
    }

    public void setGiaoVien(GiaoVien giaoVien) {
        this.giaoVien = giaoVien;
    }

    public int getSoLuongLopHocHienTai() {
        return soLuongLopHocHienTai;
    }

    public void setSoLuongLopHocHienTai(int soLuongLopHocHienTai) {
        this.soLuongLopHocHienTai = soLuongLopHocHienTai;
    }
}
