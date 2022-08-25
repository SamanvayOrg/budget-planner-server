package org.mbs.budgetplannerserver.export.service;

import org.apache.poi.ss.usermodel.*;
import org.mbs.budgetplannerserver.export.data.CustomCellStyle;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class StylesGenerator {

    Map<CustomCellStyle, CellStyle> prepareStyles(Workbook wb) {
        Font boldArial = createBoldArialFont(wb);
        Font redBoldArial = createRedBoldArialFont(wb);
        Font largeBoldArial = createLargeBoldArialFont(wb);

        CellStyle centeredBoldArialWithoutBorderStyle = createCenteredBoldArialWithoutBorderStyle(wb, largeBoldArial);
        CellStyle rightAlignedStyle = createRightAlignedStyle(wb);
        CellStyle rightAlignedBoldStyle = createRightAlignedBoldStyle(wb, boldArial);
        CellStyle leftAlignedStyle = createLeftAlignedStyle(wb);
        CellStyle centerAlignedStyle = createCenterAlignedStyle(wb);
        CellStyle centerAlignedBoldStyle = createCenterAlignedBoldStyle(wb, boldArial);
        CellStyle greyCenteredBoldArialWithBorderStyle =
                createGreyCenteredBoldArialWithBorderStyle(wb, boldArial);
        CellStyle redBoldArialWithBorderStyle =
                createRedBoldArialWithBorderStyle(wb, redBoldArial);
        CellStyle rightAlignedDateFormatStyle =
                createRightAlignedDateFormatStyle(wb);

        return Map.of(
                CustomCellStyle.CENTERED_BOLD_ARIAL_WITHOUT_BORDER, centeredBoldArialWithoutBorderStyle,
                CustomCellStyle.RIGHT_ALIGNED, rightAlignedStyle,
                CustomCellStyle.RIGHT_ALIGNED_BOLD, rightAlignedBoldStyle,
                CustomCellStyle.CENTER_ALIGNED_BOLD, centerAlignedBoldStyle,
                CustomCellStyle.LEFT_ALIGNED, leftAlignedStyle,
                CustomCellStyle.CENTER_ALIGNED, centerAlignedStyle,
                CustomCellStyle.GREY_CENTERED_BOLD_ARIAL_WITH_BORDER, greyCenteredBoldArialWithBorderStyle,
                CustomCellStyle.RED_BOLD_ARIAL_WITH_BORDER, redBoldArialWithBorderStyle,
                CustomCellStyle.RIGHT_ALIGNED_DATE_FORMAT, rightAlignedDateFormatStyle
        );
    }

    private CellStyle createRightAlignedBoldStyle(Workbook wb, Font boldArial) {
        CellStyle style = wb.createCellStyle();
        style.setFont(boldArial);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createCenterAlignedBoldStyle(Workbook wb, Font boldArial) {
        CellStyle style = wb.createCellStyle();
        style.setFont(boldArial);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCenteredBoldArialWithoutBorderStyle(Workbook wb, Font largeBoldArial) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.NONE);
        style.setBorderTop(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        style.setBorderLeft(BorderStyle.NONE);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(largeBoldArial);
        return style;
    }

    private CellStyle createRightAlignedDateFormatStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat((short) 14);
        return style;
    }

    private CellStyle createRedBoldArialWithBorderStyle(Workbook wb, Font redBoldArial) {
        CellStyle style = createBorderedStyle(wb);
        style.setFont(redBoldArial);
        return style;
    }

    private CellStyle createGreyCenteredBoldArialWithBorderStyle(Workbook wb, Font boldArial) {
        CellStyle style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(boldArial);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createBorderedStyle(Workbook wb) {
        BorderStyle thin = BorderStyle.THIN;
        Short black = IndexedColors.BLACK.getIndex();
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(thin);
        style.setRightBorderColor(black);
        style.setBorderBottom(thin);
        style.setBottomBorderColor(black);
        style.setBorderLeft(thin);
        style.setLeftBorderColor(black);
        style.setBorderTop(thin);
        style.setTopBorderColor(black);
        return style;
    }

    private CellStyle createRightAlignedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createLeftAlignedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle createCenterAlignedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private Font createRedBoldArialFont(Workbook wb) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(IndexedColors.RED.index);
        return font;
    }

    private Font createBoldArialFont(Workbook wb) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        return font;
    }

    private Font createLargeBoldArialFont(Workbook wb) {
        Font font = wb.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeightInPoints((short) 24);
        return font;
    }
}
