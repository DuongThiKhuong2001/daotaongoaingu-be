package com.quanly.trungtamngoaingu.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "NhanVien")
public class NhanVien {

    @Id
    private Long maTaiKhoan;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan")
    @MapsId
    private TaiKhoan taiKhoan;
    @ManyToMany
    @JoinTable(name = "NhanVien_VaiTro",
            joinColumns = @JoinColumn(name = "maTaiKhoan"),
            inverseJoinColumns = @JoinColumn(name = "vaiTroId"))
    @JsonIgnore
    private Set<VaiTro> vaiTros = new HashSet<>();

    public NhanVien() {
    }

    public NhanVien(Long maTaiKhoan, TaiKhoan taiKhoan, Set<VaiTro> vaiTros) {
        this.maTaiKhoan = maTaiKhoan;
        this.taiKhoan = taiKhoan;
        this.vaiTros = vaiTros;
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public Long getMaNhanVien() {
        return maTaiKhoan;
    }

    public void setMaNhanVien(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public Set<VaiTro> getVaiTros() {
        return vaiTros;
    }

    public void setVaiTros(Set<VaiTro> vaiTros) {
        this.vaiTros = vaiTros;
    }
}
