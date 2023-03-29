package com.sfg.services.customer;

import com.sfg.web.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;


public interface CustomerService {
    CustomerPagedList listCustomers(Pageable pageable);
}
