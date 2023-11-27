package com.quanly.trungtamngoaingu.entity;
import jakarta.persistence.*;
import java.util.Date;
@Entity
public class DangKyThi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDangKyThi;

    @ManyToOne
    @JoinColumn(name = "maHocVien")
    private HocVien hocVien;

    @ManyToOne
    @JoinColumn(name = "maKyThi")
    private KyThi kyThi;

    private Date ngayDangKy;
    @ManyToOne
    @JoinColumn(name = "maLichThi")
    private LichThi lichThi;

    @Enumerated(EnumType.STRING)
    private TrangThaiDangKyThi trangThaiDangKyThi;
    public enum TrangThaiDangKyThi{
        Chua_Duyet, Da_Duyet, Da_Sap_Lich, Da_Len_Diem
    }

    public DangKyThi() {
    }

    public DangKyThi(Long maDangKyThi, HocVien hocVien, KyThi kyThi,
                     Date ngayDangKy, LichThi lichThi, TrangThaiDangKyThi trangThaiDangKyThi) {
        this.maDangKyThi = maDangKyThi;
        this.hocVien = hocVien;
        this.kyThi = kyThi;
        this.ngayDangKy = ngayDangKy;
        this.lichThi = lichThi;
        this.trangThaiDangKyThi = trangThaiDangKyThi;
    }

    public LichThi getLichThi() {
        return lichThi;
    }

    public void setLichThi(LichThi lichThi) {
        this.lichThi = lichThi;
    }

    public Long getMaDangKyThi() {
        return maDangKyThi;
    }

    public void setMaDangKyThi(Long maDangKyThi) {
        this.maDangKyThi = maDangKyThi;
    }

    public HocVien getHocVien() {
        return hocVien;
    }

    public void setHocVien(HocVien hocVien) {
        this.hocVien = hocVien;
    }

    public KyThi getKyThi() {
        return kyThi;
    }

    public void setKyThi(KyThi kyThi) {
        this.kyThi = kyThi;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public TrangThaiDangKyThi getTrangThaiDangKyThi() {
        return trangThaiDangKyThi;
    }

    public void setTrangThaiDangKyThi(TrangThaiDangKyThi trangThaiDangKyThi) {
        this.trangThaiDangKyThi = trangThaiDangKyThi;
    }
}

