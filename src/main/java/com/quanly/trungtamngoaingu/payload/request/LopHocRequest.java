package com.quanly.trungtamngoaingu.payload.request;


import com.quanly.trungtamngoaingu.entity.LopHoc;
import jakarta.validation.constraints.*;
import org.hibernate.mapping.Set;

import java.util.Date;

public class LopHocRequest {

    private Long maLop;
    private int soLuong;
    private Long maPhong;
    private Long maKhoaHoc;
    private String tenLop;
    private String tenTaiKhoan;
    private Long maLichHoc;
    private LopHoc.HinhThucHoc hinhThucHoc;

    public LopHoc.HinhThucHoc getHinhThucHoc() {
        return hinhThucHoc;
    }

    public void setHinhThucHoc(LopHoc.HinhThucHoc hinhThucHoc) {
        this.hinhThucHoc = hinhThucHoc;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public Long getMaLichHoc() {
        return maLichHoc;
    }

    public void setMaLichHoc(Long maLichHoc) {
        this.maLichHoc = maLichHoc;
    }

    public Long getMaLop() {
        return maLop;
    }

    public void setMaLop(Long maLop) {
        this.maLop = maLop;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public Long getMaKhoaHoc() {
        return maKhoaHoc;
    }

    public void setMaKhoaHoc(Long maKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

}
