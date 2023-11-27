package com.quanly.trungtamngoaingu.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maThongBao;

    @ManyToOne
    @JoinColumn(name = "maTaiKhoan")
    private TaiKhoan taiKhoan;
    private String tieuDe;
    @Column(columnDefinition = "TEXT")
    private String noiDung;
    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;
    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
    public enum TrangThai {
        DaDoc, ChuaDoc
    }
    public ThongBao() {
    }

    public ThongBao(Long maThongBao, TaiKhoan taiKhoan,
                    String tieuDe, String noiDung, TrangThai trangThai,
                    LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maThongBao = maThongBao;
        this.taiKhoan = taiKhoan;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public Long getMaThongBao() {
        return maThongBao;
    }

    public void setMaThongBao(Long maThongBao) {
        this.maThongBao = maThongBao;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

}

