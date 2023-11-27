package com.quanly.trungtamngoaingu.entity;


import jakarta.persistence.*;

@Entity
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPhong;

    private String tenPhong;
    @Column(unique = true)
    private String kiHieu;

    private Integer sucChua;
    @Column(columnDefinition = "TEXT")

    private String viTri;
    @Enumerated(EnumType.STRING)
    private LoaiPhong loaiPhong;
    public enum LoaiPhong{
        Thi, Hoc
    }
    public Phong() {
    }

    public Phong(Long maPhong, String tenPhong, String kiHieu, Integer sucChua, String viTri, LoaiPhong loaiPhong) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.kiHieu = kiHieu;
        this.sucChua = sucChua;
        this.viTri = viTri;
        this.loaiPhong = loaiPhong;
    }

    public LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhongHoc) {
        this.maPhong = maPhongHoc;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getKiHieu() {
        return kiHieu;
    }

    public void setKiHieu(String kiHieu) {
        this.kiHieu = kiHieu;
    }
}
