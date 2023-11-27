package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.LichHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LichHocRepository extends JpaRepository<LichHoc, Long> {
    boolean existsByKiHieu(String ki);

}
