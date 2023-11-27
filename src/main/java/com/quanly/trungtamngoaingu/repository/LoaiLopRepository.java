package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.LoaiLop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiLopRepository extends JpaRepository<LoaiLop, Long> {
    boolean existsByTenLoaiLop(String ten);
}
