package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.queue.dto.request.CategoriesPayloadRequestDto;
import com.lifelibrarians.lifebookshelf.queue.service.CategoriesPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriesConsumer {
    private final CategoriesPersistenceService categoriesPersistenceService;

    @RabbitListener(queues = "interview.meta.queue")
    public void receive(CategoriesPayloadRequestDto dto) {
        categoriesPersistenceService.receiveCategoriesPayload(dto);
    }
}
