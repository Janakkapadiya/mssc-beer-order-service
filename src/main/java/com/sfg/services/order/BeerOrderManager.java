package com.sfg.services.order;

import com.sfg.domain.BeerOrder;
import com.sfg.web.model.BeerOrderDto;

import java.util.UUID;

public interface BeerOrderManager {
    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID beerOrderId, Boolean isValid);

    void beerOrderAllocationPassed(BeerOrderDto beerOrder);

    void beerOrderAllocationPendingInventory(BeerOrderDto beerOrder);

    void beerOrderAllocationFailed(BeerOrderDto beerOrder);

    void beerOrderPickedUp(UUID id);

    void cancelOrder(UUID id);
}
