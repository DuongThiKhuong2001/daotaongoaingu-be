package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.ChungChi;
import com.quanly.trungtamngoaingu.entity.KhoaHoc;
import com.quanly.trungtamngoaingu.entity.KyThi;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KyThiRepository extends JpaRepository<KyThi, Long>, JpaSpecificationExecutor<KyThi> {
    List<KyThi> findByChungChi_MaChungChi(Long chungChi, Sort sort);
    List<KyThi> findAll(Sort sort);
    List<KyThi> findByThangThiAndNamThi(Integer thangThi, Integer namThi, Sort sort);
    @Query("SELECT DISTINCT kt.namThi FROM KyThi kt ORDER BY kt.namThi ASC")
    List<Integer> findDistinctAndOrderByNamThi();
    List<KyThi> findByHanDangKy(String hanDangKy, Sort sort);
    @Query("SELECT kt FROM KyThi kt JOIN kt.giaoViens gv WHERE gv.maTaiKhoan = :maTaiKhoan")
    List<KyThi> findAllByGiaoVien(Long maTaiKhoan);
    List<KyThi> findByHanDangKyAndChungChi_MaChungChi(String hanDangKy, Long maChungChi, Sort sort);
    List<KyThi> findByHanDangKyAndThangThiAndNamThi(String hanDangKy, Integer thangThi, Integer namThi, Sort sort);
    boolean existsByThangThiAndNamThiAndChungChi(Integer thangThi, Integer namThi, ChungChi chungChi);
}