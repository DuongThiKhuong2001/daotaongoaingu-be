package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.BacChungChi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BacChungChiRepository extends JpaRepository<BacChungChi, Long> {
    //dựa vào điểm tổng để lấy bậc chứng chỉ tương ứng
    @Query("SELECT b FROM BacChungChi b WHERE b.chungChi.maChungChi = :maChungChi AND :diemTong >= b.diemToiThieu AND :diemTong <= b.diemToiDa")
    Optional<BacChungChi> findBacChungChiByMaChungChiAndDiemTong(@Param("maChungChi") Long maChungChi, @Param("diemTong") Float diemTong);

    List<BacChungChi> findByChungChi_MaChungChi(Long ma);
}
