package com.quanly.trungtamngoaingu.payload.response;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String tenTaiKhoan;
  private String quyen;

  public JwtResponse() {
  }

  public JwtResponse(String token, String tenTaiKhoan, String quyen) {
    this.token = token;
    this.tenTaiKhoan = tenTaiKhoan;
    this.quyen = quyen;
  }

  public String getTenTaiKhoan() {
    return tenTaiKhoan;
  }

  public void setTenTaiKhoan(String tenTaiKhoan) {
    this.tenTaiKhoan = tenTaiKhoan;
  }

  public String getQuyen() {
    return quyen;
  }

  public void setQuyen(String quyen) {
    this.quyen = quyen;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
