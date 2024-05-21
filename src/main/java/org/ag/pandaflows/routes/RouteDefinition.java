package org.ag.pandaflows.routes;

import lombok.Getter;
import lombok.Setter;
import org.ag.pandaflows.processors.Processor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RouteDefinition<T> {

    private String fromQueueName;

    private String toQueueName;

    private List<Processor<T>> processors = new ArrayList<>();
}
