package com.skincare.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.skincare.dto.ReviewAnalyticsDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generatePdfReport(ReviewAnalyticsDTO analytics) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Tiêu đề báo cáo
            addPdfTitle(document, "Báo cáo thống kê đánh giá");
            addPdfGenerationInfo(document);

            // Thống kê tổng quan
            addPdfSection(document, "Thống kê tổng quan");
            addPdfOverviewStats(document, analytics);

            // Phân bố đánh giá
            addPdfSection(document, "Phân bố đánh giá");
            addPdfRatingDistribution(document, analytics.getRatingDistribution());

            // Hiệu suất chuyên viên
            if (analytics.getSpecialistPerformance() != null && !analytics.getSpecialistPerformance().isEmpty()) {
                addPdfSection(document, "Hiệu suất chuyên viên");
                addPdfSpecialistPerformance(document, analytics.getSpecialistPerformance());
            }

            // Đánh giá theo dịch vụ
            if (analytics.getServiceRatings() != null && !analytics.getServiceRatings().isEmpty()) {
                addPdfSection(document, "Đánh giá theo dịch vụ");
                addPdfServiceRatings(document, analytics.getServiceRatings());
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo PDF", e);
        }
    }

    public byte[] generateExcelReport(ReviewAnalyticsDTO analytics) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Tạo các sheet
            createOverviewSheet(workbook, analytics);
            createDistributionSheet(workbook, analytics);
            createSpecialistSheet(workbook, analytics);
            createServiceSheet(workbook, analytics);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo Excel", e);
        }
    }

    private void addPdfTitle(Document document, String title) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(20);
        document.add(titlePara);
    }

    private void addPdfGenerationInfo(Document document) throws DocumentException {
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph infoPara = new Paragraph(
            "Thời gian tạo: " + LocalDateTime.now().format(DATE_FORMATTER),
            infoFont
        );
        infoPara.setAlignment(Element.ALIGN_RIGHT);
        infoPara.setSpacingAfter(20);
        document.add(infoPara);
    }

    private void addPdfSection(Document document, String title) throws DocumentException {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph sectionPara = new Paragraph(title, sectionFont);
        sectionPara.setSpacingBefore(15);
        sectionPara.setSpacingAfter(10);
        document.add(sectionPara);
    }

    private void addPdfOverviewStats(Document document, ReviewAnalyticsDTO analytics) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        addPdfTableRow(table, "Tổng số đánh giá", String.valueOf(analytics.getTotalReviews()));
        addPdfTableRow(table, "Điểm trung bình", String.format("%.1f", analytics.getAverageRating()));
        addPdfTableRow(table, "Tỷ lệ phản hồi", String.format("%.1f%%", analytics.getResponseRate()));
        addPdfTableRow(table, "Thời gian phản hồi TB", String.format("%.1f giờ", analytics.getAvgResponseTime()));

        document.add(table);
    }

    private void addPdfRatingDistribution(Document document, Map<Integer, Long> distribution) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        // Header
        addPdfTableHeader(table, "Số sao", "Số lượng", "Tỷ lệ");

        // Calculate total
        long total = distribution.values().stream().mapToLong(Long::longValue).sum();

        // Data rows
        for (int i = 5; i >= 1; i--) {
            long count = distribution.getOrDefault(i, 0L);
            double percentage = total > 0 ? (count * 100.0 / total) : 0;
            addPdfTableRow(table, 
                i + " sao",
                String.valueOf(count),
                String.format("%.1f%%", percentage)
            );
        }

        document.add(table);
    }

    private void addPdfSpecialistPerformance(Document document, 
            List<ReviewAnalyticsDTO.SpecialistPerformance> performances) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        // Header
        addPdfTableHeader(table, 
            "Chuyên viên", "Số đánh giá", "Điểm TB", 
            "Tỷ lệ phản hồi", "T.gian phản hồi TB"
        );

        // Data rows
        for (ReviewAnalyticsDTO.SpecialistPerformance perf : performances) {
            addPdfTableRow(table,
                perf.getSpecialistName(),
                String.valueOf(perf.getTotalReviews()),
                String.format("%.1f", perf.getAverageRating()),
                String.format("%.1f%%", perf.getResponseRate()),
                String.format("%.1f giờ", perf.getAvgResponseTime())
            );
        }

        document.add(table);
    }

    private void addPdfServiceRatings(Document document,
            List<ReviewAnalyticsDTO.ServiceRating> ratings) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        // Header
        addPdfTableHeader(table, 
            "Dịch vụ", "Số đánh giá", "Điểm TB", "Phân bố"
        );

        // Data rows
        for (ReviewAnalyticsDTO.ServiceRating rating : ratings) {
            addPdfTableRow(table,
                rating.getServiceName(),
                String.valueOf(rating.getTotalReviews()),
                String.format("%.1f", rating.getAverageRating()),
                formatRatingDistribution(rating.getRatingDistribution())
            );
        }

        document.add(table);
    }

    private void addPdfTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addPdfTableRow(PdfPTable table, String... values) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, cellFont));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void createOverviewSheet(Workbook workbook, ReviewAnalyticsDTO analytics) {
        Sheet sheet = workbook.createSheet("Tổng quan");
        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Báo cáo thống kê đánh giá");
        titleCell.setCellStyle(createTitleStyle(workbook));

        // Generation time
        Row timeRow = sheet.createRow(rowNum++);
        timeRow.createCell(0).setCellValue("Thời gian tạo: " + 
            LocalDateTime.now().format(DATE_FORMATTER));

        rowNum++; // Empty row

        // Overview statistics
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Chỉ số");
        headerRow.createCell(1).setCellValue("Giá trị");
        headerRow.createCell(2).setCellValue("Tăng trưởng");

        addOverviewRow(sheet, rowNum++, "Tổng số đánh giá", 
            analytics.getTotalReviews(), analytics.getReviewsGrowth());
        addOverviewRow(sheet, rowNum++, "Điểm trung bình", 
            analytics.getAverageRating(), analytics.getRatingGrowth());
        addOverviewRow(sheet, rowNum++, "Tỷ lệ phản hồi", 
            analytics.getResponseRate(), analytics.getResponseRateGrowth());
        addOverviewRow(sheet, rowNum++, "Thời gian phản hồi TB", 
            analytics.getAvgResponseTime(), analytics.getResponseTimeGrowth());

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDistributionSheet(Workbook workbook, ReviewAnalyticsDTO analytics) {
        Sheet sheet = workbook.createSheet("Phân bố đánh giá");
        int rowNum = 0;

        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Số sao");
        headerRow.createCell(1).setCellValue("Số lượng");
        headerRow.createCell(2).setCellValue("Tỷ lệ");

        Map<Integer, Long> distribution = analytics.getRatingDistribution();
        long total = distribution.values().stream().mapToLong(Long::longValue).sum();

        // Data rows
        for (int i = 5; i >= 1; i--) {
            Row row = sheet.createRow(rowNum++);
            long count = distribution.getOrDefault(i, 0L);
            double percentage = total > 0 ? (count * 100.0 / total) : 0;

            row.createCell(0).setCellValue(i + " sao");
            row.createCell(1).setCellValue(count);
            row.createCell(2).setCellValue(String.format("%.1f%%", percentage));
        }

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createSpecialistSheet(Workbook workbook, ReviewAnalyticsDTO analytics) {
        if (analytics.getSpecialistPerformance() == null || 
            analytics.getSpecialistPerformance().isEmpty()) {
            return;
        }

        Sheet sheet = workbook.createSheet("Hiệu suất chuyên viên");
        int rowNum = 0;

        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Chuyên viên");
        headerRow.createCell(1).setCellValue("Số đánh giá");
        headerRow.createCell(2).setCellValue("Điểm TB");
        headerRow.createCell(3).setCellValue("Tỷ lệ phản hồi");
        headerRow.createCell(4).setCellValue("T.gian phản hồi TB");

        // Data rows
        for (ReviewAnalyticsDTO.SpecialistPerformance perf : analytics.getSpecialistPerformance()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(perf.getSpecialistName());
            row.createCell(1).setCellValue(perf.getTotalReviews());
            row.createCell(2).setCellValue(String.format("%.1f", perf.getAverageRating()));
            row.createCell(3).setCellValue(String.format("%.1f%%", perf.getResponseRate()));
            row.createCell(4).setCellValue(String.format("%.1f", perf.getAvgResponseTime()));
        }

        // Auto-size columns
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createServiceSheet(Workbook workbook, ReviewAnalyticsDTO analytics) {
        if (analytics.getServiceRatings() == null || 
            analytics.getServiceRatings().isEmpty()) {
            return;
        }

        Sheet sheet = workbook.createSheet("Đánh giá theo dịch vụ");
        int rowNum = 0;

        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Dịch vụ");
        headerRow.createCell(1).setCellValue("Số đánh giá");
        headerRow.createCell(2).setCellValue("Điểm TB");
        headerRow.createCell(3).setCellValue("Phân bố đánh giá");

        // Data rows
        for (ReviewAnalyticsDTO.ServiceRating rating : analytics.getServiceRatings()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rating.getServiceName());
            row.createCell(1).setCellValue(rating.getTotalReviews());
            row.createCell(2).setCellValue(String.format("%.1f", rating.getAverageRating()));
            row.createCell(3).setCellValue(formatRatingDistribution(rating.getRatingDistribution()));
        }

        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private void addOverviewRow(Sheet sheet, int rowNum, String label, Number value, Double growth) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value.toString());
        row.createCell(2).setCellValue(growth != null ? 
            String.format("%+.1f%%", growth) : "N/A");
    }

    private String formatRatingDistribution(Map<Integer, Long> distribution) {
        StringBuilder sb = new StringBuilder();
        for (int i = 5; i >= 1; i--) {
            if (i < 5) sb.append(", ");
            sb.append(i).append("★: ").append(distribution.getOrDefault(i, 0L));
        }
        return sb.toString();
    }
} 