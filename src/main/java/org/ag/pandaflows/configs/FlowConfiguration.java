package org.ag.pandaflows.configs;

import jakarta.jms.ConnectionFactory;
import org.ag.pandaflows.flows.PandaFlow;
import org.ag.pandaflows.models.OrderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;


@Configuration
public class FlowConfiguration {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public PandaFlow flow() {
        PandaFlow.FlowBuilder<OrderDetails> tradeRouteBuilder = PandaFlow.builder();

        return tradeRouteBuilder
                .from("fromQueue")
                .process(message -> {
                    System.out.println("Trade procesing");
                    return message;
                })
                .process(message -> {
                    System.out.println("Position processed");
                    return message;
                })
                .to("toQueueName")
                .build();
    }


    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        // You can configure additional properties here
        return jmsTemplate;
    }
}
