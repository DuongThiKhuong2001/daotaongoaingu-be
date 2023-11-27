package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DangKyThiRepository extends JpaRepository<DangKyThi, Long> {
    List<DangKyThi> findAll(Sort sort);
    List<DangKyThi> findByMaDangKyThiIn(List<Long> maDangKyThiIds);
    List<DangKyThi> findByHocVien_TaiKhoan(TaiKhoan taiKhoan);
    @Query("SELECT d FROM DangKyThi d WHERE d.kyThi.hanDangKy = :han AND (d.trangThaiDangKyThi = :trangThaiDangKyThi1 OR d.trangThaiDangKyThi = :trangThaiDangKyThi2) ORDER BY d.ngayDangKy DESC")
    List<DangKyThi> findByKyThi_HanDangKyAndTrangThaiDangKyThiOrTrangThaiDangKyThi(
            @Param("han") String han,
            @Param("trangThaiDangKyThi1") DangKyThi.TrangThaiDangKyThi trangThaiDangKyThi1,
            @Param("trangThaiDangKyThi2") DangKyThi.TrangThaiDangKyThi trangThaiDangKyThi2
    );


    List<DangKyThi> findByHocVien(HocVien hocVien, Sort sort);
    boolean existsByHocVienAndKyThi(HocVien hocVien, KyThi kyThi);
    DangKyThi findByHocVienAndKyThi(HocVien hocVien, KyThi kyThi);
    @Query("SELECT COUNT(d) FROM DangKyThi d WHERE d.kyThi.maKyThi = :maKyThi")
    Long countByMaKyThi(@Param("maKyThi") Long maKyThi);
    Long countByLichThi(LichThi lichThi);
    List<DangKyThi> findByLichThiNgayThiAfter(Date currentDate, Sort sort);

        @Query("SELECT d.hocVien FROM DangKyThi d WHERE d.lichThi.maLichThi = :maLichThi")
        List<HocVien> findHocVienByMaLichThi(@Param("maLichThi") Long maLichThi);
    DangKyThi findByKyThi_MaKyThiAndLichThi_MaLichThiAndHocVien_MaTaiKhoan(Long maK, Long maL, Long maH);
    @Query("SELECT COUNT(d) FROM DangKyThi d WHERE d.lichThi.maLichThi = :maLichThi")
    int countByLichThiId(@Param("maLichThi") Long maLichThi);
    @Transactional
    @Modifying
    @Query("DELETE FROM DangKyThi d WHERE d.trangThaiDangKyThi = 'Chua_Duyet' AND d.kyThi.hanDangKy = 'Het_Han'")
    void deleteAllChuaDuyetAndHetHan();
    boolean existsByHocVien_MaTaiKhoan(Long maTk);
}
