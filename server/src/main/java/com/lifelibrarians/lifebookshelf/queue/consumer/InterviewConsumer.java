package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.queue.dto.request.InterviewPayloadRequestDto;
import com.lifelibrarians.lifebookshelf.queue.service.InterviewPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewConsumer {
    private final InterviewPersistenceService interviewPersistenceService;

    @RabbitListener(queues = "interview-queue")
    public void receive(InterviewPayloadRequestDto dto) {
        interviewPersistenceService.receiveInterviewPayload(dto);
    }
}
