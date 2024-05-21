package org.ag.pandaflows.routes;


import jakarta.annotation.PostConstruct;
import jakarta.jms.JMSException;
import jakarta.jms.MessageListener;
import org.ag.pandaflows.processors.Processor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.Message;

public abstract class RouteBuilder<T> implements ApplicationContextAware {

    protected RouteDefinition<T> route;

    public abstract void configure() ;
    private JmsTemplate jmsTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        jmsTemplate = applicationContext.getBean(JmsTemplate.class);
    }


    @PostConstruct
    public void start() throws JMSException {
        this.configure();
        // You can access other Spring beans or application context here if needed
    }

    public RouteBuilder<T> from(String fromQueueName) {
        this.route = new RouteDefinition<T>();
        this.route.setFromQueueName(fromQueueName);
        return this;
    }


    public RouteBuilder<T> to(String toQueueName) {
        this.route.setToQueueName(toQueueName);
        return this;
    }

    public RouteBuilder<T> process(Processor<T> processor){
        this.route.getProcessors().add(processor);
        return this;
    }

    protected void end() {

        //create message listener and define the flow
        MessageListener listener = message -> {
            try {
                Message<T> jmsMessage =  message.getBody(Message.class);
                for (Processor<T> processor : this.route.getProcessors()) {
                    jmsMessage = processor.process(jmsMessage);
                }
                this.jmsTemplate.convertAndSend(this.route.getToQueueName(), jmsMessage);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        };

        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(jmsTemplate.getConnectionFactory());
        container.setDestinationName(this.route.getFromQueueName());
        container.start();
    }


}
