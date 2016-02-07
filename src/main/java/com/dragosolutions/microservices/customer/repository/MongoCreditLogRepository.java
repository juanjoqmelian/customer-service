package com.dragosolutions.microservices.customer.repository;


import com.dragosolutions.microservices.customer.domain.CreditLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MongoCreditLogRepository implements CreditLogRepository {

    private MongoTemplate mongoTemplate;

    public MongoCreditLogRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<CreditLog> findByCustomer(String customerId) {
        return mongoTemplate.find(Query.query(Criteria.where("customerId").is(customerId)), CreditLog.class);
    }

    @Override
    public void save(CreditLog creditLog) {
        mongoTemplate.save(creditLog);
    }
}
