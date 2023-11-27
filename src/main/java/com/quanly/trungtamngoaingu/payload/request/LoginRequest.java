package com.quanly.trungtamngoaingu.payload.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
	@NotBlank
	private String taiKhoan;

	@NotBlank
	private String matKhau;

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