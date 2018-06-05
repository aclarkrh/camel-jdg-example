package com.redhat.fuse.boosters.rest.jdg;

import java.io.IOException;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@Configuration
public class CacheManagerConfig {

    String hostname = "datagrid-app-hotrod.camel-jdg.svc.cluster.local";
    int port = 11333;
    String saslServerName = "jdg-server";
    String jdgUser = "admin";
    String jdgPass = "password";

    @Bean
    public RemoteCacheManager cacheManager() throws IOException {

        ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
        clientBuilder.addServer()
            .host(hostname)
            .port(port)
        .marshaller(new ProtoStreamMarshaller())
        .security()
            .authentication()
            .enable()
            .serverName(saslServerName)
            .saslMechanism("DIGEST-MD5")
            .callbackHandler(new LoginCallbackHandler(jdgUser, "ApplicationRealm", jdgPass.toCharArray()));
    
        RemoteCacheManager remoteCacheManager =  new RemoteCacheManager(clientBuilder.build());

        SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(remoteCacheManager);

		ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();

		//create a protobuf schema file from the annotated class. Protobuf marshallers and unmarshallers are generated automtically
		String eventSchema = protoSchemaBuilder
				.fileName("event.proto")
				.packageName("example")
				.addClass(Person.class)
				.build(ctx);

		//register the protobuf schema in the remote cache
		RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
		metadataCache.put("event.proto", eventSchema);

		//check if there is an error with the schemas
		String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
		if (errors != null) {
			throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
		}

		return remoteCacheManager;
    }
}
