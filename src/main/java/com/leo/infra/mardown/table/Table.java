package com.leo.infra.mardown.table;

import com.leo.infra.mardown.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    
    private List<TableRow<?>> rows;
    
    private final boolean firstRowIsHeader;
    
    private final int minimumColumnWidth;
    
    public Table() {
        this.firstRowIsHeader = true;
        this.minimumColumnWidth = 3;
        this.rows = new ArrayList<>();
    }
    
    public Table(List<TableRow<?>> rows) {
        this();
        this.rows = rows;
    }
    
    public String serialize() {
        Map<Integer, Integer> columnWidths = getColumnWidths(this.rows, this.minimumColumnWidth);
        StringBuilder sb = new StringBuilder();
        String headerSeparator = generateHeaderSeparator(columnWidths);
        boolean headerSeparatorAdded = !this.firstRowIsHeader;
        for (var row : this.rows) {
            for (int columnIndex = 0; columnIndex < columnWidths.size(); ++columnIndex) {
                sb.append("|");
                String value = null;
                if (row.getColumns().size() > columnIndex) {
                    Object valueObject = row.getColumns().get(columnIndex);
                    if (valueObject != null) {
                        value = valueObject.toString();
                    }
                }
                
                value = StringUtil.surroundValueWith(value, " ");
                value = StringUtil.fillUpLeftAligned(value, " ", columnWidths.get(columnIndex) + 2);
                
                sb.append(value);
                if (columnIndex == row.getColumns().size() - 1) {
                    sb.append("|");
                }
            }
            
            if (this.rows.indexOf(row) < this.rows.size() - 1) {
                sb.append(System.lineSeparator());
            }
            
            if (!headerSeparatorAdded) {
                sb.append(headerSeparator).append(System.lineSeparator());
                headerSeparatorAdded = true;
            }
        }
        
        return sb.toString();
    }
    
    
    public static String generateHeaderSeparator(Map<Integer, Integer> columnWidths) {
        StringBuilder sb = new StringBuilder();
        
        for (int columnIndex = 0; columnIndex < columnWidths.entrySet().size(); ++columnIndex) {
            sb.append("|");
            String value = StringUtil.fillUpLeftAligned("", "-", columnWidths.get(columnIndex));
            value = StringUtil.surroundValueWith(value, " ");
            
            sb.append(value);
            if (columnIndex == columnWidths.entrySet().size() - 1) {
                sb.append("|");
            }
        }
        
        return sb.toString();
    }
    
    public static Map<Integer, Integer> getColumnWidths(List<TableRow<?>> rows, int minimumColumnWidth) {
        Map<Integer, Integer> columnWidths = new HashMap<>();
        if (!rows.isEmpty()) {
            for (int columnIndex = 0; columnIndex < rows.get(0).getColumns().size(); ++columnIndex) {
                columnWidths.put(columnIndex, getMaximumItemLength(rows, columnIndex, minimumColumnWidth));
            }
            
        }
        return columnWidths;
    }
    
    public static int getMaximumItemLength(List<TableRow<?>> rows, int columnIndex, int minimumColumnWidth) {
        int maximum = minimumColumnWidth;
        
        for (TableRow<?> row : rows) {
            if (row.getColumns().size() >= columnIndex + 1) {
                Object value = row.getColumns().get(columnIndex);
                if (value != null) {
                    maximum = Math.max(value.toString().length(), maximum);
                }
            }
        }
        
        return maximum;
    }
    
    
    public List<TableRow<?>> getRows() {
        return this.rows;
    }
    
    public void setRows(List<TableRow<?>> rows) {
        this.rows = rows;
    }
    
    public static class Builder {
        
        private Table table = new Table();
        
        public Builder() {
        }
        
        public Table.Builder withRows(List<TableRow<?>> tableRows) {
            this.table.setRows(tableRows);
            return this;
        }
        
        public Table.Builder addRow(TableRow<?> tableRow) {
            this.table.getRows().add(tableRow);
            return this;
        }
        
        public Table.Builder addRow(Object... objects) {
            TableRow<?> tableRow = new TableRow<>(Arrays.asList(objects));
            this.table.getRows().add(tableRow);
            return this;
        }
        
        
        public Table build() {
            return this.table;
        }
    }
}
