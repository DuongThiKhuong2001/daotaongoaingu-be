package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.DangKyKhoaHoc;
import com.quanly.trungtamngoaingu.entity.KhoaHoc;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DangKyKhoaHocRepository extends JpaRepository<DangKyKhoaHoc, Long>, JpaSpecificationExecutor<DangKyKhoaHoc> {
    boolean existsByKhoaHoc_MaKhoaHoc(Long maKh);
    @Transactional
    @Modifying
    @Query("DELETE FROM DangKyKhoaHoc d WHERE d.trangThaiDangKyHoc = 'CHUA_DUYET' AND d.khoaHoc.trangThai = 'DA_DIEN_RA'")
    void deleteAllChuaDuyetAndDaDienRa();
    DangKyKhoaHoc findByHocVien_TaiKhoan_TenDangNhapAndKhoaHoc_MaKhoaHoc(String ten,Long ma);
    boolean existsByKhoaHoc_MaKhoaHocAndHocVien_MaTaiKhoan(Long maKh, Long maHv);
    boolean existsByHocVien_TaiKhoan_TenDangNhapAndKhoaHoc_MaKhoaHoc(String ten,Long ma);
    List<DangKyKhoaHoc> findAllByKhoaHocAndTrangThaiDangKyHoc(KhoaHoc khoaHoc, DangKyKhoaHoc.TrangThaiDangKyHoc trangThaiHocPhi);
}
