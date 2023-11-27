package com.quanly.trungtamngoaingu.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long maTaiKhoan;

  private String tenDangNhap;

  private String email;

  @JsonIgnore
  private String matKhau;

  private GrantedAuthority authority;

  public UserDetailsImpl(Long maTaiKhoan, String tenDangNhap, String email, String matKhau,
                         GrantedAuthority authority) {
    this.maTaiKhoan = maTaiKhoan;
    this.tenDangNhap = tenDangNhap;
    this.email = email;
    this.matKhau = matKhau;
    this.authority = authority;
  }

  public static UserDetailsImpl build(TaiKhoan taiKhoan) {
    GrantedAuthority authority = new SimpleGrantedAuthority(taiKhoan.getQuyen().name());
    return new UserDetailsImpl(
            taiKhoan.getMaTaiKhoan(),
            taiKhoan.getTenDangNhap(),
            taiKhoan.getEmail(),
            taiKhoan.getMatKhau(),
            authority);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(authority);
  }

  public GrantedAuthority getAuthority() {
    return authority;
  }
  public Long getId() {
    return maTaiKhoan;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return matKhau;
  }

  @Override
  public String getUsername() {
    return tenDangNhap;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(maTaiKhoan, user.maTaiKhoan);
  }
}
