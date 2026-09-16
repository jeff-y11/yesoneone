package com.geodevai.mcp.exception;

public class McpMethodNotFoundException extends McpException {
    public McpMethodNotFoundException(String toolName) {
        super("Method not found: " + toolName);
    }
}
