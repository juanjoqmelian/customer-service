package com.dragosolutions.microservices.customer.repository;


import com.dragosolutions.microservices.customer.domain.CreditLog;

import java.util.List;

public interface CreditLogRepository {

    List<CreditLog> findByCustomer(String customerId);

    void save(CreditLog creditLog);
}
