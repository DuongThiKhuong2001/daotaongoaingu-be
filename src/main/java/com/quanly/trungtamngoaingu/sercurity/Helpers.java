package com.quanly.trungtamngoaingu.sercurity;


import com.quanly.trungtamngoaingu.entity.TaiKhoan;
import com.quanly.trungtamngoaingu.repository.TaiKhoanRepository;
import com.quanly.trungtamngoaingu.sercurity.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.quanly.trungtamngoaingu.repository.TaiKhoanRepository;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class Helpers {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    //hàm hổ trợ lấy thông tin tài khoản hiện tại thực hiện gửi yêu cầu
    public TaiKhoan getCurrentUser(HttpServletRequest httpServletRequest) {
        String token = JwtUtils.resolveToken(httpServletRequest);
        if (token == null || !JwtUtils.validateJwtToken(token)) {
            return null;
        }

        Claims claims = JwtUtils.getClaimsFromToken(token);
        Long currentUserId = claims.get("id", Long.class);
        return taiKhoanRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    public static String createSlug(String string) {
        String[] search = {
                "(à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ)",
                "(è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ)",
                "(ì|í|ị|ỉ|ĩ)",
                "(ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ)",
                "(ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ)",
                "(ỳ|ý|ỵ|ỷ|ỹ)",
                "(đ)",
                "(À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ)",
                "(È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ)",
                "(Ì|Í|Ị|Ỉ|Ĩ)",
                "(Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ)",
                "(Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ)",
                "(Ỳ|Ý|Ỵ|Ỷ|Ỹ)",
                "(Đ)",
                "[^a-zA-Z0-9\\-_]"
        };

        String[] replace = {
                "a",
                "e",
                "i",
                "o",
                "u",
                "y",
                "d",
                "A",
                "E",
                "I",
                "O",
                "U",
                "Y",
                "D",
                "-"
        };

        String temp = string;

        for (int i = 0; i < search.length; i++) {
            temp = temp.replaceAll(search[i], replace[i]);
        }

        temp = temp.replaceAll("(-)+", " ");
        temp = temp.toLowerCase();

        // Remove diacritics using Normalizer
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        temp = temp.replaceAll("[^\\p{ASCII}]", "");

        return temp;
    }
}