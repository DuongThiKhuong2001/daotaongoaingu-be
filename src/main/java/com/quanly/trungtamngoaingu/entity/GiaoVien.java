package com.quanly.trungtamngoaingu.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "GiaoVien")
public class GiaoVien {

    @Id
    private Long maTaiKhoan;

    private String trinhDo;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan")
    @MapsId
    private TaiKhoan taiKhoan;

    public GiaoVien() {
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public GiaoVien(Long maTaiKhoan, String trinhDo, TaiKhoan taiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
        this.trinhDo = trinhDo;
        this.taiKhoan = taiKhoan;
    }

    public String getTrinhDo() {
        return trinhDo;
    }

    public void setTrinhDo(String trinhDo) {
        this.trinhDo = trinhDo;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }
}
