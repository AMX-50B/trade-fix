package com.lbx.tradefix;

import com.lbx.tradefix.service.OrderService;
import com.lbx.tradefix.service.TradeFixService;
import com.lbx.tradefix.vo.OutboundDetail;
import com.lbx.tradefix.vo.StockEntity;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@MapperScan("com.lbx.tradefix.dao")
public class FixStock {

    @Resource
    TradeFixService tradeFixService;
    @Resource
    OrderService orderService;

    @Test
    public void test() {
        //1、查询待修复的数据
        List<StockEntity> stockEntities = tradeFixService.getFixDate();
        //2、查询tidb
        System.out.println("groupId" + '\t' + "companyId" + '\t' + "businessId" + '\t' + "billNumber" + '\t' + "billType" + '\t' + "businessType" + '\t' + "wareInsideCode" + '\t' + "madeNumber" + '\t' + "batchCode" + '\t' + "stallType" + '\t' + "applyQty" + '\t' + "stallId");
        for (StockEntity stockEntity : stockEntities) {
            List<OutboundDetail> outboundDetails = orderService.selectStock(stockEntity.getGroupId(), stockEntity.getCompanyId(), stockEntity.getBusinessId(),
                    stockEntity.getBillNumber(), Long.valueOf(stockEntity.getWareInsideCode()));
            if (outboundDetails == null || outboundDetails.isEmpty()) {
                System.out.println("outboundDetail is null, billNo:" + stockEntity.getBillNumber());
            } else {
                for (OutboundDetail detail : outboundDetails) {
                    Integer stallId = orderService.selectStock2(stockEntity.getGroupId(), stockEntity.getCompanyId(), stockEntity.getBusinessId());
                    detail.setStallId(stallId);
                    //把数据写入到excel文档

                }

            }
        }
    }


    @Test
    public void test2() throws IOException {
        //1、查询待修复的数据
        List<StockEntity> stockEntities = tradeFixService.getFixDate();
        //2、查询tidb
        Workbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Outbound Details");
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"groupId", "companyId", "businessId", "billNumber", "billType", "businessType", "wareInsideCode", "madeNumber", "batchCode", "stallType", "applyQty", "stallId"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (StockEntity stockEntity : stockEntities) {
            List<OutboundDetail> outboundDetails = orderService.selectStock(stockEntity.getGroupId(), stockEntity.getCompanyId(), stockEntity.getBusinessId(),
                    stockEntity.getBillNumber(), Long.valueOf(stockEntity.getWareInsideCode()));
            if (outboundDetails == null || outboundDetails.isEmpty()) {
                System.out.println("outboundDetail is null, billNo:" + stockEntity.getBillNumber());
            } else {
                for (OutboundDetail detail : outboundDetails) {
                    Integer stallId = orderService.selectStock2(stockEntity.getGroupId(), stockEntity.getCompanyId(), stockEntity.getBusinessId());
                    detail.setStallId(stallId);
                    System.out.println(detail.getGroupId() + '\t' + detail.getCompanyId() + '\t' + detail.getBusinessId() + '\t' + detail.getBillNumber() + '\t'
                            + detail.getBillType() + '\t' + detail.getBusinessType() + '\t' + detail.getWareInsideCode() + '\t' + detail.getMadeNumber() + '\t' + detail.getBatchCode() +
                            '\t' + detail.getStallType() + '\t' + detail.getApplyQty() + '\t' + detail.getStallId());

                    // Create a row for each OutboundDetail
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(detail.getGroupId());
                    row.createCell(1).setCellValue(detail.getCompanyId());
                    row.createCell(2).setCellValue(detail.getBusinessId());
                    row.createCell(3).setCellValue(detail.getBillNumber());
                    row.createCell(4).setCellValue(detail.getBillType());
                    row.createCell(5).setCellValue(detail.getBusinessType());
                    row.createCell(6).setCellValue(detail.getWareInsideCode());
                    row.createCell(7).setCellValue(detail.getMadeNumber());
                    row.createCell(8).setCellValue(detail.getBatchCode() == null ? 0 : detail.getBatchCode());
                    row.createCell(9).setCellValue(detail.getStallType());
                    row.createCell(10).setCellValue(detail.getApplyQty());
                    row.createCell(11).setCellValue(detail.getStallId() == null ? 0 : detail.getStallId());
                }
            }
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    @Test
    public void test3() throws IOException {
        //1、查询待修复的数据
        List<StockEntity> stockEntities = tradeFixService.getFixDate();
        //2、查询tidb
        Workbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Outbound Details");
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"groupId", "companyId", "businessId", "billNumber", "billType", "businessType", "wareInsideCode", "madeNumber", "batchCode", "stallType", "applyQty", "stallId"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (StockEntity stockEntity : stockEntities) {
            //根据单据号查询需要的信息
            //查询货位id
            List<OutboundDetail> outboundDetails = orderService.selectStock(stockEntity.getGroupId(), stockEntity.getCompanyId(), stockEntity.getBusinessId(),
                    stockEntity.getBillNumber(), Long.valueOf(stockEntity.getWareInsideCode()));
            if (outboundDetails == null || outboundDetails.isEmpty()) {
                System.out.println("outboundDetail is null, billNo:" + stockEntity.getBillNumber());
            } else {
                OutboundDetail detail = outboundDetails.get(0);
                Integer stallId = orderService.selectStock2(stockEntity.getGroupId(), stockEntity.getCompanyId(), stockEntity.getBusinessId());

                // Create a row for each OutboundDetail
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stockEntity.getGroupId());
                row.createCell(1).setCellValue(stockEntity.getCompanyId());
                row.createCell(2).setCellValue(stockEntity.getBusinessId());
                row.createCell(3).setCellValue(stockEntity.getBillNumber());
                row.createCell(4).setCellValue(52);
                row.createCell(5).setCellValue(2);
                row.createCell(6).setCellValue(stockEntity.getWareInsideCode());
                row.createCell(7).setCellValue(detail.getMadeNumber());
                row.createCell(8).setCellValue(detail.getBatchCode() == null ? 0 : detail.getBatchCode());
                row.createCell(9).setCellValue(1);
                row.createCell(10).setCellValue(stockEntity.getOriginNum());
                row.createCell(11).setCellValue(stallId == null ? 0 : stallId);

            }
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
