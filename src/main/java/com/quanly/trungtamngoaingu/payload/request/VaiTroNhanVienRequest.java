package com.quanly.trungtamngoaingu.payload.request;

import java.util.List;

public class VaiTroNhanVienRequest {
    private List<Long> vaiTroIds;

    public List<Long> getVaiTroIds() {
        return vaiTroIds;
    }

    public void setVaiTroIds(List<Long> vaiTroIds) {
        this.vaiTroIds = vaiTroIds;
    }

}
