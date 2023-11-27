package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.*;
import com.quanly.trungtamngoaingu.payload.response.JwtResponse;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.*;
import com.quanly.trungtamngoaingu.sercurity.Helpers;
import com.quanly.trungtamngoaingu.sercurity.jwt.JwtUtils;
import com.quanly.trungtamngoaingu.service.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tai-khoan")
public class TaiKhoanController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private GiaoVienRepository giaoVienRepository;
    @Autowired
    private HocVienRepository hocVienRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private Helpers helpers;

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String taiKhoan = loginRequest.getTaiKhoan();
        // Kiểm tra thông tin
        Optional<TaiKhoan> taiKhoanInfo = taiKhoanRepository.findByTenDangNhap(taiKhoan);
        if (!taiKhoanRepository.existsByTenDangNhap(taiKhoan)) {
            return new ResponseEntity<>(new MessageResponse("account-warning"), HttpStatus.OK);
        }
        if (taiKhoanInfo.isPresent()) {
            if (!encoder.matches(loginRequest.getMatKhau(), taiKhoanInfo.get().getMatKhau())) {
                return new ResponseEntity<>(new MessageResponse("account-warning"), HttpStatus.OK);
            } else if (taiKhoanInfo.get().getTrangThai() == TaiKhoan.TrangThai.Khoa) {
                return new ResponseEntity<>(new MessageResponse("account-block"), HttpStatus.OK);
            }
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getTaiKhoan(), loginRequest.getMatKhau()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        JwtResponse response = new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthority().getAuthority());
        return ResponseEntity.ok(response);
    }
    // có kiểm tra email và user-name ton tại hay không
    //Trong Spring Data JPA, bạn có thể sử dụng quy tắc đặt tên để tạo ra các phương thức truy vấn
    // dựa trên tên mà không cần viết câu truy vấn SQL. (ví dụ existsByTenDangNhap: kiểm
    //tra có tồn tại 1 tên đăng nhập được điền vào ko)

    @PostMapping("/them-moi")
    @Transactional
    public ResponseEntity<?> createAccount(@RequestBody TaiKhoanRequest taiKhoanRequest) {
        if (taiKhoanRepository.existsByTenDangNhap(taiKhoanRequest.getTenDangNhap())) {
            return new ResponseEntity<>(new MessageResponse("username-exist"), HttpStatus.OK);
        }

        if (taiKhoanRepository.existsByEmail(taiKhoanRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("email-exist"), HttpStatus.OK);
        }
        // Tạo tài khoản và thông tin tương ứng dựa trên quyền và loại người dùng
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanRequest.getTenDangNhap(),
                encoder.encode(taiKhoanRequest.getMatKhau()),taiKhoanRequest.getHoTen(),taiKhoanRequest.getEmail(),
                taiKhoanRequest.getSoDienThoai(),taiKhoanRequest.getDiaChi(),taiKhoanRequest.getGioiTinh(),taiKhoanRequest.getNgaySinh(),
                taiKhoanRequest.getQuyen());
        // Thiết lập thông tin tài khoản từ request
        taiKhoanRepository.save(taiKhoan);
        // Kiểm tra quyền để xác định loại người dùng
        if (taiKhoanRequest.getQuyen() == TaiKhoan.Quyen.GiaoVien) {
            //tạo mới đối tượng giáo viên
            GiaoVien giaoVien = new GiaoVien();
            // Thiết lập thông tin giáo viên từ request
            giaoVien.setTaiKhoan(taiKhoan);
            giaoVien.setTrinhDo(taiKhoanRequest.getTrinhDo());
            //lưu thông tin lại
            giaoVienRepository.save(giaoVien);
        }
        if (taiKhoanRequest.getQuyen() == TaiKhoan.Quyen.NhanVien) {
            NhanVien nhanVien = new NhanVien();
            nhanVien.setTaiKhoan(taiKhoan);
            nhanVienRepository.save(nhanVien);
        }
        if (taiKhoanRequest.getQuyen() == TaiKhoan.Quyen.HocVien) {
            HocVien hocVien = new HocVien();
            hocVien.setTaiKhoan(taiKhoan);
            hocVien.setLop(taiKhoanRequest.getLop());
            hocVien.setSoDTNguoiThan(taiKhoanRequest.getSoDTNguoiThan());
            hocVien.setTruongHoc(taiKhoanRequest.getTruongHoc());
            hocVienRepository.save(hocVien);
        }
        // Trả về thông báo thành công
        return ResponseEntity.ok(new MessageResponse("Thêm tài khoản thành công!"));
    }

    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllUsersByRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maTaiKhoan") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam TaiKhoan.Quyen userRole
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);
        Specification<?> spec = null;

        if (!searchTerm.isEmpty()) {
            spec = (root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("taiKhoan").get("tenDangNhap"), pattern),
                        criteriaBuilder.like(root.get("taiKhoan").get("email"), pattern),
                        criteriaBuilder.like(root.get("taiKhoan").get("hoTen"), pattern)
                );
            };
        }
        if (spec == null) {
            spec = (root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true
            };
        }

        switch (userRole) {
            case GiaoVien:
                Page<GiaoVien> giaoVienPage = giaoVienRepository.findAll((Specification<GiaoVien>) spec, paging);
                return ResponseEntity.ok(giaoVienPage);
            case HocVien:
                Page<HocVien> hocVienPage = hocVienRepository.findAll((Specification<HocVien>) spec, paging);
                return ResponseEntity.ok(hocVienPage);
            case NhanVien:
                Page<NhanVien> nhanVienPage = nhanVienRepository.findAll((Specification<NhanVien>) spec, paging);
                return ResponseEntity.ok(nhanVienPage);
            default:
                return new ResponseEntity<>(new MessageResponse("Quyền không hợp lệ"), HttpStatus.BAD_REQUEST);

        }
    }
    //nhận về thông tin trạng thái và thông tin tenDangNhap để thay đổi
    //nếu là quản trị viên thì không the thay đổi
    @PutMapping("/cap-nhat-trang-thai")
    public ResponseEntity<?> updateStatus(
            @RequestParam(value = "status") TaiKhoan.TrangThai status,
            @RequestParam(value = "tenDangNhap") String tenDangNhap,
            HttpServletRequest httpServletRequest) {

        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap);
        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
        if (currentUser.getQuyen().equals(TaiKhoan.Quyen.QuanTriVien) && taiKhoan.isPresent()) {
            if (taiKhoan.get().getTrangThai().equals(status)) {
                return new ResponseEntity<>(new MessageResponse("NO_CHANGE"), HttpStatus.OK);
            }
            taiKhoan.get().setTrangThai(status);
            taiKhoanRepository.save(taiKhoan.get());
        } else {
            return new ResponseEntity<>(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new MessageResponse("OK"), HttpStatus.OK);
    }
    //su dụng de kiem tra token dang nhap(tranh truong hop thay doi token)
    @PostMapping("/kiem-tra-dang-nhap")
    public ResponseEntity<?> testLogin(@RequestBody TokenRequest token) {
        if (token.getToken() != null && !token.getToken().isEmpty()) {
            if (JwtUtils.testJwtToken(token.getToken())) {
                return ResponseEntity.ok(new MessageResponse("ok"));
            }
            return new ResponseEntity<>(new MessageResponse("error"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("empty"), HttpStatus.OK);
    }
    //lay thong tin chi tiết ứng với từng tài khoản guui yêu cầu
    @GetMapping("/lay-thong-tin-chi-tiet")
    public ResponseEntity<?> getUserDetails(HttpServletRequest httpServletRequest) {
        //hàm lấy thông tin tài khoản gửi đến api này
        TaiKhoan queriedUser= helpers.getCurrentUser(httpServletRequest);

        switch (queriedUser.getQuyen()) {
            case GiaoVien:
                GiaoVien giaoVien = giaoVienRepository.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("GiaoVien not found"));
                return ResponseEntity.ok(giaoVien);
            case HocVien:
                HocVien hocVien = hocVienRepository.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("HocVien not found"));
                return ResponseEntity.ok(hocVien);
            case NhanVien:
                NhanVien nhanVien = nhanVienRepository.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("NhanVien not found"));
                return ResponseEntity.ok(nhanVien);
            default:
                return new ResponseEntity<>(new MessageResponse("Quyền không hợp lệ"), HttpStatus.BAD_REQUEST);
        }
    }
    //có kiểm tra là mật khẩu cũ ko đúng va khong thay doi
    //nếu đoi mat khau thành công sẽ cấp lai jwt
    @PutMapping("/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(@Valid @RequestBody MatKhauMoiRequest matKhauMoiRequest,
                                        HttpServletRequest httpServletRequest) {
        TaiKhoan currentUser = helpers.getCurrentUser(httpServletRequest);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        if (!encoder.matches(matKhauMoiRequest.getMatKhauCu(), currentUser.getMatKhau())) {
            return new ResponseEntity<>(new MessageResponse("NOT_MATCH"), HttpStatus.NOT_FOUND);
        } else if (encoder.matches(matKhauMoiRequest.getMatKhauMoi(), currentUser.getMatKhau())) {
            return new ResponseEntity<>(new MessageResponse("NO_CHANGE"), HttpStatus.OK);
        } else {
            currentUser.setMatKhau(encoder.encode(matKhauMoiRequest.getMatKhauMoi()));
            taiKhoanRepository.save(currentUser);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            JwtResponse response = new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthority().getAuthority());
            return ResponseEntity.ok(response);
        }
    }
    //nhận về thông tin username và thông tin trong requestbody để cập nhật thông tin ungws với từng loại
    //tai khoan
    @PutMapping("/cap-nhat/{username}")
    @Transactional
    public ResponseEntity<?> updateAccount(@PathVariable String username, @RequestBody TaiKhoanRequest taiKhoanRequest) {
        Optional<TaiKhoan> existingAccount = taiKhoanRepository.findByTenDangNhap(username);

        if (existingAccount.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("account-notfound"), HttpStatus.OK);
        }

        TaiKhoan taiKhoan = existingAccount.get();
        // Cập nhật thông tin tài khoản từ request
        taiKhoan.setHoTen(taiKhoanRequest.getHoTen());
        taiKhoan.setEmail(taiKhoanRequest.getEmail());
        taiKhoan.setSoDienThoai(taiKhoanRequest.getSoDienThoai());
        taiKhoan.setDiaChi(taiKhoanRequest.getDiaChi());
        taiKhoan.setGioiTinh(taiKhoanRequest.getGioiTinh());
        taiKhoan.setNgaySinh(taiKhoanRequest.getNgaySinh());
        taiKhoan.setTrangThai(TaiKhoan.TrangThai.Mo);

        taiKhoanRepository.save(taiKhoan);

        // Kiểm tra và cập nhật thông tin riêng biệt dựa trên quyền của tài khoản
        // Kiểm tra quyền để xác định loại người dùng
        if (taiKhoan.getQuyen() == TaiKhoan.Quyen.GiaoVien) {
            GiaoVien giaoVien = giaoVienRepository.findByTaiKhoan_TenDangNhap(taiKhoan.getTenDangNhap());
            // Thiết lập thông tin giáo viên từ request
            giaoVien.setTaiKhoan(taiKhoan);
            giaoVien.setTrinhDo(taiKhoanRequest.getTrinhDo());
            giaoVienRepository.save(giaoVien);
            return new ResponseEntity<>(new MessageResponse("Success"), HttpStatus.OK);
        }
        if (taiKhoan.getQuyen() == TaiKhoan.Quyen.NhanVien) {
                NhanVien nhanVien = nhanVienRepository.findByTaiKhoan_TenDangNhap(taiKhoan.getTenDangNhap());
                nhanVien.setTaiKhoan(taiKhoan);
                nhanVienRepository.save(nhanVien);
            return new ResponseEntity<>(new MessageResponse("Success"), HttpStatus.OK);
        }
        if (taiKhoan.getQuyen() == TaiKhoan.Quyen.HocVien) {
            HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(taiKhoan.getTenDangNhap());
            hocVien.setTaiKhoan(taiKhoan);
            hocVien.setLop(taiKhoanRequest.getLop());
            hocVien.setSoDTNguoiThan(taiKhoanRequest.getSoDTNguoiThan());
            hocVien.setTruongHoc(taiKhoanRequest.getTruongHoc());
            hocVienRepository.save(hocVien);
            return new ResponseEntity<>(new MessageResponse("Success"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("No"), HttpStatus.OK);
    }

}
