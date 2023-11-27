package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.KyThi;
import com.quanly.trungtamngoaingu.entity.LichThi;
import com.quanly.trungtamngoaingu.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LichThiRepository extends JpaRepository<LichThi, Long> {
    List<LichThi> findByNgayThiAndPhongAndCaThi(Date ngayThi, Phong phong, LichThi.CaThi caThi);
    void deleteAllByKyThi(KyThi kyThi);
    LichThi findFirstByKyThiOrderByNgayThi(KyThi kyThi);
    List<LichThi> findByKyThi(KyThi kyThi);

    //kiểm tra có lịch thi đã tồn tại với cùng ngày thi và ca thi hay chưa
    @Query("SELECT COUNT(l) FROM LichThi l WHERE l.phong.maPhong = :maPhong AND l.ngayThi = :ngayThi AND l.caThi = :caThi")
    int countByPhongAndNgayThiAndCaThi(@Param("maPhong") Long maPhong, @Param("ngayThi") Date ngayThi, @Param("caThi") LichThi.CaThi caThi);
}
