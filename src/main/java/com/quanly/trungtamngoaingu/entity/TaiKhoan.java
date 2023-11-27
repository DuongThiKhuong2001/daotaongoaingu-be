package com.quanly.trungtamngoaingu.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTaiKhoan;

    @Column(unique = true , nullable = false)
    private String tenDangNhap;
    @JsonIgnore
    @Column(nullable = false)
    private String matKhau;

    private String hoTen;
    @Column(unique = true)
    private String email;
    private String soDienThoai;
    private String diaChi;
    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;

    public enum GioiTinh {
        Nam, Nu, Khac
    }
    private Date ngaySinh;
    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Quyen quyen;

    public enum Quyen {
        QuanTriVien, GiaoVien, HocVien, NhanVien
    }
    public enum TrangThai {
        Mo, Khoa
    }
    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;
    public TaiKhoan() {

    }

    public TaiKhoan(String tenDangNhap, String matKhau,
                    String hoTen, String email, String soDienThoai,
                    String diaChi, GioiTinh gioiTinh, Date ngaySinh,
                    Quyen quyen) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.quyen = quyen;
        this.trangThai = TrangThai.Mo;
    }

    public TaiKhoan(Long maTaiKhoan, String tenDangNhap,
                    String matKhau, String hoTen, String email,
                    String soDienThoai, String diaChi,
                    GioiTinh gioiTinh, Date ngaySinh, LocalDateTime ngayTao,
                    LocalDateTime ngayCapNhat, Quyen quyen, TrangThai trangthai) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.quyen = quyen;
        this.trangThai = trangthai;
    }

    public TaiKhoan(String tenDangNhap, String matKhau, String hoTen,
                    String email, String soDienThoai, String diaChi,  GioiTinh gioiTinh, Date ngaySinh, Quyen quyen, TrangThai trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;

        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.quyen = quyen;
        this.trangThai = trangThai;
    }

    public void capNhatThongTin(String soDienThoai, Date ngaySinh, GioiTinh gioiTinh, String diaChi) {
        if (soDienThoai != null) {
            this.soDienThoai = soDienThoai;
        }
        if (ngaySinh != null) {
            this.ngaySinh = ngaySinh;
        }
        if (gioiTinh != null) {
            this.gioiTinh = gioiTinh;
        }
        if (diaChi != null) {
            this.diaChi = diaChi;
        }
        this.ngayCapNhat = LocalDateTime.now();
    }


    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangthai) {
        this.trangThai = trangthai;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
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

    public Quyen getQuyen() {
        return quyen;
    }

    public void setQuyen(Quyen quyen) {
        this.quyen = quyen;
    }
}