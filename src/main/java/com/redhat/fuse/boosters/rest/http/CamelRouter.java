package com.redhat.fuse.boosters.rest.http;

import com.redhat.fuse.boosters.rest.jdg.Person;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * A simple Camel REST DSL route that implement the greetings service.
 * 
 */
@Component
public class CamelRouter extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        // @formatter:off
        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Greeting REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
//                .apiProperty("schemes", "")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .bindingMode(RestBindingMode.json);
        
        rest("/greetings/").description("Greeting to {name}")
            .get("/{name}").outType(Greetings.class)
                .route().routeId("greeting-api")
                .to("direct:greetingsImpl");

        from("direct:greetingsImpl").description("Greetings REST service implementation route")
            .streamCaching()
            .to("bean:greetingsService?method=getGreetings");     
        // @formatter:on


        // Insert a new Person into JDG
        rest("/person/").put("/{id}").type(Person.class).to("direct:insertPerson");

        from("direct:insertPerson")
            .log("About to insert Person ${headers.id}")
            .setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.PUT))
            .setHeader(InfinispanConstants.KEY, simple("${headers.id}"))
            .setHeader(InfinispanConstants.VALUE, simple("${body}"))
            .to("infinispan://localhost?cacheContainer=#cacheManager")
            .log("Inserted Person ${headers.id}")
            .setBody(simple("${header.CamelInfinispanOperationResult}"));

        
        // Get an Person from JDG by Id
        rest("/person/").get("/{id}").to("direct:getPerson");

        from("direct:getPerson")
            .log("About to get Person ${headers.id}")
            .setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.GET))
            .setHeader(InfinispanConstants.KEY, simple("${headers.id}"))
            .to("infinispan://localhost?cacheContainer=#cacheManager")
            .log("Got Person ${headers.id}")
            .setBody(simple("${header.CamelInfinispanOperationResult}"));


        // Query JDG for an Person using a query
        rest("/person/").get("/query/{queryString}").to("direct:queryPerson");

        from("direct:queryPerson")
            .log("About to query Person using query ${headers.queryString}")
            .setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.QUERY))
            .setHeader(InfinispanConstants.QUERY_BUILDER).method("generateQuery", "getBuilder")
            .to("infinispan://localhost?cacheContainer=#cacheManager")
            .log("Queried Person")
            .setBody(simple("${header.CamelInfinispanOperationResult}"));
    }
}