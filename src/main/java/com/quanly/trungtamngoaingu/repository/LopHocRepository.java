package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.GiaoVien;
import com.quanly.trungtamngoaingu.entity.LichHoc;
import com.quanly.trungtamngoaingu.entity.LopHoc;
import com.quanly.trungtamngoaingu.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LopHocRepository extends JpaRepository<LopHoc, Long>, JpaSpecificationExecutor<LopHoc> {
    List<LopHoc> findByKhoaHoc_MaKhoaHoc(Long maKhoaHoc);
    List<LopHoc> findByGiaoVien(GiaoVien gv);
    List<LopHoc> findByGiaoVien_TaiKhoan_TenDangNhap(String gv);
    boolean existsByLichHoc_MaLichHoc(Long maLh);
    boolean existsByTenLop(String ten);
    @Query("SELECT lh.lichHoc FROM LopHoc lh WHERE lh.giaoVien.maTaiKhoan = :maTaiKhoan")
    List<LichHoc> findLichHocByGiaoVien(@Param("maTaiKhoan") Long maTaiKhoan);
    List<LopHoc> findByLichHocAndPhongAndKhoaHoc_NgayBatDauBeforeAndKhoaHoc_NgayKetThucAfter(
            LichHoc lichHoc, Phong phong, Date ngayKetThuc, Date ngayBatDau);
    List<LopHoc> findByLichHocAndKhoaHoc_NgayBatDauBeforeAndKhoaHoc_NgayKetThucAfterAndGiaoVien(
            LichHoc lichHoc, Date ngayKetThuc, Date ngayBatDau, GiaoVien giaoVien);
    @Query("SELECT l FROM LopHoc l JOIN l.hocViens hv WHERE l.khoaHoc.maKhoaHoc = :maKhoaHoc AND hv.maTaiKhoan = :maHocVien")
    LopHoc findUniqueLopHocByMaKhoaHocAndMaHocVien(@Param("maKhoaHoc") Long maKhoaHoc, @Param("maHocVien") Long maHocVien);
}