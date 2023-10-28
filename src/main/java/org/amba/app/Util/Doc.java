package org.amba.app.Util;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Builder
@Data
public class Doc {

    XSSFWorkbook workbook;

    XSSFSheet sheet;

    CreationHelper createHelper;

}
