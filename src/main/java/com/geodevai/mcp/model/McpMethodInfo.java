package com.geodevai.mcp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class McpMethodInfo {
    private Object bean;
    private Method method;
    private String name;
    private String description;
    private List<Map<String, Object>> parameters;
}
