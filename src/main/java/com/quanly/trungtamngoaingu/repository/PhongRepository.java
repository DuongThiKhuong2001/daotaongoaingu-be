package com.quanly.trungtamngoaingu.repository;
import com.quanly.trungtamngoaingu.entity.LichThi;
import com.quanly.trungtamngoaingu.entity.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Long> {
    @Query("SELECT p FROM Phong p WHERE p.loaiPhong = 'Hoc' AND NOT EXISTS (" +
            "SELECT l FROM LopHoc l JOIN l.khoaHoc k JOIN l.lichHoc lh WHERE " +
            "l.phong.maPhong = p.maPhong AND " +
            "lh.kiHieu = :kiHieu AND " +
            "(k.ngayBatDau BETWEEN :thoiGianBatDau AND :thoiGianKetThuc OR " +
            "k.ngayKetThuc BETWEEN :thoiGianBatDau AND :thoiGianKetThuc))")
    List<Phong> findAvailablePhongHocByLichHocAndKhoaHoc(Date thoiGianBatDau, Date thoiGianKetThuc, String kiHieu);
    @Query("SELECT CASE WHEN (COUNT(p) > 0) THEN TRUE ELSE FALSE END FROM Phong p WHERE p.maPhong = :maPhongHoc AND p.loaiPhong = 'Hoc' AND NOT EXISTS (" +
            "SELECT l FROM LopHoc l JOIN l.khoaHoc k JOIN l.lichHoc lh WHERE " +
            "l.phong.maPhong = p.maPhong AND " +
            "lh.kiHieu = :kiHieu AND " +
            "(k.ngayBatDau BETWEEN :thoiGianBatDau AND :thoiGianKetThuc OR " +
            "k.ngayKetThuc BETWEEN :thoiGianBatDau AND :thoiGianKetThuc))")
    boolean isPhongHocAvailable(Long maPhongHoc, Date thoiGianBatDau, Date thoiGianKetThuc, String kiHieu);
    Phong findByKiHieu(String ki);
    List<Phong> findAllByLoaiPhong(Phong.LoaiPhong phong);
    //lấy danh sách phòng hợp lệ
    @Query("SELECT p FROM Phong p WHERE p.loaiPhong = 'Thi' AND NOT EXISTS (SELECT 1 FROM LichThi l WHERE l.phong.maPhong = p.maPhong AND l.ngayThi = :ngayThi AND l.caThi = :caThi)")
    List<Phong> findUnusedExamRoomsByDateAndCaThi(@Param("ngayThi") Date ngayThi, @Param("caThi") LichThi.CaThi caThi);

}
