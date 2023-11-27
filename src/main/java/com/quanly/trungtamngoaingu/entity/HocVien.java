package com.quanly.trungtamngoaingu.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "HocVien")
public class HocVien {

    @Id
    private Long maTaiKhoan;

    private String soDTNguoiThan;
    private int lop;
    private String truongHoc;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan")
    @MapsId
    private TaiKhoan taiKhoan;
    @ManyToMany(mappedBy = "hocViens")
    @JsonIgnore
    private Set<LopHoc> lopHocs = new HashSet<>();
    public HocVien() {
    }

    public HocVien(Long maTaiKhoan, String soDTNguoiThan, int lop, String truongHoc,
                   TaiKhoan taiKhoan, Set<LopHoc> lopHocs) {
        this.maTaiKhoan = maTaiKhoan;
        this.soDTNguoiThan = soDTNguoiThan;
        this.lop = lop;
        this.truongHoc = truongHoc;
        this.taiKhoan = taiKhoan;
        this.lopHocs = lopHocs;
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public Set<LopHoc> getLopHocs() {
        return lopHocs;
    }

    public void setLopHocs(Set<LopHoc> lopHocs) {
        this.lopHocs = lopHocs;
    }

    public Long getMaHocVien() {
        return maTaiKhoan;
    }

    public void setMaHocVien(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getSoDTNguoiThan() {
        return soDTNguoiThan;
    }

    public void setSoDTNguoiThan(String soDTNguoiThan) {
        this.soDTNguoiThan = soDTNguoiThan;
    }

    public int getLop() {
        return lop;
    }

    public void setLop(int lop) {
        this.lop = lop;
    }

    public String getTruongHoc() {
        return truongHoc;
    }

    public void setTruongHoc(String truongHoc) {
        this.truongHoc = truongHoc;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }


}