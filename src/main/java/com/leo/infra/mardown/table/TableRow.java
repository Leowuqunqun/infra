package com.leo.infra.mardown.table;


import java.util.ArrayList;
import java.util.List;

public class TableRow<T> {
    
    private List<T> columns;
    
    public TableRow() {
        this.columns = new ArrayList<>();
    }
    
    public TableRow(List<T> columns) {
        this.columns = columns;
    }
    
    
    public List<T> getColumns() {
        return this.columns;
    }
    
    public void setColumns(List<T> columns) {
        this.columns = columns;
    }
    
}
