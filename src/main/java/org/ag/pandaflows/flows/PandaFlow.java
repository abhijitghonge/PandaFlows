package org.ag.pandaflows.flows;

import jakarta.jms.JMSException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.Message;
import jakarta.jms.MessageListener;
import org.ag.pandaflows.processors.Processor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;


public class PandaFlow<T>  {


    private String fromQueueName;

    private String toQueueName;

    private List<Processor<T>> processors;



    private PandaFlow(String fromQueueName, String toQueueName, List<Processor<T>> processors) {
        this.fromQueueName = fromQueueName;
        this.toQueueName = toQueueName;
        this.processors = processors;
    }

    public  static <U> FlowBuilder<U> builder(){
        return new FlowBuilder<U>();
    }



    public static class FlowBuilder<T> implements ApplicationContextAware{
        public String fromQueueName;
        public String toQueueName;
        public List<Processor<T>> processors = new ArrayList<>();


        public JmsTemplate jmsTemplate;

        public FlowBuilder<T> from(String fromQueueName) {
            this.fromQueueName = fromQueueName;
            return this;
        }

        public FlowBuilder<T> to(String toQueueName) {
            this.toQueueName = toQueueName;
            return this;
        }

        public FlowBuilder<T> process(Processor<T> processor){
            this.processors.add(processor);
            return this;
        }

        public PandaFlow<T> build() {
            PandaFlow<T> flow =  new PandaFlow<>(fromQueueName, toQueueName, processors);
            //create message listener and define the flow
            MessageListener listener = message -> {
                try {
                    Message<T> jmsMessage =  message.getBody(Message.class);
                    for (Processor<T> processor : processors) {
                        jmsMessage = processor.process(jmsMessage);
                    }
                    this.jmsTemplate.convertAndSend(toQueueName, jmsMessage);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }


            };



            DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
            container.setDestinationName(fromQueueName);
            container.start();
            return flow;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.jmsTemplate = applicationContext.getBean(JmsTemplate.class);
        }
    }
}
