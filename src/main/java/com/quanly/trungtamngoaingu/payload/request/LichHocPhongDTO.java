package com.quanly.trungtamngoaingu.payload.request;

import com.quanly.trungtamngoaingu.entity.LichHoc;
import com.quanly.trungtamngoaingu.entity.Phong;

public class LichHocPhongDTO {
    private LichHoc lichHoc;
    private Phong phong;

    public LichHocPhongDTO(LichHoc lichHoc, Phong phong) {
        this.lichHoc = lichHoc;
        this.phong = phong;
    }

    public LichHoc getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(LichHoc lichHoc) {
        this.lichHoc = lichHoc;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }
}
