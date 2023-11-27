package com.quanly.trungtamngoaingu.entity;


import java.util.Date;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

    @Entity
    public class KhoaHoc {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long maKhoaHoc;

        private String tenKhoaHoc;
        private Date ngayBatDau;

        private Date ngayKetThuc;

        @ManyToOne
        @JoinColumn(name = "maLoaiLop")
        private LoaiLop loaiLop;
    public KhoaHoc() {
    }
    @Formula("(CASE WHEN ngay_bat_dau <= CURRENT_DATE THEN 'DA_DIEN_RA' ELSE 'CHUA_DIEN_RA' END)")
    private String trangThai;
    public KhoaHoc(Long maKhoaHoc, String tenKhoaHoc, Date ngayBatDau,
                   Date ngayKetThuc, LoaiLop loaiLop) {
        this.maKhoaHoc = maKhoaHoc;
        this.tenKhoaHoc = tenKhoaHoc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.loaiLop = loaiLop;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Long getMaKhoaHoc() {
        return maKhoaHoc;
    }

    public void setMaKhoaHoc(Long maKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
    }

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

    public LoaiLop getLoaiLop() {
        return loaiLop;
    }

    public void setLoaiLop(LoaiLop loaiLop) {
        this.loaiLop = loaiLop;
    }
}
