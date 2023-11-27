package com.quanly.trungtamngoaingu.payload.request;

import com.quanly.trungtamngoaingu.entity.Phong;

public  class PhongHocRequest {
    private String tenPhong;
    private Integer sucChua;
    private String viTri;
    private String kiHieu;
    private Phong.LoaiPhong loaiPhong;
    public String getKiHieu() {
        return kiHieu;
    }

    public void setKiHieu(String kiHieu) {
        this.kiHieu = kiHieu;
    }

    public Phong.LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(Phong.LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

}

