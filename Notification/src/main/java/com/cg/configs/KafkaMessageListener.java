package com.cg.configs;

import com.cg.dto.LoanDto;
import com.cg.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "messageService",groupId = "ycp")
    public void consumeEvents(LoanDto loanDto) {
        log.info("consumer consume the events {} ", loanDto.toString());
        notificationService.sendNotification(loanDto);
    }

}
