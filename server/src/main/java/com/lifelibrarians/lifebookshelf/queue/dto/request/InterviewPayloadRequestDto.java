package com.lifelibrarians.lifebookshelf.queue.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class InterviewPayloadRequestDto {
    private Long autobiographyId;
    private Long userId;
    private Integer categoryId; // AI에서 보내는 categoryId 필드 추가
    private List<Conversation> conversation;
    private InterviewQuestion interviewQuestion;

    @Data
    @NoArgsConstructor
    public static class Conversation {
        private String content;
        private String conversationType;
        private LocalDateTime timestamp;
    }

    @Data
    @NoArgsConstructor
    public static class InterviewQuestion {
        private String questionText;
        private int questionOrder;
        private LocalDateTime timestamp;
        private String materials;
    }
}