package com.sfg.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sfg.domain.BeerOrder;
import com.sfg.domain.BeerOrderLine;
import com.sfg.domain.BeerOrderStatusEnum;
import com.sfg.domain.Customer;
import com.sfg.repositories.BeerOrderRepository;
import com.sfg.repositories.CustomerRepository;
import com.sfg.services.beer.impl.BeerServiceImpl;
import com.sfg.services.order.BeerOrderManager;
import com.sfg.web.model.BeerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.jgroups.util.Util.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WireMockExtension.class)
@SpringBootTest
class BeerOrderManagerImplIT {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    JmsTemplate jmsTemplate;

    Customer testCustomer;

    UUID beerId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer(){
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            Assertions.assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });

        BeerOrder savedBeerOrder2 = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertNotNull(savedBeerOrder2);
        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());
        savedBeerOrder2.getBeerOrderLines().forEach(line -> {
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
    }

    public BeerOrder createBeerOrder(){
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());

        beerOrder.setBeerOrderLines(lines);

        return beerOrder;
    }
}





//    @Test
//    @Ignore
//    void testFailedValidation() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//        beerOrder.setCustomerRef("fail-validation");
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//
//            assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
//        });
//    }
//
//    @Test
//    @Ignore
//    void testNewToPickedUp() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
//        });
//
//        beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
//        });
//
//        BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();
//
//        assertEquals(BeerOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());
//    }
//
//    @Test
//    @Ignore
//    void testAllocationFailure() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//        beerOrder.setCustomerRef("fail-allocation");
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
//        });
//
//        AllocationFailureEvent allocationFailureEvent = (AllocationFailureEvent) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_FAILURE_QUEUE);
//
//        assertNotNull(allocationFailureEvent);
//        assertThat(allocationFailureEvent.getOrderId()).isEqualTo(savedBeerOrder.getId());
//    }
//
//    @Test
//    @Ignore
//    void testPartialAllocation() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//        beerOrder.setCustomerRef("partial-allocation");
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
//        });
//    }
//
//    @Test
//    @Ignore
//    void testValidationPendingToCancel() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//        beerOrder.setCustomerRef("dont-validate");
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.VALIDATION_PENDING, foundOrder.getOrderStatus());
//        });
//
//        beerOrderManager.cancelOrder(savedBeerOrder.getId());
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
//        });
//    }
//
//    @Test
//    @Ignore
//    void testAllocationPendingToCancel() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//        beerOrder.setCustomerRef("dont-allocate");
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());
//        });
//
//        beerOrderManager.cancelOrder(savedBeerOrder.getId());
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
//        });
//    }
//
//    @Test
//    @Ignore
//    void testAllocatedToCancel() throws JsonProcessingException {
//        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
//
//        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
//                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
//
//        BeerOrder beerOrder = createBeerOrder();
//
//        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
//        });
//
//        beerOrderManager.cancelOrder(savedBeerOrder.getId());
//
//        await().untilAsserted(() -> {
//            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
//            assertEquals(BeerOrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
//        });
//
//        DeallocateOrderRequest deallocateOrderRequest = (DeallocateOrderRequest) jmsTemplate.receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);
//
//        assertNotNull(deallocateOrderRequest);
//        assertThat(deallocateOrderRequest.getBeerOrderDto().getId()).isEqualTo(savedBeerOrder.getId());
//  }