package com.redhat.fuse.boosters.rest.jdg;

import org.apache.camel.component.infinispan.InfinispanQueryBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

public class GenericQuery implements InfinispanQueryBuilder {

	private String queryString;

	public GenericQuery(String queryString) {
		super();

		this.queryString = queryString;
	}
	
	@Override
	public Query build(QueryFactory queryFactory) {
		Query q = queryFactory.create(queryString);

		return q;
	}
}