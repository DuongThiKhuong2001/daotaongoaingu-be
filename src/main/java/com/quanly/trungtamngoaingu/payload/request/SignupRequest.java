package com.quanly.trungtamngoaingu.payload.request;

public class SignupRequest {
  private String ten;
  private String email;
  private String taiKhoan;
  private String matKhau;
  private String quyen = "BENHNHAN";
  public String getTen() {
    return ten;
  }

  public String getQuyen() {
    return quyen;
  }

  public void setQuyen(String quyen) {
    this.quyen = quyen;
  }

  public void setTen(String ten) {
    this.ten = ten;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTaiKhoan() {
    return taiKhoan;
  }

  public void setTaiKhoan(String taiKhoan) {
    this.taiKhoan = taiKhoan;
  }

  public String getMatKhau() {
    return matKhau;
  }

  public void setMatKhau(String matKhau) {
    this.matKhau = matKhau;
  }
}
