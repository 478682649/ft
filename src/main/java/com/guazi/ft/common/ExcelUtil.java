package com.guazi.ft.common;

import com.google.common.collect.Lists;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 *
 * @author shichunyang
 */
@Slf4j
public class ExcelUtil {

    /**
     * 对外输出excel
     *
     * @param out       输出流
     * @param sheetName sheet页名称
     * @param columnChs 列名(中文)
     * @param dataList  表格中数据
     */
    public static void createExcel(
            OutputStream out,
            String sheetName,
            List<String> columnChs,
            List<Map<String, Object>> dataList,
            int limit
    ) {
        // 创建时间格式化模版
        jxl.write.WritableCellFormat writableCellFormat = new jxl.write.WritableCellFormat(new jxl.write.DateFormat(DateUtil.DEFAULT_DATE_FORMAT));

        WritableWorkbook writableWorkbook = null;
        try {
            writableWorkbook = Workbook.createWorkbook(out);
            // 分组处理
            List<List<Map<String, Object>>> partitions = Lists.partition(dataList, limit);

            for (int m = 0; m < partitions.size(); m++) {
                WritableSheet writableSheet = writableWorkbook.createSheet(sheetName + (m + 1), m);

                // 将页头添加进sheet页中
                for (int i = 0; i < columnChs.size(); i++) {
                    writableSheet.addCell(new Label(i, 0, columnChs.get(i)));
                }

                List<Map<String, Object>> tempList = partitions.get(m);
                // 向sheet页中添加数据
                for (int i = 0; i < tempList.size(); i++) {
                    Map<String, Object> tempMap = tempList.get(i);

                    for (int j = 0; j < columnChs.size(); j++) {
                        Object data = tempMap.get(columnChs.get(j));

                        if (data != null && data instanceof Date) {
                            // 处理日期类型
                            jxl.write.DateTime dateTime = new jxl.write.DateTime(j, i + 1, (Date) data, writableCellFormat);
                            writableSheet.addCell(dateTime);
                        } else {
                            if (StringUtil.isEmpty(data)) {
                                data = "";
                            }

                            Label label = new Label(j, i + 1, data.toString());
                            writableSheet.addCell(label);
                        }
                    }
                }
            }

            writableWorkbook.write();
        } catch (Exception e) {
            log.error("sheetName==>{}, columnChs==>{}, dataList==>{}, exception==>{}", sheetName, columnChs, dataList, JsonUtil.object2Json(e), e);
        } finally {
            if (writableWorkbook != null) {
                try {
                    writableWorkbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
