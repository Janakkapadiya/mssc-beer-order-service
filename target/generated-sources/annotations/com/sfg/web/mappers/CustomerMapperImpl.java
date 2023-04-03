package com.sfg.web.mappers;

import com.sfg.domain.Customer;
import com.sfg.domain.Customer.CustomerBuilder;
import com.sfg.web.model.CustomerDto;
import com.sfg.web.model.CustomerDto.CustomerDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-03-31T16:11:19+0530",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 17.0.6 (Private Build)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CustomerDto customerToDto(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDtoBuilder customerDto = CustomerDto.builder();

        customerDto.id( customer.getId() );
        if ( customer.getVersion() != null ) {
            customerDto.version( customer.getVersion().intValue() );
        }
        customerDto.createdDate( dateMapper.asOffsetDateTime( customer.getCreatedDate() ) );
        customerDto.lastModifiedDate( dateMapper.asOffsetDateTime( customer.getLastModifiedDate() ) );

        return customerDto.build();
    }

    @Override
    public Customer dtoToCustomer(CustomerDto customerDto) {
        if ( customerDto == null ) {
            return null;
        }

        CustomerBuilder customer = Customer.builder();

        customer.id( customerDto.getId() );
        if ( customerDto.getVersion() != null ) {
            customer.version( customerDto.getVersion().longValue() );
        }
        customer.createdDate( dateMapper.asTimestamp( customerDto.getCreatedDate() ) );
        customer.lastModifiedDate( dateMapper.asTimestamp( customerDto.getLastModifiedDate() ) );

        return customer.build();
    }
}
