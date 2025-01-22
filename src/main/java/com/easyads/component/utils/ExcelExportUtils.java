package com.easyads.component.utils;


import com.easyads.management.common.ExcelMeta;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Map;


/**
 * 导出 excel 文件
 */
@Slf4j
public class ExcelExportUtils {
    private static final int MAX_ROW = 300000;

    public enum CELL_STYLE {INT, DOUBLE, PERCENT, STRING;}

    public static void exportReportData(HttpServletResponse response, String fileName,
                                        Map<String, ExcelMeta> cellMetaMap,
                                        List<Map<String, Object>> dataList) {
        int totalGroup = dataList.size() / MAX_ROW + 1;

        // 避免 Zip bomb 异常
        ZipSecureFile.setMinInflateRatio(0);
        //内存中只创建100个对象
        Workbook wb = new SXSSFWorkbook(100);           //关键语句
        Sheet sheet = null;     //工作表对象
        Row nRow = null;        //行对象
        Cell nCell = null;      //列对象

        for(int group = 0 ; group < totalGroup; group++) {
            wb.createSheet("工作簿_" + group);// 创建新的sheet对象
            sheet = wb.getSheetAt(group);        // 动态指定当前的工作表
            int pageRowNo = 0;                  // 新的工作表,重置工作表的行号为0
            // -----------定义表头-----------
            createTitleRow(sheet, pageRowNo++, cellMetaMap);
            // 插入表数据
            for(int idx = group * MAX_ROW; idx < (group + 1) * MAX_ROW && idx < dataList.size(); idx++) {
                Map<String, Object> dataMap = dataList.get(idx);
                nRow = sheet.createRow(pageRowNo++);    //新建行对象

                int columnCnt = 0;
                for(Map.Entry<String, ExcelMeta> excelMeta : cellMetaMap.entrySet()) {
                    String dataKey = excelMeta.getKey();
                    ExcelMeta cellMeta = excelMeta.getValue();
                    Object dataValue = dataMap.get(dataKey);
                    nCell = nRow.createCell(columnCnt++);
                    if (null != dataValue) {
                        if (cellMeta.getCellStyle() == CELL_STYLE.INT) {
                            nCell.setCellValue(Long.parseLong(dataValue.toString()));
                        }
                        else if (cellMeta.getCellStyle() == CELL_STYLE.DOUBLE) {
                            nCell.setCellValue(Double.parseDouble(dataValue.toString()));
                        }
                        else if (cellMeta.getCellStyle() == CELL_STYLE.PERCENT) {
                            nCell.setCellValue(Double.parseDouble(dataValue.toString()) / 100.0);
                            CellStyle cellStyle = wb.createCellStyle();
                            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
                            nCell.setCellStyle(cellStyle);
                        }
                        else {
                            nCell.setCellValue(dataValue.toString());
                        }
                    }
                    else {
                        nCell.setCellValue(StringUtils.EMPTY);
                    }
                }
            }
        }

        // 文件输出到Response中
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        OutputStream outputStream = null;
        try {
            response.flushBuffer();
            wb.write(response.getOutputStream());
            outputStream = response.getOutputStream();

            wb.close();
            outputStream.flush();
            outputStream.close();
        } catch (EOFException e) {
            // 错误正常，客户端主动关闭链接，导致的写入异常
            log.warn("Eof Exception", "client disconnect", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName());
        } catch (IOException e) {
            log.warn("IO Exception", "export excel exception", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName());
        }
    }

    private static void createTitleRow(Sheet sheet, int rowNum, Map<String, ExcelMeta> cellMetaMap) {
        Row nRow = sheet.createRow(rowNum);
        int column = 0;
        for(Map.Entry<String, ExcelMeta> entry : cellMetaMap.entrySet()){
            Cell cell_tem = nRow.createCell(column++);
            cell_tem.setCellValue(entry.getValue().getCellTitle());
        }
    }
}
