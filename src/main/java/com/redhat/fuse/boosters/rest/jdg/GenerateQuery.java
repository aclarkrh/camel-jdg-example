package com.redhat.fuse.boosters.rest.jdg;

import java.beans.IntrospectionException;

import org.apache.camel.Exchange;
import org.apache.camel.component.infinispan.InfinispanQueryBuilder;
import org.springframework.stereotype.Component;

@Component("generateQuery")
public class GenerateQuery {

	public InfinispanQueryBuilder getBuilder(Exchange ex) throws ClassNotFoundException, IntrospectionException {

		InfinispanQueryBuilder qb = new GenericQuery(ex.getIn().getHeader("queryString",String.class));

		return qb;
	}
}