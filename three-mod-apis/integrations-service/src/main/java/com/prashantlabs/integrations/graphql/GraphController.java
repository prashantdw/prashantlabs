package com.prashantlabs.integrations.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphController {
    @QueryMapping
    public Echo echo(@Argument("text") String text) {
        return new Echo(text, "graph-api");
    }

    @MutationMapping
    public String ping() {
        return "pong";
    }

    public record Echo(String text, String who) {
    }
}
