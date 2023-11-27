package com.quanly.trungtamngoaingu.repository;

import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import com.quanly.trungtamngoaingu.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {
    VaiTro findByTenVaiTro(String vaitro);
}
