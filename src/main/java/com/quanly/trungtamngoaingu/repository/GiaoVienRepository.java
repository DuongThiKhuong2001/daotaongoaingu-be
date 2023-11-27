package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.GiaoVien;
import com.quanly.trungtamngoaingu.entity.HocVien;
import com.quanly.trungtamngoaingu.entity.LichHoc;
import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface GiaoVienRepository  extends JpaRepository<GiaoVien, Long>, JpaSpecificationExecutor<GiaoVien> {
    Page<GiaoVien> findAll(Specification<GiaoVien> spec, Pageable pageable);
    GiaoVien findByTaiKhoan_TenDangNhap(String ten);
    @Query("SELECT gv FROM GiaoVien gv " +
            "WHERE NOT EXISTS (" +
            "  SELECT 1 FROM LopHoc lh " +
            "  WHERE lh.giaoVien = gv " +
            "  AND lh.lichHoc.kiHieu = :kiHieuLichHoc " +
            "  AND lh.khoaHoc.ngayBatDau <= :ngayKetThuc " +
            "  AND lh.khoaHoc.ngayKetThuc >= :ngayBatDau" +
            ")")
    List<GiaoVien> findValidGiaoVienForLopHoc(
            String kiHieuLichHoc,
            Date ngayBatDau,
            Date ngayKetThuc
    );
    @Query("SELECT gv FROM GiaoVien gv WHERE NOT EXISTS (SELECT kt FROM KyThi kt JOIN kt.giaoViens gv2 WHERE kt.maKyThi = :maKyThi AND gv = gv2)")
    List<GiaoVien> findAvailableTeachersForExam(@Param("maKyThi") Long maKyThi);
    List<GiaoVien> findAll();
}
