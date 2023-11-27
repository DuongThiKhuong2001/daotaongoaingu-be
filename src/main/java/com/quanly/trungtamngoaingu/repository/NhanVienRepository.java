package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NhanVienRepository  extends JpaRepository<NhanVien, Long>, JpaSpecificationExecutor<NhanVien> {
    Page<NhanVien> findAll(Specification<NhanVien> spec, Pageable pageable);
    NhanVien findByTaiKhoan_TenDangNhap(String tenTk);
}