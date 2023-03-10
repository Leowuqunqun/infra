package com.leo.infra.mardown;

public class StringUtil {
    
    public static String fillUpLeftAligned(String value, String fill, int length) {
        if (value.length() < length) {
            StringBuilder valueBuilder = new StringBuilder(value);
            while (valueBuilder.length() < length) {
                valueBuilder.append(fill);
            }
            value = valueBuilder.toString();
        }
        return value;
    }
    
    
    public static String surroundValueWith(String value, String surrounding) {
        return surrounding + value + surrounding;
    }
    
   
}
