package com.sfg.services.listeners;

import com.sfg.services.order.BeerOrderManager;
import com.sfg.web.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeerOrderAllocationResultListener {
    private final BeerOrderManager beerOrderManager;
    public void listen(AllocateOrderResult result) {
        if (Boolean.TRUE.equals(!result.getAllocationError()) && Boolean.TRUE.equals(!result.getPendingInventory())) {
            beerOrderManager.beerOrderAllocationPassed(result.getBeerOrderDto());
        } else if (!result.getAllocationError() && result.getPendingInventory()) {
            beerOrderManager.beerOrderAllocationPendingInventory(result.getBeerOrderDto());
        } else if (result.getAllocationError()) {
            beerOrderManager.beerOrderAllocationFailed(result.getBeerOrderDto());
        }
    }
}
