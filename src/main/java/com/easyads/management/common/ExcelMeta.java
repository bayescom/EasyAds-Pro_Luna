package com.easyads.management.common;

import com.easyads.component.utils.ExcelExportUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExcelMeta {
    private String cellTitle;
    private ExcelExportUtils.CELL_STYLE cellStyle;

    public ExcelMeta(String cellTitle) {
        this.cellTitle = cellTitle;
        this.cellStyle = ExcelExportUtils.CELL_STYLE.STRING;
    }
}
