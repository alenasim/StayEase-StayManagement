package com.laioffer.staybooking.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/*this configuration class defines a bean that sets up a connection to an Elasticsearch cluster using the RestHighLevelClient.
The configuration values are read from external properties files, and the client is created with the appropriate connection settings,
including the address and authentication credentials. This setup allows other parts of the Spring application to use
the RestHighLevelClient to interact with the Elasticsearch cluster.*/
@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.address}")         //field injection
    private String elasticsearchAddress;

    @Value("${elasticsearch.username}")
    private String elasticsearchUsername;

    @Value("${elasticsearch.password}")
    private String elasticsearchPassword;

    /*This method defines a bean named elasticsearchClient of type RestHighLevelClient.
    This bean will be managed by the Spring container and can be used to interact with the Elasticsearch cluster.
    The ClientConfiguration is created using the provided Elasticsearch address and authentication credentials.
    The RestClients.create(clientConfiguration).rest() method call creates an instance of the RestHighLevelClient based on the provided configuration.*/
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticsearchAddress)
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
