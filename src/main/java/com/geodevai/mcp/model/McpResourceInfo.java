package com.geodevai.mcp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class McpResourceInfo {
    private String uriPattern;
    private String description;
}
