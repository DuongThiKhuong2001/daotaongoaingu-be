package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.ThongBao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    List<ThongBao> findByTaiKhoan_MaTaiKhoanAndTrangThai(Long maTaiKhoan, ThongBao.TrangThai trangThai);

    @Query("SELECT tb FROM ThongBao tb WHERE tb.taiKhoan.maTaiKhoan = :maTk ORDER BY tb.ngayCapNhat DESC")
    List<ThongBao> findByTaiKhoan_MaTaiKhoan(@Param("maTk") Long maTk);
    @Query("SELECT COUNT(tb) FROM ThongBao tb WHERE tb.taiKhoan.maTaiKhoan = :maTaiKhoan AND tb.trangThai = 'ChuaDoc'")
    Long countChuaDocByMaTaiKhoan(Long maTaiKhoan);
    @Transactional
    void deleteAllByTaiKhoan_MaTaiKhoanAndTrangThai(Long maTk, ThongBao.TrangThai trangThai);
}
