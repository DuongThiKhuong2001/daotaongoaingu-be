package com.quanly.trungtamngoaingu.payload.response;

public class NgayBuoiHocResponse {
    private String ngay;
    private String buoi;

    public NgayBuoiHocResponse(String ngay, String buoi) {
        this.ngay = ngay;
        this.buoi = buoi;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }
}
