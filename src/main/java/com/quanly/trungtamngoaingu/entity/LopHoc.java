package com.quanly.trungtamngoaingu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class LopHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLop;
    private int soLuong;
    @ManyToOne
    @JoinColumn(name = "maPhong")
    private Phong phong;
    @ManyToOne
    @JoinColumn(name = "maKhoaHoc")
    private KhoaHoc khoaHoc;
    @ManyToOne
    @JoinColumn(name = "maLichHoc")
    private LichHoc lichHoc;
    private String tenLop;
    @ManyToMany
    @JoinTable(
            name = "HocVien_LopHoc",
            joinColumns = @JoinColumn(name = "maLop"),
            inverseJoinColumns = @JoinColumn(name = "maHocVien")
    )
    @JsonIgnore
    private Set<HocVien> hocViens = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "maGiaoVien")
    private GiaoVien giaoVien;
    public enum HinhThucHoc {
        Online,
        Offline;
    }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HinhThucHoc hinhThucHoc;
    private String fileDiemDanh;
    public LopHoc() {
    }

    public LopHoc(Long maLop, int soLuong, Phong phong, KhoaHoc khoaHoc, LichHoc lichHoc,
                  String tenLop, Set<HocVien> hocViens, GiaoVien giaoVien, HinhThucHoc hinhThucHoc,
                  String fileDiemDanh) {
        this.maLop = maLop;
        this.soLuong = soLuong;
        this.phong = phong;
        this.khoaHoc = khoaHoc;
        this.lichHoc = lichHoc;
        this.tenLop = tenLop;
        this.hocViens = hocViens;
        this.giaoVien = giaoVien;
        this.hinhThucHoc = hinhThucHoc;
        this.fileDiemDanh = fileDiemDanh;
    }

    public String getFileDiemDanh() {
        return fileDiemDanh;
    }

    public void setFileDiemDanh(String fileDiemDanh) {
        this.fileDiemDanh = fileDiemDanh;
    }

    public HinhThucHoc getHinhThucHoc() {
        return hinhThucHoc;
    }

    public void setHinhThucHoc(HinhThucHoc hinhThucHoc) {
        this.hinhThucHoc = hinhThucHoc;
    }

    public int getSoLuongHocVienHienTai() {
        return this.hocViens.size();
    }

    public GiaoVien getGiaoVien() {
        return giaoVien;
    }

    public void setGiaoVien(GiaoVien giaoVien) {
        this.giaoVien = giaoVien;
    }

    public Long getMaLop() {
        return maLop;
    }

    public void setMaLop(Long maLop) {
        this.maLop = maLop;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public KhoaHoc getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(KhoaHoc khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public LichHoc getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(LichHoc lichHoc) {
        this.lichHoc = lichHoc;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public Set<HocVien> getHocViens() {
        return hocViens;
    }

    public void setHocViens(Set<HocVien> hocViens) {
        this.hocViens = hocViens;
    }
}
