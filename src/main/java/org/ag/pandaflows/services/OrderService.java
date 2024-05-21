package org.ag.pandaflows.services;

import org.ag.pandaflows.models.OrderDetails;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrderService extends RouteBuilder {
    @Autowired
    private InventoryService inventoryService;



    @Override
    public void configure() throws Exception {
        from("activemq:queue:panda.order.request")
                .process(exchange -> {
                    OrderDetails receivedOrderDetails = exchange.getIn().getBody(OrderDetails.class);
                    if (Objects.isNull(receivedOrderDetails)) {
                        throw new IllegalArgumentException("Order is null!");
                    }
                    exchange.getIn().setBody(receivedOrderDetails);
                })
                .unmarshal().json()
                .process(this::placeOrder)
                .choice()
                    .when(Exchange::isFailed)
                        .log("Place Order failed!!")
                    .otherwise()
                        .marshal().json().to("activemq:queue:panda.order.response")
                    .end();

    }

    private void placeOrder(Exchange exchange) throws Exception {
      //  Order order = exchange.getIn().getBody(Order.class);
       /* if (inventoryService.checkInventory(order.getOrderQuantity())) {
            inventoryService.blockInventory(order.getOrderQuantity());
            boolean isSuccess = hedgeService.placeHedge(covertToPaperLot(order.getOrderQuantity()));
            if (isSuccess) {
                FillDetails filled = hedgeService.getFilledDetails(order);
                Boolean isDebitSuccess = inventoryService.debitInventory(filled.getHedgedQuantity);
                if (isDebitSuccess) {
                    boolean isPaid = paymentService.payFor(filled.getHedgedQuantity);
                    if (isPaid) {
                        order.setFulfilledQuantity(filled.getHedgedQuantity);
                        orderRepo.save(order);
                    } else {
                        throw new PaymentFailedException("Payment failed!");
                    }
                } else {
                    throw new InventoryDebitFailedException("Inventory debit failed: " + filled.getHedgedQuantity());
                }
            } else {
                throw new HedgeFailedException("Hedging failed for:" + order.getId());
            }
        } else {
            throw new InsufficientInventoryException("Inventory insufficient for order quantity:" + order.getOrderQuantity());
        }*/
    }
}
