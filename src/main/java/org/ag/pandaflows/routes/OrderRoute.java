package org.ag.pandaflows.routes;

import org.ag.pandaflows.models.OrderDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class OrderRoute extends RouteBuilder<OrderDetails> {

    @Value("${panda.order.request}")
    private String orderRequestQueue;

    @Value("${panda.order.response}")
    private String orderResponseQueue;

    @Override
    public void configure() {
        from(orderRequestQueue)
                .process(message -> {
                    System.out.println("processor 1");
                    return message;
                })
                .process(message -> {
                    System.out.println("processor 2");
                    return message;
                })
                .to(orderResponseQueue)
                .end();

    }

}
