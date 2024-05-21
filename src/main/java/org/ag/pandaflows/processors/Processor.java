package org.ag.pandaflows.processors;

import org.springframework.messaging.Message;

public interface Processor <T>{

    public Message<T> process(Message<T> message);
}
