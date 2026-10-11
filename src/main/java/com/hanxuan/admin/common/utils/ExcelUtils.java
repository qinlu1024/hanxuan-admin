package com.hanxuan.admin.common.utils;

import cn.hutool.poi.excel.ExcelWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Excel 导出工具类
 *
 * @author hanxuan
 */
public class ExcelUtils {

    /**
     * 将 ExcelWriter 内容写入响应输出流（xlsx 格式）
     *
     * @param response HTTP 响应
     * @param fileName 导出文件名（不含扩展名）
     * @param writer   Hutool ExcelWriter
     */
    public static void writeToResponse(HttpServletResponse response, String fileName, ExcelWriter writer) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");
        try (OutputStream out = response.getOutputStream()) {
            writer.flush(out, true);
        }
    }
}
