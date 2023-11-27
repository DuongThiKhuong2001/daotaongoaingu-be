package com.quanly.trungtamngoaingu.payload.request;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.List;

public class KyThiRequest {
    private Integer thangThi;
    private Integer namThi;
    private Long maChungChi;
    private Long soLuongDuocDangKy;
    private List<LocalDate> danhSachNgayThi;

    public Long getSoLuongDuocDangKy() {
        return soLuongDuocDangKy;
    }

    public void setSoLuongDuocDangKy(Long soLuongDuocDangKy) {
        this.soLuongDuocDangKy = soLuongDuocDangKy;
    }

    public Integer getThangThi() {
        return thangThi;
    }

    public void setThangThi(Integer thangThi) {
        this.thangThi = thangThi;
    }

    public Integer getNamThi() {
        return namThi;
    }

    public void setNamThi(Integer namThi) {
        this.namThi = namThi;
    }

    public Long getMaChungChi() {
        return maChungChi;
    }

    public void setMaChungChi(Long maChungChi) {
        this.maChungChi = maChungChi;
    }

    public List<LocalDate> getDanhSachNgayThi() {
        return danhSachNgayThi;
    }

    public void setDanhSachNgayThi(List<LocalDate> danhSachNgayThi) {
        this.danhSachNgayThi = danhSachNgayThi;
    }
}
