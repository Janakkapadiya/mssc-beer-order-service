package com.sfg.services.listeners;

import com.sfg.config.JmsConfig;
import com.sfg.services.order.BeerOrderManager;
import com.sfg.web.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {
    private final BeerOrderManager beerOrderManager;
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result){
        final UUID beerOrderId = result.getOrderId();

        log.info("Validation Result for Order Id: " + beerOrderId);

        log.info("is beerOrderId Valid {}" , result.getIsValid());

        beerOrderManager.processValidationResult(beerOrderId, result.getIsValid());
    }
}