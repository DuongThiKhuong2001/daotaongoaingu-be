package com.quanly.trungtamngoaingu.repository;


import com.quanly.trungtamngoaingu.entity.KhoaHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Long>, JpaSpecificationExecutor<KhoaHoc> {
    boolean existsByLoaiLop_MaLoaiLop(Long maLl);
    boolean existsByTenKhoaHocAndNgayBatDauAndNgayKetThuc(String ten, Date ngayBd, Date ngaKt);
}
