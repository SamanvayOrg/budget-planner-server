package org.mbs.budgetplannerserver.export.data;

import org.apache.poi.ss.usermodel.CellStyle;

public class CustomCellStyleAndValue {
    CellStyle style;
    String value;

    public CustomCellStyleAndValue() {
    }

    public CustomCellStyleAndValue(CellStyle style, String value) {
        this.style = style;
        this.value = value;
    }

    public CellStyle getStyle() {
        return style;
    }

    public void setStyle(CellStyle style) {
        this.style = style;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
