package com.geodevai.mcp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class McpToolInfo {
    private String name;
    private String description;
    private List<Map<String, Object>> parameters;
}
