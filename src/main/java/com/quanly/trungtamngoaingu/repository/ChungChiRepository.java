package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.ChungChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChungChiRepository extends JpaRepository<ChungChi, Long> {
}
