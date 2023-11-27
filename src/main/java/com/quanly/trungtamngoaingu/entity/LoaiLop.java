package com.quanly.trungtamngoaingu.entity;

import jakarta.persistence.*;

@Entity
public class LoaiLop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLoaiLop;

    private String tenLoaiLop;
    @Column(columnDefinition = "TEXT")
    private  String tomTatDeCuong;
    private String deCuong;
    private Long hocPhi;
    public LoaiLop() {
    }

    public LoaiLop(Long maLoaiLop, String tenLoaiLop, String tomTatDeCuong, String deCuong, Long hocPhi) {
        this.maLoaiLop = maLoaiLop;
        this.tenLoaiLop = tenLoaiLop;
        this.tomTatDeCuong = tomTatDeCuong;
        this.deCuong = deCuong;
        this.hocPhi = hocPhi;
    }

    public String getTomTatDeCuong() {
        return tomTatDeCuong;
    }

    public void setTomTatDeCuong(String tomTatDeCuong) {
        this.tomTatDeCuong = tomTatDeCuong;
    }

    public Long getHocPhi() {
        return hocPhi;
    }

    public void setHocPhi(Long hocPhi) {
        this.hocPhi = hocPhi;
    }

    public String getDeCuong() {
        return deCuong;
    }

    public void setDeCuong(String deCuong) {
        this.deCuong = deCuong;
    }

    public Long getMaLoaiLop() {
        return maLoaiLop;
    }

    public void setMaLoaiLop(Long maLoaiLop) {
        this.maLoaiLop = maLoaiLop;
    }

    public String getTenLoaiLop() {
        return tenLoaiLop;
    }

    public void setTenLoaiLop(String tenLoaiLop) {
        this.tenLoaiLop = tenLoaiLop;
    }
}

