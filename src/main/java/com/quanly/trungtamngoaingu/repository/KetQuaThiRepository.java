package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.KetQuaThi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KetQuaThiRepository extends JpaRepository<KetQuaThi, Long> {
    KetQuaThi findByDangKyThi_MaDangKyThi(Long ten);
}
