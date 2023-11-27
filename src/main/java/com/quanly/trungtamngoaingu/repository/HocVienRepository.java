package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.DangKyKhoaHoc;
import com.quanly.trungtamngoaingu.entity.HocVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HocVienRepository  extends JpaRepository<HocVien, Long>, JpaSpecificationExecutor<HocVien> {
    Page<HocVien> findAll(Specification<HocVien> spec, Pageable pageable);
    HocVien findByTaiKhoan_TenDangNhap(String ten);
}