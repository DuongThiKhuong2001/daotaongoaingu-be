package com.quanly.trungtamngoaingu.entity;
import jakarta.persistence.*;
@Entity
public class PhanCongGiaoVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPhanCongGiaoVien;

    @ManyToOne
    @JoinColumn(name = "maGiaoVien")
    private GiaoVien giaoVien;

    @ManyToOne
    @JoinColumn(name = "maLichThi")
    private LichThi lichThi;
    @Enumerated(EnumType.STRING)
    private LoaiPhanCong loaiPhanCong;
    public enum LoaiPhanCong{
        Gac_Thi, Len_Diem
    }
    public PhanCongGiaoVien() {
    }

    public PhanCongGiaoVien(Long maPhanCongGiaoVien, GiaoVien giaoVien, LichThi lichThi, LoaiPhanCong loaiPhanCong) {
        this.maPhanCongGiaoVien = maPhanCongGiaoVien;
        this.giaoVien = giaoVien;
        this.lichThi = lichThi;
        this.loaiPhanCong = loaiPhanCong;
    }

    public Long getMaPhanCongGiaoVien() {
        return maPhanCongGiaoVien;
    }

    public void setMaPhanCongGiaoVien(Long maPhanCongGiaoVien) {
        this.maPhanCongGiaoVien = maPhanCongGiaoVien;
    }

    public GiaoVien getGiaoVien() {
        return giaoVien;
    }

    public void setGiaoVien(GiaoVien giaoVien) {
        this.giaoVien = giaoVien;
    }

    public LichThi getLichThi() {
        return lichThi;
    }

    public void setLichThi(LichThi lichThi) {
        this.lichThi = lichThi;
    }

    public LoaiPhanCong getLoaiPhanCong() {
        return loaiPhanCong;
    }

    public void setLoaiPhanCong(LoaiPhanCong loaiPhanCong) {
        this.loaiPhanCong = loaiPhanCong;
    }
}

