package com.quanly.trungtamngoaingu.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "VaiTro")
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maVaiTro;
    @Column(nullable = false, unique = true)
    private String tenVaiTro;

    private String moTa;
    @ManyToMany(mappedBy = "vaiTros")
    @JsonIgnore
    private Set<NhanVien> nhanViens = new HashSet<>();
    public VaiTro() {
    }

    public VaiTro(Long maVaiTro, String tenVaiTro, String moTa, Set<NhanVien> nhanViens) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
        this.nhanViens = nhanViens;
    }


    public Set<NhanVien> getNhanViens() {
        return nhanViens;
    }

    public void setNhanViens(Set<NhanVien> nhanViens) {
        this.nhanViens = nhanViens;
    }

    public Long getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(Long maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
