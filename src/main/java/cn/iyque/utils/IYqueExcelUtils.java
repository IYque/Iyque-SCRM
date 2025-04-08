package cn.iyque.utils;

import com.alibaba.excel.EasyExcel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class IYqueExcelUtils<T> {

    public  static <T> void export(HttpServletResponse response, Class<T> clazz, List<T> datas, String fileName){

        try {

            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileEName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileEName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), clazz).sheet(fileName).doWrite(datas);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
