package com.quanly.trungtamngoaingu.payload.request;

public class GiaoVienGacThiRequest {
    private Long maGiaoVien; // ID của giáo viên
    private Long maLichThi; // ID của lịch thi

    // Getters và Setters

    public Long getMaGiaoVien() {
        return maGiaoVien;
    }

    public void setMaGiaoVien(Long maGiaoVien) {
        this.maGiaoVien = maGiaoVien;
    }

    public Long getMaLichThi() {
        return maLichThi;
    }

    public void setMaLichThi(Long maLichThi) {
        this.maLichThi = maLichThi;
    }
}
