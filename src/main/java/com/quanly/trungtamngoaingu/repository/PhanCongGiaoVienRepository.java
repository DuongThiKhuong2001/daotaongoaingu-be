package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.GiaoVien;
import com.quanly.trungtamngoaingu.entity.KyThi;
import com.quanly.trungtamngoaingu.entity.PhanCongGiaoVien;
import com.quanly.trungtamngoaingu.entity.LichThi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PhanCongGiaoVienRepository extends JpaRepository<PhanCongGiaoVien, Long> {
    boolean existsByLichThi_KyThi(KyThi kyThi);
    List<PhanCongGiaoVien> findByGiaoVien_TaiKhoan_TenDangNhapAndLoaiPhanCong(String tenDangNhap, PhanCongGiaoVien.LoaiPhanCong loaiPhanCong);
    List<PhanCongGiaoVien> findByGiaoVien_MaTaiKhoanAndLichThi_NgayThiAndLichThi_CaThiAndLoaiPhanCong(
            Long maGiaoVien,
            Date ngayThi,
            LichThi.CaThi caThi,
            PhanCongGiaoVien.LoaiPhanCong loaiPhanCong
    );
    @Query("SELECT pcgv.giaoVien FROM PhanCongGiaoVien pcgv WHERE pcgv.lichThi.ngayThi = :ngayThi AND pcgv.lichThi.caThi = :caThi AND pcgv.loaiPhanCong = :loaiPC ")
    List<GiaoVien> findGiaoViensAssignedOnSameDateAndShift(@Param("ngayThi") Date ngayThi, @Param("caThi") LichThi.CaThi caThi, @Param("loaiPC") PhanCongGiaoVien.LoaiPhanCong loaiPC);
    List<PhanCongGiaoVien> findByLichThi_MaLichThiAndLoaiPhanCong(Long maLichThi, PhanCongGiaoVien.LoaiPhanCong loaiPhanCong);
    @Query("SELECT pcgv.giaoVien FROM PhanCongGiaoVien pcgv WHERE pcgv.lichThi.maLichThi = :maLichThi AND pcgv.loaiPhanCong = :loaiPC ")
    List<GiaoVien> findGiaoViensByMaLichThiAndLoaiPhanCong(@Param("maLichThi") Long maLichThi, @Param("loaiPC") PhanCongGiaoVien.LoaiPhanCong loaiPhanCong);
    @Query("SELECT pcg.lichThi FROM PhanCongGiaoVien pcg WHERE pcg.giaoVien.maTaiKhoan = :maTaiKhoan AND pcg.loaiPhanCong = 'Len_Diem'")
    List<LichThi> findLichThiByGiaoVienAndLoaiPhanCong(@Param("maTaiKhoan") Long maTaiKhoan);
    @Query("SELECT pcg.lichThi FROM PhanCongGiaoVien pcg WHERE pcg.giaoVien.maTaiKhoan = :maTaiKhoan AND pcg.loaiPhanCong = 'Gac_Thi'")
    List<LichThi> findLichThiByGiaoVienAndLoaiPhanCongGT(@Param("maTaiKhoan") Long maTaiKhoan);

    List<PhanCongGiaoVien> findByGiaoVien_MaTaiKhoanAndLichThi_MaLichThi(Long matk, Long maLichThi);
}
