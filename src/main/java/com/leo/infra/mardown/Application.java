package com.leo.infra.mardown;


import com.leo.infra.mardown.table.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
    
    private final static String staticVariable = "\\$\\{\\w+\\}";
    
    private final static String dynamicVariable = "#\\{(\\w+)\\}";
    
    private final static String mainContent = """
            ## ${name}
            
            负责人：${owner}
            
            描述：<b>${description}</b>
            
            ### 入参对象  ${inputCode}
            #{inputObject}
            ### 出参  ${outputCode}
            #{outputObject}
            """;
    
    public static String processTemplate(String template, String regex, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        Matcher m = Pattern.compile(regex).matcher(template);
        while (m.find()) {
            String param = m.group();
            Object value = params.get(param.substring(2, param.length() - 1));
            m.appendReplacement(sb, value == null ? "" : value.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    
    public static void main(String[] args) {
        
        Map<String, Object> map = new HashMap<>();
        
        Table.Builder tableBuilder = new Table.Builder().addRow("字段", "名称", "必填", "类型", "描述");
        
        //写死查询走对象 or  执行内部的marmotAPI 脚本Code
        
        for (int i = 1; i <= 5; i++) {
            tableBuilder.addRow(i, 1, 2, 3, 4);
        }
        
        map.put("outputObject", tableBuilder.build().serialize());
        map.put("description", "我是静态");
        map.put("inputCode", "我是静态");
        map.put("outputCode", "我是静态");
        System.out.println(processTemplate(mainContent, staticVariable, map));
        System.out.println(processTemplate(mainContent, dynamicVariable, map));
    }
    
}
