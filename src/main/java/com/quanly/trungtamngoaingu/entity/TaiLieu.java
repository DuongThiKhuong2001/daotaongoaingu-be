package com.quanly.trungtamngoaingu.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class TaiLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTaiLieu;

    private String fileTaiLieu;

    @ManyToOne
    @JoinColumn(name ="maLoaiLop")
    private LoaiLop loaiLop;
    @CreationTimestamp
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
    public TaiLieu() {
    }

    public TaiLieu(Long maTaiLieu, String fileTaiLieu, LoaiLop loaiLop, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maTaiLieu = maTaiLieu;
        this.fileTaiLieu = fileTaiLieu;
        this.loaiLop = loaiLop;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public Long getMaTaiLieu() {
        return maTaiLieu;
    }

    public void setMaTaiLieu(Long maTaiLieu) {
        this.maTaiLieu = maTaiLieu;
    }

    public String getFileTaiLieu() {
        return fileTaiLieu;
    }

    public void setFileTaiLieu(String fileTaiLieu) {
        this.fileTaiLieu = fileTaiLieu;
    }

    public LoaiLop getLoaiLop() {
        return loaiLop;
    }

    public void setLoaiLop(LoaiLop loaiLop) {
        this.loaiLop = loaiLop;
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
