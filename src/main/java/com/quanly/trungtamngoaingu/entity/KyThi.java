package com.quanly.trungtamngoaingu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

import java.util.HashSet;
import java.util.Set;

@Entity
public class KyThi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKyThi;

    private Integer thangThi;
    private Integer namThi;
    private Long soLuongDuocDangKy;
    @ManyToOne
    @JoinColumn(name = "maChungChi")
    private ChungChi chungChi;

    @OneToMany(mappedBy = "kyThi")
    @JsonIgnore
    private Set<LichThi> lichThis;
    @Formula("(CASE WHEN EXTRACT(YEAR FROM CURRENT_DATE) < nam_thi OR (EXTRACT(YEAR FROM CURRENT_DATE) = nam_thi AND EXTRACT(MONTH FROM CURRENT_DATE) < thang_thi) THEN 'Con_Han' WHEN EXTRACT(YEAR FROM CURRENT_DATE) > nam_thi OR (EXTRACT(YEAR FROM CURRENT_DATE) = nam_thi AND EXTRACT(MONTH FROM CURRENT_DATE) >= thang_thi) THEN 'Het_Han' END)")
    private String hanDangKy;
    @ManyToMany
    @JoinTable(
            name = "giao_vien_ra_de",
            joinColumns = @JoinColumn(name = "maKyThi"),
            inverseJoinColumns = @JoinColumn(name = "maTaiKhoan")
    )
    private Set<GiaoVien> giaoViens = new HashSet<>();


    public KyThi() {
    }

    public KyThi(Long maKyThi, Integer thangThi, Integer namThi, Long soLuongDuocDangKy, ChungChi chungChi,
                 Set<LichThi> lichThis, String hanDangKy, Set<GiaoVien> giaoViens) {
        this.maKyThi = maKyThi;
        this.thangThi = thangThi;
        this.namThi = namThi;
        this.soLuongDuocDangKy = soLuongDuocDangKy;
        this.chungChi = chungChi;
        this.lichThis = lichThis;
        this.hanDangKy = hanDangKy;
        this.giaoViens = giaoViens;
    }

    public Set<GiaoVien> getGiaoViens() {
        return giaoViens;
    }

    public void setGiaoViens(Set<GiaoVien> giaoViens) {
        this.giaoViens = giaoViens;
    }

    public String getHanDangKy() {
        return hanDangKy;
    }

    public void setHanDangKy(String hanDangKy) {
        this.hanDangKy = hanDangKy;
    }

    public Long getSoLuongDuocDangKy() {
        return soLuongDuocDangKy;
    }

    public void setSoLuongDuocDangKy(Long soLuongDuocDangKy) {
        this.soLuongDuocDangKy = soLuongDuocDangKy;
    }

    public Long getMaKyThi() {
        return maKyThi;
    }

    public void setMaKyThi(Long maKyThi) {
        this.maKyThi = maKyThi;
    }

    public Integer getThangThi() {
        return thangThi;
    }

    public void setThangThi(Integer thangThi) {
        this.thangThi = thangThi;
    }

    public Integer getNamThi() {
        return namThi;
    }

    public void setNamThi(Integer namThi) {
        this.namThi = namThi;
    }

    public ChungChi getChungChi() {
        return chungChi;
    }

    public void setChungChi(ChungChi chungChi) {
        this.chungChi = chungChi;
    }

    public Set<LichThi> getLichThis() {
        return lichThis;
    }

    public void setLichThis(Set<LichThi> lichThis) {
        this.lichThis = lichThis;
    }
}