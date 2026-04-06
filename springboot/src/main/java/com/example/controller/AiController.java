package com.example.controller;

import com.example.common.Result;
import com.example.entity.Department;
import com.example.service.DepartmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private DepartmentService departmentService;

    @Resource
    private RestTemplate restTemplate;

    @Value("${deepseek.apiKey}")
    private String apiKey;

    @Value("${deepseek.apiUrl}")
    private String apiUrl;

    /**
     * AI 聊天接口
     */
    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, String> params) {
        String userMessage = params.get("message");
        if (userMessage == null || userMessage.isEmpty()) {
            return Result.error("-1", "消息内容不能为空");
        }

        // 1. 获取所有社团信息作为背景知识
        List<Department> departments = departmentService.selectAll(null);
        StringBuilder context = new StringBuilder("你是一个大学社团管理系统的智能助手。以下是系统中所有社团的信息：\n");
        for (Department dept : departments) {
            context.append(String.format("- 社团名称：%s，社团介绍：%s\n", dept.getName(), dept.getDescription()));
        }
        context.append("\n请根据以上信息回答用户的问题。如果用户询问的问题不在社团范围内，请礼貌地告知并尝试以助手身份回答。如果是具体的社团操作问题，请引导用户去相应的界面查看。");

        // 2. 构造 DeepSeek 请求负载 (OpenAI 兼容格式)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", context.toString());
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("stream", false);

        // 3. 发送请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return Result.success(content);
                }
            }
            return Result.error("-1", "AI 响应异常");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("-1", "调用 AI 接口失败：" + e.getMessage());
        }
    }
}
