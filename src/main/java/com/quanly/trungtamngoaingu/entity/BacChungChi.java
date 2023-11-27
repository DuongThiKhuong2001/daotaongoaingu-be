package com.quanly.trungtamngoaingu.entity;

import jakarta.persistence.*;

@Entity
public class BacChungChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maBacChungChi;

    @ManyToOne
    @JoinColumn(name = "maChungChi")
    private ChungChi chungChi;

    @Column(nullable = false)
    private String bac;

    @Column(name = "diemToiThieu")
    private Float diemToiThieu;

    @Column(name = "diemToiDa")
    private Float diemToiDa;


    public BacChungChi() {
    }

    public BacChungChi(Long maBacChungChi, ChungChi chungChi,
                       String bac, Float diemToiThieu, Float diemToiDa) {
        this.maBacChungChi = maBacChungChi;
        this.chungChi = chungChi;
        this.bac = bac;
        this.diemToiThieu = diemToiThieu;
        this.diemToiDa = diemToiDa;
    }

    public Long getMaBacChungChi() {
        return maBacChungChi;
    }

    public void setMaBacChungChi(Long maBacChungChi) {
        this.maBacChungChi = maBacChungChi;
    }

    public ChungChi getChungChi() {
        return chungChi;
    }

    public void setChungChi(ChungChi chungChi) {
        this.chungChi = chungChi;
    }

    public String getBac() {
        return bac;
    }

    public void setBac(String bac) {
        this.bac = bac;
    }

    public Float getDiemToiThieu() {
        return diemToiThieu;
    }

    public void setDiemToiThieu(Float diemToiThieu) {
        this.diemToiThieu = diemToiThieu;
    }

    public Float getDiemToiDa() {
        return diemToiDa;
    }

    public void setDiemToiDa(Float diemToiDa) {
        this.diemToiDa = diemToiDa;
    }
}
