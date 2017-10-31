package com.starkindustriesne.carbaresist;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories
public class CarbaResistDBConfig extends AbstractCouchbaseConfiguration {

    @Value("${couchbase.hosts}")
    private String[] hosts;

    @Value("${couchbase.bucket}")
    private String bucketName;

    @Value("${couchbase.password}")
    private String cbPassword;

    @Override
    protected List<String> getBootstrapHosts() {
        // TODO Auto-generated method stub
        return Arrays.asList(hosts);
    }

    @Override
    protected String getBucketName() {
        // TODO Auto-generated method stub
        return bucketName;
    }

    @Override
    protected String getBucketPassword() {
        // TODO Auto-generated method stub
        return cbPassword;
    }

}
