package com.quanly.trungtamngoaingu.entity;

import jakarta.persistence.*;

@Entity
public class ChungChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChungChi;

    private String tenChungChi;

    @Column(columnDefinition = "TEXT")
    private String moTa;
    private Long lePhiThi;

    public ChungChi() {
    }

    public ChungChi(Long maChungChi, String tenChungChi, String moTa, Long lePhiThi) {
        this.maChungChi = maChungChi;
        this.tenChungChi = tenChungChi;
        this.moTa = moTa;
        this.lePhiThi = lePhiThi;
    }

    public Long getLePhiThi() {
        return lePhiThi;
    }

    public void setLePhiThi(Long lePhiThi) {
        this.lePhiThi = lePhiThi;
    }

    public Long getMaChungChi() {
        return maChungChi;
    }

    public void setMaChungChi(Long maChungChi) {
        this.maChungChi = maChungChi;
    }

    public String getTenChungChi() {
        return tenChungChi;
    }

    public void setTenChungChi(String tenChungChi) {
        this.tenChungChi = tenChungChi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}



