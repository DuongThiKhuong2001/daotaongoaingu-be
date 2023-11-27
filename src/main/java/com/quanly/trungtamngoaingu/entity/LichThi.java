package com.quanly.trungtamngoaingu.entity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class LichThi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLichThi;

    @ManyToOne
    @JoinColumn(name = "maKyThi")
    private KyThi kyThi;

    private Date ngayThi;
    private String fileDiemDanh;
    @ManyToOne
    @JoinColumn(name = "maPhong")
    private Phong phong;

    @Enumerated(EnumType.STRING)
    private CaThi caThi;
    public enum CaThi{
        Sang, Chieu
    }
    public LichThi() {
    }

    public LichThi(Long maLichThi, KyThi kyThi, Date ngayThi, String fileDiemDanh, Phong phong, CaThi caThi) {
        this.maLichThi = maLichThi;
        this.kyThi = kyThi;
        this.ngayThi = ngayThi;
        this.fileDiemDanh = fileDiemDanh;
        this.phong = phong;
        this.caThi = caThi;
    }

    public String getFileDiemDanh() {
        return fileDiemDanh;
    }

    public void setFileDiemDanh(String fileDiemDanh) {
        this.fileDiemDanh = fileDiemDanh;
    }

    public Long getMaLichThi() {
        return maLichThi;
    }

    public void setMaLichThi(Long maLichThi) {
        this.maLichThi = maLichThi;
    }

    public Date getNgayThi() {
        return ngayThi;
    }

    public void setNgayThi(Date ngayThi) {
        this.ngayThi = ngayThi;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public CaThi getCaThi() {
        return caThi;
    }

    public void setCaThi(CaThi caThi) {
        this.caThi = caThi;
    }

    public KyThi getKyThi() {
        return kyThi;
    }

    public void setKyThi(KyThi kyThi) {
        this.kyThi = kyThi;
    }
}

