package com.quanly.trungtamngoaingu.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "DangKyKhoaHoc")
public class DangKyKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDangKy;


    @ManyToOne
    @JoinColumn(name = "maHocVien")
    private HocVien hocVien;

    @ManyToOne
    @JoinColumn(name = "maKhoaHoc")
    private KhoaHoc khoaHoc;

    @Column(name = "ngayDangKy", nullable = false)
    private Date ngayDangKy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiDangKyHoc trangThaiDangKyHoc;
    public enum TrangThaiDangKyHoc {
        CHUA_DUYET,
        DA_DUYET,
        DA_PHAN_LOP
    }
    public DangKyKhoaHoc() {
    }

    public DangKyKhoaHoc(Long maDangKy, HocVien hocVien, KhoaHoc khoaHoc,
                         Date ngayDangKy, TrangThaiDangKyHoc trangThaiDangKyHoc) {
        this.maDangKy = maDangKy;
        this.hocVien = hocVien;
        this.khoaHoc = khoaHoc;
        this.ngayDangKy = ngayDangKy;
        this.trangThaiDangKyHoc = trangThaiDangKyHoc;
    }

    public TrangThaiDangKyHoc getTrangThaiDangKyHoc() {
        return trangThaiDangKyHoc;
    }

    public void setTrangThaiDangKyHoc(TrangThaiDangKyHoc trangThaiDangKyHoc) {
        this.trangThaiDangKyHoc = trangThaiDangKyHoc;
    }

    public Long getMaDangKy() {
        return maDangKy;
    }

    public void setMaDangKy(Long maDangKy) {
        this.maDangKy = maDangKy;
    }

    public HocVien getHocVien() {
        return hocVien;
    }

    public void setHocVien(HocVien hocVien) {
        this.hocVien = hocVien;
    }

    public KhoaHoc getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(KhoaHoc khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }
}
