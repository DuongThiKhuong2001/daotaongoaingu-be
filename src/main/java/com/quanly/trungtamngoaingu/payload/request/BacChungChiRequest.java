package com.quanly.trungtamngoaingu.payload.request;

public class BacChungChiRequest {

    private Long maChungChi;
    private String bac;
    private Float diemToiThieu;
    private Float diemToiDa;


    public Long getMaChungChi() {
        return maChungChi;
    }

    public void setMaChungChi(Long maChungChi) {
        this.maChungChi = maChungChi;
    }

    public String getBac() {
        return bac;
    }

    public void setBac(String bac) {
        this.bac = bac;
    }

    public Float getDiemToiThieu() {
        return diemToiThieu;
    }

    public void setDiemToiThieu(Float diemToiThieu) {
        this.diemToiThieu = diemToiThieu;
    }

    public Float getDiemToiDa() {
        return diemToiDa;
    }

    public void setDiemToiDa(Float diemToiDa) {
        this.diemToiDa = diemToiDa;
    }
}
