package com.geodevai.mcp.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class McpResponse {
    private String jsonrpc = "2.0";
    private Object id;
    private JsonNode result;
    private McpError error;

    public boolean hasError() {
        return error != null;
    }
}
