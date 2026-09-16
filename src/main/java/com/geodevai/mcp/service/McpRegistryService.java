package com.geodevai.mcp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.geodevai.mcp.annotation.McpParameter;
import com.geodevai.mcp.annotation.McpPrompt;
import com.geodevai.mcp.annotation.McpResource;
import com.geodevai.mcp.annotation.McpService;
import com.geodevai.mcp.annotation.McpTool;
import com.geodevai.mcp.exception.McpException;
import com.geodevai.mcp.exception.McpInvalidParamsException;
import com.geodevai.mcp.exception.McpMethodNotFoundException;
import com.geodevai.mcp.model.McpMethodInfo;
import com.geodevai.mcp.model.McpPromptInfo;
import com.geodevai.mcp.model.McpResourceInfo;
import com.geodevai.mcp.model.McpResponse;
import com.geodevai.mcp.model.McpToolInfo;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class McpRegistryService implements ApplicationListener<ContextRefreshedEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<McpMethodInfo> tools = new ArrayList<>();
    private final List<McpMethodInfo> resources = new ArrayList<>();
    private final List<McpMethodInfo> prompts = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        tools.clear();
        resources.clear();
        prompts.clear();

        Map<String, ?> beans = event.getApplicationContext().getBeansWithAnnotation(McpService.class);
        for (Object bean : beans.values()) {
            scanBean(bean);
        }
    }

    public void registerBean(Object bean) {
        scanBean(bean);
    }

    void scanBean(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            McpTool toolAnnotation = AnnotationUtils.findAnnotation(method, McpTool.class);
            if (toolAnnotation != null) {
                McpMethodInfo info = new McpMethodInfo();
                info.setBean(bean);
                info.setMethod(method);
                info.setName(toolAnnotation.name());
                info.setDescription(toolAnnotation.description());
                info.setParameters(generateParamList(method));
                tools.add(info);
                continue;
            }

            McpResource resourceAnnotation = AnnotationUtils.findAnnotation(method, McpResource.class);
            if (resourceAnnotation != null) {
                McpMethodInfo info = new McpMethodInfo();
                info.setBean(bean);
                info.setMethod(method);
                info.setName(resourceAnnotation.uriPattern());
                info.setDescription(resourceAnnotation.description());
                info.setParameters(generateParamList(method));
                resources.add(info);
                continue;
            }

            McpPrompt promptAnnotation = AnnotationUtils.findAnnotation(method, McpPrompt.class);
            if (promptAnnotation != null) {
                McpMethodInfo info = new McpMethodInfo();
                info.setBean(bean);
                info.setMethod(method);
                info.setName(promptAnnotation.name());
                info.setDescription(promptAnnotation.description());
                info.setParameters(generateParamList(method));
                prompts.add(info);
            }
        }
    }

    private List<Map<String, Object>> generateParamList(Method method) {
        List<Map<String, Object>> params = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("type", JsonSchemaGenerator.mapType(param.getType()));
            if (param.isAnnotationPresent(McpParameter.class)) {
                McpParameter ann = param.getAnnotation(McpParameter.class);
                paramMap.put("name", ann.name());
                paramMap.put("description", ann.description());
                paramMap.put("required", ann.required());
            }
            params.add(paramMap);
        }
        return params;
    }

    public List<McpToolInfo> listTools() {
        return mapToToolInfo(tools);
    }

    public List<McpResourceInfo> listResources() {
        return mapToResourceInfo(resources);
    }

    public List<McpPromptInfo> listPrompts() {
        return mapToPromptInfo(prompts);
    }

    public ObjectNode listToolsAsJson() {
        return buildToolsJson();
    }

    public ObjectNode listResourcesAsJson() {
        return buildResourcesJson();
    }

    public ObjectNode listPromptsAsJson() {
        return buildPromptsJson();
    }

    public McpResponse executeTool(String toolName, Map<String, Object> arguments) {
        McpMethodInfo methodInfo = tools.stream()
                .filter(t -> t.getName().equals(toolName))
                .findFirst()
                .orElseThrow(() -> new McpMethodNotFoundException(toolName));

        Method method = methodInfo.getMethod();
        Object bean = methodInfo.getBean();
        Parameter[] params = method.getParameters();

        if (arguments == null && params.length > 0) {
            throw new McpInvalidParamsException("Missing arguments for tool: " + toolName);
        }

        try {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                if (arguments != null && arguments.containsKey(params[i].getName())) {
                    args[i] = objectMapper.convertValue(arguments.get(params[i].getName()), params[i].getType());
                } else if (params[i].isAnnotationPresent(McpParameter.class)) {
                    McpParameter ann = params[i].getAnnotation(McpParameter.class);
                    if (arguments != null && arguments.containsKey(ann.name())) {
                        args[i] = objectMapper.convertValue(arguments.get(ann.name()), params[i].getType());
                    } else if (ann.required()) {
                        throw new McpInvalidParamsException("Missing required parameter: " + ann.name());
                    }
                }
            }

            method.setAccessible(true);
            Object result = method.invoke(bean, args);
            McpResponse response = new McpResponse();
            response.setResult(objectMapper.valueToTree(result));
            return response;
        } catch (IllegalAccessException e) {
            throw new McpException("Failed to invoke method: " + toolName, e);
        } catch (InvocationTargetException e) {
            throw new McpException("Method execution failed: " + toolName, e.getCause());
        } catch (IllegalArgumentException e) {
            throw new McpInvalidParamsException("Invalid parameters for tool: " + toolName);
        }
    }

    public Object executeToolRaw(String toolName, Object[] args) {
        McpMethodInfo methodInfo = tools.stream()
                .filter(t -> t.getName().equals(toolName))
                .findFirst()
                .orElseThrow(() -> new McpMethodNotFoundException(toolName));

        try {
            Object result = methodInfo.getMethod().invoke(methodInfo.getBean(), args);
            return result;
        } catch (IllegalAccessException e) {
            throw new McpException("Failed to invoke method: " + toolName, e);
        } catch (InvocationTargetException e) {
            throw new McpException("Method execution failed: " + toolName, e.getCause());
        }
    }

    private List<McpToolInfo> mapToToolInfo(List<McpMethodInfo> methods) {
        List<McpToolInfo> result = new ArrayList<>();
        for (McpMethodInfo info : methods) {
            McpToolInfo tool = new McpToolInfo();
            tool.setName(info.getName());
            tool.setDescription(info.getDescription());
            tool.setParameters(info.getParameters());
            result.add(tool);
        }
        return result;
    }

    private List<McpResourceInfo> mapToResourceInfo(List<McpMethodInfo> methods) {
        List<McpResourceInfo> result = new ArrayList<>();
        for (McpMethodInfo info : methods) {
            McpResourceInfo resource = new McpResourceInfo();
            resource.setUriPattern(info.getName());
            resource.setDescription(info.getDescription());
            result.add(resource);
        }
        return result;
    }

    private List<McpPromptInfo> mapToPromptInfo(List<McpMethodInfo> methods) {
        List<McpPromptInfo> result = new ArrayList<>();
        for (McpMethodInfo info : methods) {
            McpPromptInfo prompt = new McpPromptInfo();
            prompt.setName(info.getName());
            prompt.setDescription(info.getDescription());
            result.add(prompt);
        }
        return result;
    }

    private ObjectNode buildToolsJson() {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode toolsArray = objectMapper.createArrayNode();
        for (McpToolInfo tool : listTools()) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("name", tool.getName());
            toolNode.put("description", tool.getDescription());
            ObjectNode schema = objectMapper.createObjectNode();
            schema.put("type", "object");
            ArrayNode requiredArray = objectMapper.createArrayNode();
            for (Map<String, Object> param : tool.getParameters()) {
                if ((Boolean) param.getOrDefault("required", true)) {
                    requiredArray.add(param.get("name").toString());
                }
            }
            schema.set("required", requiredArray);
            toolNode.set("schema", schema);
            toolsArray.add(toolNode);
        }
        root.set("tools", toolsArray);
        return root;
    }

    private ObjectNode buildResourcesJson() {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode resourcesArray = objectMapper.createArrayNode();
        for (McpResourceInfo resource : listResources()) {
            ObjectNode resourceNode = objectMapper.createObjectNode();
            resourceNode.put("uri", resource.getUriPattern());
            resourceNode.put("description", resource.getDescription());
            resourcesArray.add(resourceNode);
        }
        root.set("resources", resourcesArray);
        return root;
    }

    private ObjectNode buildPromptsJson() {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode promptsArray = objectMapper.createArrayNode();
        for (McpPromptInfo prompt : listPrompts()) {
            ObjectNode promptNode = objectMapper.createObjectNode();
            promptNode.put("name", prompt.getName());
            promptNode.put("description", prompt.getDescription());
            promptsArray.add(promptNode);
        }
        root.set("prompts", promptsArray);
        return root;
    }
}
