package com.drago.microservices.customer.rules;

import com.drago.microservices.customer.server.MongoDB;
import org.junit.rules.ExternalResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

public class MongoRule extends ExternalResource {

    private static MongoDB mongoDB;
    private MongoTemplate mongoTemplate;
    private Class<?>[] collections;

    private String host = "localhost";
    private String dbName = "test";
    private int port = 27017;

    public MongoRule(Class<?>... collections) {
        this.collections = collections;
    }

    @Override
    protected void before() throws Throwable {
        mongoDB = new MongoDB(port);
        mongoDB.start();
        this.mongoTemplate = new MongoTemplate(new com.mongodb.MongoClient(host, port), dbName);
        this.dropCollections();
    }

    @Override
    protected void after() {
        mongoDB.stop();
    }

    public MongoRule host(String host) {

        this.host = host;
        return this;
    }

    public MongoRule port(int port) {

        this.port = port;
        return this;
    }

    public MongoRule dbName(String dbName) {

        this.dbName = dbName;
        return this;
    }

    public <T> T findOne(Query query, Class<T> entityClass) {
        return this.mongoTemplate.findOne(query, entityClass);
    }


    public void insert(Object objectToSave) {
        this.mongoTemplate.insert(objectToSave);
    }

    public <T> List<T> find(Query query, Class<T> entityClass) {

        return mongoTemplate.find(query, entityClass);
    }

    public <T> List<T> findAll(Class<T> entityClass) {

        return mongoTemplate.findAll(entityClass);
    }


    public void remove(Object objectToBeRemoved) {

        mongoTemplate.remove(objectToBeRemoved);
    }

    private void dropCollections() {

        checkNotNull(collections, "Collections can not be a null value");
        checkElementIndex(0, collections.length, "At least one collection must be specified. Collection");

        for (Class<?> collection : collections) {
            mongoTemplate.dropCollection(collection);
        }
    }
}
