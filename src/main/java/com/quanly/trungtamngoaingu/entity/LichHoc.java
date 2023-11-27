package com.quanly.trungtamngoaingu.entity;

import jakarta.persistence.*;
@Entity
public class LichHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLichHoc;
    @Column(unique = true)
    private String kiHieu;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    public LichHoc() {
    }

    public LichHoc(Long maLichHoc, String kiHieu, String moTa) {
        this.maLichHoc = maLichHoc;
        this.kiHieu = kiHieu;
        this.moTa = moTa;
    }

    public Long getMaLichHoc() {
        return maLichHoc;
    }

    public void setMaLichHoc(Long maLichHoc) {
        this.maLichHoc = maLichHoc;
    }


    public String getKiHieu() {
        return kiHieu;
    }

    public void setKiHieu(String kiHieu) {
        this.kiHieu = kiHieu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

}
