package com.drago.microservices.customer.repository;


import com.drago.microservices.customer.domain.CreditLog;

import java.util.List;

public interface CreditLogRepository {

    List<CreditLog> findByCustomer(String customerId);

    void save(CreditLog creditLog);
}
