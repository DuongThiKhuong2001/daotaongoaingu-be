package com.quanly.trungtamngoaingu.controller;

import com.quanly.trungtamngoaingu.entity.*;
import com.quanly.trungtamngoaingu.payload.request.KetQuaThiRequest;
import com.quanly.trungtamngoaingu.payload.response.MessageResponse;
import com.quanly.trungtamngoaingu.repository.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/ket-qua-thi")
public class KetQuaThiController {

    @Autowired
    private KetQuaThiRepository ketQuaThiRepository;
    @Autowired
    private DangKyThiRepository dangKyThiRepository;
    @Autowired
    private BacChungChiRepository bacChungChiRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private HocVienRepository hocVienRepository;
    @Autowired
    private LichThiRepository lichThiRepository;
    @PostMapping("/them")
    public ResponseEntity<?> themKetQuaThi(@RequestBody KetQuaThiRequest request) {
        try {
            Optional<DangKyThi> dangKyThiOptional = dangKyThiRepository.findById(request.getMaDangKyThi());

            if (dangKyThiOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Đăng ký thi không tồn tại"), HttpStatus.NOT_FOUND);
            }
            //Ko thể lên điểm khi kì thi chưa thi
            if (dangKyThiOptional.get().getTrangThaiDangKyThi() == DangKyThi.TrangThaiDangKyThi.Da_Len_Diem) {
                return new ResponseEntity<>(new MessageResponse("dalendiem"), HttpStatus.OK);
            }
            KetQuaThi ketQuaThi = new KetQuaThi();
            ketQuaThi.setDangKyThi(dangKyThiOptional.get());
            // Tính điểm trung bình từ các điểm kỹ năng
            int count = 0;
            float totalScore = 0;

            if (request.getDiemNghe() != null) {
                totalScore += request.getDiemNghe();
                ketQuaThi.setDiemNghe(request.getDiemNghe());
                count++;
            }
            if (request.getDiemNoi() != null) {
                totalScore += request.getDiemNoi();
                ketQuaThi.setDiemNoi(request.getDiemNoi());
                count++;
            }
            if (request.getDiemDoc() != null) {
                totalScore += request.getDiemDoc();
                ketQuaThi.setDiemDoc(request.getDiemDoc());
                count++;
            }
            if (request.getDiemViet() != null) {
                totalScore += request.getDiemViet();
                ketQuaThi.setDiemViet(request.getDiemViet());
                count++;
            }

            float averageScore = count > 0 ? totalScore / count : 0;

            // Làm tròn điểm trung bình
            float roundedScore = (float) (Math.round(averageScore * 2) / 2.0);
            if (roundedScore - averageScore >= 0.5) {
                roundedScore = Math.round(averageScore);
            }

            ketQuaThi.setDiemTong(roundedScore);

            // So sánh điểm tổng với điểm tối thiểu và tối đa trong BacChungChi để gán bậc chứng chỉ
            // Giả sử bạn có một service hoặc repository để lấy BacChungChi dựa trên điểm
            Optional<BacChungChi> bacChungChi =
                    bacChungChiRepository.findBacChungChiByMaChungChiAndDiemTong(dangKyThiOptional.get().getKyThi().getChungChi().getMaChungChi(), roundedScore);
            if (bacChungChi.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Không có bậc chứng chỉ"),
                        HttpStatus.NOT_FOUND);

            }
            ketQuaThi.setBacChungChi(bacChungChi.get());
            // Lưu kết quả thi
            ketQuaThiRepository.save(ketQuaThi);
            dangKyThiOptional.get().setTrangThaiDangKyThi(DangKyThi.TrangThaiDangKyThi.Da_Len_Diem);
            dangKyThiRepository.save(dangKyThiOptional.get());
            String tieuDe = "Kết quả thi";
            String nDung = "Bạn đã có kết quả thi cho kỳ thi " + ketQuaThi.getDangKyThi().getKyThi().getChungChi().getTenChungChi() + " tháng "
                    + ketQuaThi.getDangKyThi().getKyThi().getThangThi() + " năm " + ketQuaThi.getDangKyThi().getKyThi().getNamThi();
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    dangKyThiOptional.get().getHocVien().getTaiKhoan(),
                    tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
            return new ResponseEntity<>(new MessageResponse("Thêm kết quả thi thành công"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi thêm kết quả thi"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/danh-sach")
    public ResponseEntity<List<KetQuaThi>> layTatCaKetQuaThi() {
        try {
            List<KetQuaThi> ketQuas = ketQuaThiRepository.findAll();
            return new ResponseEntity<>(ketQuas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lay/{maKetQuaThi}")
    public ResponseEntity<KetQuaThi> layKetQuaThiTheoId(@PathVariable Long maKetQuaThi) {
        Optional<KetQuaThi> ketQuaData = ketQuaThiRepository.findById(maKetQuaThi);

        if (ketQuaData.isPresent()) {
            return new ResponseEntity<>(ketQuaData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/sua/{maKetQuaThi}")
    public ResponseEntity<?> suaKetQuaThi(@PathVariable Long maKetQuaThi, @RequestBody KetQuaThiRequest request) {
        try {
            Optional<KetQuaThi> ketQuaThiOptional = ketQuaThiRepository.findById(maKetQuaThi);

            if (ketQuaThiOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Kết quả thi không tồn tại"), HttpStatus.NOT_FOUND);
            }

            KetQuaThi ketQuaThi = ketQuaThiOptional.get();

            // Tính lại điểm trung bình từ các điểm kỹ năng
            int count = 0;
            float totalScore = 0;

            if (request.getDiemNghe() != null) {
                totalScore += request.getDiemNghe();
                ketQuaThi.setDiemNghe(request.getDiemNghe());
                count++;
            }
            if (request.getDiemNoi() != null) {
                totalScore += request.getDiemNoi();
                ketQuaThi.setDiemNoi(request.getDiemNoi());
                count++;
            }
            if (request.getDiemDoc() != null) {
                totalScore += request.getDiemDoc();
                ketQuaThi.setDiemDoc(request.getDiemDoc());
                count++;
            }
            if (request.getDiemViet() != null) {
                totalScore += request.getDiemViet();
                ketQuaThi.setDiemViet(request.getDiemViet());
                count++;
            }

            float averageScore = count > 0 ? totalScore / count : 0;

            // Làm tròn lại điểm trung bình
            float roundedScore = (float) (Math.round(averageScore * 2) / 2.0);
            if (roundedScore - averageScore >= 0.5) {
                roundedScore = Math.round(averageScore);
            }

            ketQuaThi.setDiemTong(roundedScore);

            // So sánh điểm tổng với điểm tối thiểu và tối đa trong BacChungChi để cập nhật bậc chứng chỉ
            // Giả sử bạn có một service hoặc repository để lấy BacChungChi dựa trên điểm
            Optional<BacChungChi> bacChungChi = bacChungChiRepository.findBacChungChiByMaChungChiAndDiemTong(ketQuaThi.getDangKyThi().getKyThi().getChungChi().getMaChungChi(), roundedScore);
            if (bacChungChi.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Không có bậc chứng chỉ"), HttpStatus.NOT_FOUND);
            }

            ketQuaThi.setBacChungChi(bacChungChi.get());

            // Lưu cập nhật kết quả thi
            ketQuaThiRepository.save(ketQuaThi);

            return new ResponseEntity<>(new MessageResponse("Cập nhật kết quả thi thành công"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi cập nhật kết quả thi"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/xoa/{maKetQuaThi}")
    public ResponseEntity<?> xoaKetQuaThi(@PathVariable Long maKetQuaThi) {
        try {
            ketQuaThiRepository.deleteById(maKetQuaThi);
            return new ResponseEntity<>(new MessageResponse("Kết quả thi đã được xóa"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/lay-ket-qua-theo-ma-dang-ky")
    public ResponseEntity<KetQuaThi> layKetQuaThiTheoMaDangKy(@RequestParam Long maDangKy) {
        KetQuaThi ketQuaThiList = ketQuaThiRepository.findByDangKyThi_MaDangKyThi(maDangKy);
        return ResponseEntity.ok(ketQuaThiList);
    }
    private boolean isTenDangNhapExists(String tenDangNhap, List<HocVien> hocViens) {
        for (HocVien hocVien : hocViens) {
            if (hocVien.getTaiKhoan().getTenDangNhap().equals(tenDangNhap)) {
                return true;
            }
        }
        return false;
    }
    //nhập điểm bằng file excel:
    //nếu có lỗi sai mã đăng ký sai học viên, có điểm am sẽ hủy tất cả nhập điểm. sữa lại đúng mới nhập được
    @Transactional
    @PostMapping("/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file,
                                         @RequestParam("maLichThi") Long maLichThi) throws IOException {
        Optional<LichThi> lichThiOptional = lichThiRepository.findById(maLichThi);
        if (lichThiOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không có lịch thi"), HttpStatus.NOT_FOUND);
        }
        List<HocVien> hocViens = dangKyThiRepository.findHocVienByMaLichThi(maLichThi);
        // Kiểm tra nếu tệp Excel không tồn tại
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Tệp Excel không hợp lệ"), HttpStatus.BAD_REQUEST);
        }

        // Đọc tệp Excel
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
        int startRow = 2; // Điều chỉnh dòng bắt đầu đọc dữ liệu
        // Đọc dữ liệu từ tệp Excel và lưu danh sách tenDangNhap vào một danh sách riêng
        Set<String> tenDangNhapSet = new HashSet<>();
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Cell cell1 = row.getCell(2);
            String tenDangNhap = getStringValueFromCell(cell1);
            tenDangNhapSet.add(tenDangNhap);
            Cell cell2 = row.getCell(3);
            Long maDangKy = getLongValueFromCell(cell2);

            if (dangKyThiRepository.findById(maDangKy).isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("saima" + (i-1)), HttpStatus.OK);
            }
            Cell cell3 = row.getCell(4);
            String diemNghe = getStringValueFromCell(cell3);

            Cell cell4 = row.getCell(5);
            String diemDoc = getStringValueFromCell(cell4);

            Cell cell5 = row.getCell(6);
            String diemViet = getStringValueFromCell(cell5);

            Cell cell6 = row.getCell(7);
            String diemNoi = getStringValueFromCell(cell6);
            if (isNegative(diemNghe) || isNegative(diemDoc) || isNegative(diemViet) || isNegative(diemNoi)) {
                return new ResponseEntity<>(new MessageResponse("codiemam" + (i-1)), HttpStatus.OK);
            }
        }

        for (String tenDangNhap : tenDangNhapSet) {
            HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

            if (hocVien == null || !isTenDangNhapExists(tenDangNhap, hocViens)) {
                return new ResponseEntity<>(new MessageResponse("saihv"), HttpStatus.OK);
            }

        }
        // Duyệt qua từng dòng của tệp Excel, bắt đầu từ dòng startRow
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            // Đọc dữ liệu từ cột cụ thể trong dòng
            Cell cell1 = row.getCell(2);
            String tenDangNhap = getStringValueFromCell(cell1);

            Cell cell2 = row.getCell(3);
            Long maDangKy = getLongValueFromCell(cell2);

            Cell cell3 = row.getCell(4);
            String diemNghe = getStringValueFromCell(cell3);

            Cell cell4 = row.getCell(5);
            String diemDoc = getStringValueFromCell(cell4);

            Cell cell5 = row.getCell(6);
            String diemViet = getStringValueFromCell(cell5);

            Cell cell6 = row.getCell(7);
            String diemNoi = getStringValueFromCell(cell6);

            // Tìm học viên dựa trên 'tenDangNhap'
            HocVien hocVien = hocVienRepository.findByTaiKhoan_TenDangNhap(tenDangNhap);

            // Kiểm tra nếu học viên không tồn tại
            if (hocVien == null) {
                return new ResponseEntity<>(new MessageResponse("Học viên với tên đăng nhập '" + tenDangNhap + "' không tồn tại"), HttpStatus.NOT_FOUND);
            }

            Optional<DangKyThi> dangKyThiOptional = dangKyThiRepository.findById(maDangKy);


            //Ko thể lên điểm khi kì thi chưa thi
//            if (dangKyThiOptional.get().getTrangThaiDangKyThi() == DangKyThi.TrangThaiDangKyThi.Da_Len_Diem) {
//                return new ResponseEntity<>(new MessageResponse("saids"), HttpStatus.OK);
//            }
            // tránh bị lặp
            KetQuaThi ketQuaThi = null;
             ketQuaThi = ketQuaThiRepository.findByDangKyThi_MaDangKyThi(maDangKy);
            if(ketQuaThi == null){
                 ketQuaThi = new KetQuaThi();
            }

            ketQuaThi.setDangKyThi(dangKyThiOptional.get());
            // Tính điểm trung bình từ các điểm kỹ năng
            int count = 0;
            float totalScore = 0;

            if (diemNghe == null) {
                return new ResponseEntity<>(new MessageResponse("Đăng ký thi không tồn tại"), HttpStatus.NOT_FOUND);

            }
            if (diemNghe != null) {
                totalScore += Float.parseFloat(diemNghe);
                ketQuaThi.setDiemNghe(Float.parseFloat(diemNghe));
                count++;
            }
            if (diemNoi != null) {
                totalScore += Float.parseFloat(diemNoi);
                ketQuaThi.setDiemNoi(Float.parseFloat(diemNoi));
                count++;
            }
            if (diemDoc != null) {
                totalScore += Float.parseFloat(diemDoc);
                ketQuaThi.setDiemDoc(Float.parseFloat(diemDoc));
                count++;
            }
            if (diemViet != null) {
                totalScore += Float.parseFloat(diemViet);
                ketQuaThi.setDiemViet(Float.parseFloat(diemViet));
                count++;
            }

            float averageScore = count > 0 ? totalScore / count : 0;

            // Làm tròn điểm trung bình
            float roundedScore = (float) (Math.round(averageScore * 2) / 2.0);
            if (roundedScore - averageScore >= 0.5) {
                roundedScore = Math.round(averageScore);
            }

            ketQuaThi.setDiemTong(roundedScore);

            // So sánh điểm tổng với điểm tối thiểu và tối đa trong BacChungChi để gán bậc chứng chỉ
            // Giả sử bạn có một service hoặc repository để lấy BacChungChi dựa trên điểm
            Optional<BacChungChi> bacChungChi =
                    bacChungChiRepository.findBacChungChiByMaChungChiAndDiemTong(dangKyThiOptional.get().getKyThi().getChungChi().getMaChungChi(), roundedScore);
            if (bacChungChi.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Không có bậc chứng chỉ"),
                        HttpStatus.NOT_FOUND);

            }
            ketQuaThi.setBacChungChi(bacChungChi.get());
            // Lưu kết quả thi
            ketQuaThiRepository.save(ketQuaThi);
            dangKyThiOptional.get().setTrangThaiDangKyThi(DangKyThi.TrangThaiDangKyThi.Da_Len_Diem);
            dangKyThiRepository.save(dangKyThiOptional.get());
            String tieuDe = "Kết quả thi";
            String nDung = "Bạn đã có kết quả thi cho kỳ thi " + ketQuaThi.getDangKyThi().getKyThi().getChungChi().getTenChungChi() + " tháng "
                    + ketQuaThi.getDangKyThi().getKyThi().getThangThi() + " năm " + ketQuaThi.getDangKyThi().getKyThi().getNamThi();
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    dangKyThiOptional.get().getHocVien().getTaiKhoan(),
                    tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
        }

        // Đóng workbook khi hoàn thành
        workbook.close();

        // Trả kết quả thành công hoặc thông báo lỗi nếu cần
        return new ResponseEntity<>(new MessageResponse("Nhập dữ liệu từ Excel thành công"), HttpStatus.OK);

    }
    private boolean isNegative(String scoreStr) {
        try {
            float score = Float.parseFloat(scoreStr);
            return score < 0;
        } catch (NumberFormatException e) {
            return false; // Hoặc xử lý lỗi định dạng số tại đây
        }
    }
    private String getStringValueFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Chuyển giá trị số thành chuỗi
                return String.valueOf(cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private Long getLongValueFromCell(Cell cell) {
        if (cell == null) {
            return (long) 0;
        }
        switch (cell.getCellType()) {
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return (long) 0;
                }
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            default:
                return (long) 0;
        }
    }
}
