package com.quanly.trungtamngoaingu.repository;


import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long>, JpaSpecificationExecutor<TaiKhoan> {
    Optional<TaiKhoan> findByTenDangNhap(String ten);
    Optional<TaiKhoan> findByMaTaiKhoan(Long ma);
    Boolean existsByTenDangNhap(String ten);

    Boolean existsByQuyen(TaiKhoan.Quyen quyen);
    List<TaiKhoan> findByQuyen(TaiKhoan.Quyen quyen);

    Boolean existsByEmail(String email);
}

