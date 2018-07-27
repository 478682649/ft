package com.guazi.ft.common;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Excel工具类
 *
 * @author shichunyang
 */
public class ExcelUtil {

    /**
     * 对外输出excel
     *
     * @param out       输出流
     * @param sheetName sheet页名称
     * @param columnChs 列名(中文)
     * @param columnEns 列名(英文)
     * @param dataList  表格中数据
     */
    public static void createExcel(
            OutputStream out,

            String sheetName,

            String[] columnChs,

            String[] columnEns,

            List<Map<String, Object>> dataList
    ) throws Exception {

        WritableWorkbook workbook = Workbook.createWorkbook(out);
        // 创建sheet1
        WritableSheet sheet0 = workbook.createSheet(sheetName, 0);
        // 创建时间格式化模版
        jxl.write.DateFormat dateFormat = new jxl.write.DateFormat("yyyy-MM-dd HH:mm:ss");
        jxl.write.WritableCellFormat sheetDateFormat = new jxl.write.WritableCellFormat(dateFormat);

        // 将页头添加进sheet页中
        for (int i = 0; i < columnChs.length; i++) {
            sheet0.addCell(new Label(i, 0, columnChs[i]));
        }

        // 向sheet页中添加数据
        for (int i = 0; i < dataList.size(); i++) {

            for (int j = 0; j < columnEns.length; j++) {

                Object data = dataList.get(i).get(columnEns[j]);

                if (data != null && data instanceof Date) {
                    // 处理日期类型
                    jxl.write.DateTime labelDT = new jxl.write.DateTime(j, i + 1, (Date) data, sheetDateFormat);
                    sheet0.addCell(labelDT);
                } else {

                    if (data == null || data.toString().trim().isEmpty() || data.toString().trim().equalsIgnoreCase("null")) {
                        data = "";
                    }

                    Label label = new Label(j, i + 1, data.toString());
                    sheet0.addCell(label);
                }
            }
        }

        workbook.write();
        workbook.close();
    }

    public static void main(String[] args) throws Exception {

        String sheetTitle = "人员信息";

        String[] columnChs = {"姓名", "年龄", "日期"};

        String[] columnEns = {"username", "age", "date"};

        List<Map<String, Object>> dataList = new ArrayList<>();

        Map<String, Object> scy = new HashMap<>(16);
        scy.put(columnEns[0], "史春阳");
        scy.put(columnEns[1], "NULL");
        scy.put(columnEns[2], new Date());

        Map<String, Object> zgl = new HashMap<>(16);
        zgl.put(columnEns[0], "诸葛亮");
        zgl.put(columnEns[1], "52");
        zgl.put(columnEns[2], "   ");

        dataList.add(scy);
        dataList.add(zgl);

        String fileName = "src/main/resources/hero.xls";

        ExcelUtil.createExcel(new FileOutputStream(fileName), sheetTitle, columnChs, columnEns, dataList);

        System.out.println("成功了");
    }
}
