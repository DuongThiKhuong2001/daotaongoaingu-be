package com.quanly.trungtamngoaingu.repository;
import com.quanly.trungtamngoaingu.entity.TaiLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiLieuRepository extends JpaRepository<TaiLieu, Long> {
    List<TaiLieu> findAllByLoaiLop_MaLoaiLop(Long ma);
}
