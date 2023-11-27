package com.quanly.trungtamngoaingu.entity;
import jakarta.persistence.*;
@Entity
public class KetQuaThi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKetQuaThi;

    @ManyToOne
    @JoinColumn(name = "maDangKyThi")
    private DangKyThi dangKyThi;
    @ManyToOne
    @JoinColumn(name = "maBacChungChi")
    private BacChungChi bacChungChi;
    private Float diemNghe;
    private Float diemNoi;
    private Float diemDoc;
    private Float diemViet;

    private Float diemTong;

    public KetQuaThi() {
    }

    public KetQuaThi(Long maKetQuaThi, DangKyThi dangKyThi, BacChungChi bacChungChi, Float diemNghe, Float diemNoi,
                     Float diemDoc, Float diemViet, Float diemTong, String ghiChu) {
        this.maKetQuaThi = maKetQuaThi;
        this.dangKyThi = dangKyThi;
        this.bacChungChi = bacChungChi;
        this.diemNghe = diemNghe;
        this.diemNoi = diemNoi;
        this.diemDoc = diemDoc;
        this.diemViet = diemViet;
        this.diemTong = diemTong;

    }

    public Long getMaKetQuaThi() {
        return maKetQuaThi;
    }

    public void setMaKetQuaThi(Long maKetQuaThi) {
        this.maKetQuaThi = maKetQuaThi;
    }

    public DangKyThi getDangKyThi() {
        return dangKyThi;
    }

    public void setDangKyThi(DangKyThi dangKyThi) {
        this.dangKyThi = dangKyThi;
    }

    public Float getDiemTong() {
        return diemTong;
    }

    public void setDiemTong(Float diemTong) {
        this.diemTong = diemTong;
    }


    public BacChungChi getBacChungChi() {
        return bacChungChi;
    }

    public void setBacChungChi(BacChungChi bacChungChi) {
        this.bacChungChi = bacChungChi;
    }

    public Float getDiemNghe() {
        return diemNghe;
    }

    public void setDiemNghe(Float diemNghe) {
        this.diemNghe = diemNghe;
    }

    public Float getDiemNoi() {
        return diemNoi;
    }

    public void setDiemNoi(Float diemNoi) {
        this.diemNoi = diemNoi;
    }

    public Float getDiemDoc() {
        return diemDoc;
    }

    public void setDiemDoc(Float diemDoc) {
        this.diemDoc = diemDoc;
    }

    public Float getDiemViet() {
        return diemViet;
    }

    public void setDiemViet(Float diemViet) {
        this.diemViet = diemViet;
    }

}

