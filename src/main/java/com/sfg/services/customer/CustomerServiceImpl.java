package com.sfg.services.customer;

import com.sfg.domain.Customer;
import com.sfg.repositories.CustomerRepository;
import com.sfg.web.mappers.CustomerMapper;
import com.sfg.web.model.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {

        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return new CustomerPagedList(customerPage
                .stream()
                .map(customerMapper::customerToDto)
                .collect(Collectors.toList()),
                PageRequest.of(customerPage.getPageable().getPageNumber(),
                        customerPage.getPageable().getPageSize()),
                customerPage.getTotalElements());
    }
}
