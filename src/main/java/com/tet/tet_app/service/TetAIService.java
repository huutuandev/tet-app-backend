package com.tet.tet_app.service;

import com.tet.tet_app.config.AIProperties;
import com.tet.tet_app.redis.service.AIRateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TetAIService {

    private final AIProperties aiProperties;
    private final AIRateLimitService rateLimitService;
    private final RestTemplate restTemplate = new RestTemplate();

    public String ask(Long userId, String userQuestion) {

        rateLimitService.checkLimit(userId);
        String systemPrompt = """
                Bạn là trợ lý cho website Tết Việt Nam.
                Chỉ trả lời các câu hỏi liên quan đến Tết Việt Nam.
                
                YÊU CẦU BẮT BUỘC:
                - Trả lời trong 1 đoạn văn duy nhất
                - Tối đa 4–5 câu
                - Không xuống dòng
                - Không lặp ý, không lan man
                - Trả lời dứt khoát, hoàn chỉnh
                
                Nếu câu hỏi không liên quan đến Tết Việt Nam, hãy lịch sự từ chối.
                """;


        Map<String, Object> body = Map.of(
                "model", aiProperties.getModel(),
                "temperature", 0.3,
                "max_tokens", 120,
                "top_p", 0.9,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userQuestion)
                )
        );



        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(aiProperties.getApi().getKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                aiProperties.getApi().getUrl(),
                request,
                Map.class
        );

        return extractText(response.getBody());
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {
        var choices = (List<Map>) response.get("choices");
        var message = (Map) choices.get(0).get("message");
        String content = message.get("content").toString().trim();

        // Gộp về 1 đoạn
        content = content.replaceAll("\\n+", " ");

        // Cắt tối đa 5 câu
        String[] sentences = content.split("(?<=\\.)");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(5, sentences.length); i++) {
            result.append(sentences[i]);
        }

        return result.toString().trim();
    }

}



