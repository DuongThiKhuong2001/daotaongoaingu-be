package com.quanly.trungtamngoaingu;

import com.quanly.trungtamngoaingu.entity.LichHoc;
import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import com.quanly.trungtamngoaingu.entity.VaiTro;
import com.quanly.trungtamngoaingu.repository.LichHocRepository;
import com.quanly.trungtamngoaingu.repository.LopHocRepository;
import com.quanly.trungtamngoaingu.repository.TaiKhoanRepository;
import com.quanly.trungtamngoaingu.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TrungtamngoainguApplication implements CommandLineRunner {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private VaiTroRepository vaiTroRepository;
    @Autowired
    private LichHocRepository lichHocRepository;
    @Autowired
    PasswordEncoder encoder;
    public static void main(String[] args) {
        SpringApplication.run(TrungtamngoainguApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        if (lichHocRepository.count() == 0) {
            // Define the kiHieu values and corresponding moTa descriptions
            String[] kiHieus = {"CASANG246", "CACHIEU246", "CATOI246", "CASANG357", "CACHIEU357", "CATOI357"};
            String[] moTas = {
                    "Buổi sáng thứ 2 4 và 6 của tuần", "Buổi chiều thứ 2 4 và 6 của tuần", "Buổi tối thứ 2 4 và 6 của tuần",
                    "Buổi sáng thứ 3 5 và 7 của tuần", "Buổi chiều thứ 3 5 và 7 của tuần", "Buổi tối thứ 3 5 và 7 của tuần"
            };

            for (int i = 0; i < kiHieus.length; i++) {
                String kiHieu = kiHieus[i];
                String moTa = moTas[i];

                LichHoc lichHoc = new LichHoc();
                lichHoc.setKiHieu(kiHieu);
                lichHoc.setMoTa(moTa);

                lichHocRepository.save(lichHoc);
            }
        }
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.QuanTriVien)) {
            // Tạo tài khoản mới với quyền là ADMIN
            TaiKhoan adminAccount = new TaiKhoan();
            adminAccount.setHoTen("Admin");
            adminAccount.setTenDangNhap("admin");
            adminAccount.setMatKhau(encoder.encode("abc123"));
            adminAccount.setEmail("admin@example.com");
            adminAccount.setQuyen(TaiKhoan.Quyen.QuanTriVien);
            adminAccount.setTrangThai(TaiKhoan.TrangThai.Mo);
            adminAccount.setGioiTinh(TaiKhoan.GioiTinh.Nam);
            taiKhoanRepository.save(adminAccount);
        }
        long totalVaiTro = vaiTroRepository.count();

        if (totalVaiTro == 0) {
            // Tạo danh sách các tên vai trò
            List<String> tenVaiTroList = Arrays.asList(
                    "QL-KH",
                    "QL-KT",
                    "QL-DKT",
                    "QL-DKKH",
                    "QL-TL"
            );
            List<String> moTas = Arrays.asList(
                    "Quản lý khóa học",
                    "Quản lý kỳ thi",
                    "Quản lý đăng ký thi",
                    "Quản lý đăng ký khóa học",
                    "Quản lý tài liệu"
            );
            for (int i = 0; i < tenVaiTroList.size(); i++) {
                String tenVaiTro = tenVaiTroList.get(i);
                String moTa = moTas.get(i);

                VaiTro vaiTro = new VaiTro();
                vaiTro.setTenVaiTro(tenVaiTro);
                vaiTro.setMoTa(moTa);

                vaiTroRepository.save(vaiTro);
            }
        }
    }
}
