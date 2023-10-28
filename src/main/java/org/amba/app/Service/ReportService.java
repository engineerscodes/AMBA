package org.amba.app.Service;

import org.amba.app.Dto.ReportDTO;
import org.amba.app.Util.Doc;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ReportService {

    List<String> columns = List.of("EMAIL", "PROJECT", "PROJECT TYPE",
            "QUESTION ANSWERED","SCORE","TOTAL QUESTION AVAILABLE","USER ROLE","REPORT DATE TIME");

    public Doc generateReport(){


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Student data");

        CreationHelper createHelper = workbook.getCreationHelper();

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.AQUA.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for(int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerCellStyle);
        }
        return Doc.builder().workbook(workbook).sheet(sheet).createHelper(createHelper).build();
    }

    public void addRow( XSSFWorkbook workbook, XSSFSheet sheet,CreationHelper createHelper ,ReportDTO reportDTO){

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.createRow(++lastRowNum);
        row.createCell(0).setCellValue(reportDTO.getEmail());
        row.createCell(1).setCellValue(reportDTO.getProject());
        row.createCell(2).setCellValue(reportDTO.getType());
        row.createCell(3).setCellValue(reportDTO.getQuestionNumber().toString());
        row.createCell(4).setCellValue(reportDTO.getScore());
        row.createCell(5).setCellValue(reportDTO.getTotalQuestions());
        row.createCell(6).setCellValue(reportDTO.getRole());
        Cell date = row.createCell(7);
        date.setCellValue(reportDTO.getReportDate());
        date.setCellStyle(dateCellStyle);

    }

    public void save( XSSFWorkbook workbook, XSSFSheet sheet) throws IOException {
        // Resize all columns to fit the content size
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        for(int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        String name = "src//main//resources//Files//StudentReport"+ date+UUID.randomUUID()+ ".xlsx";
        FileOutputStream fileOut = new FileOutputStream(name);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }


}
