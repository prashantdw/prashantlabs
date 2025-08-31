package com.prashantlabs.accounts.service.impl;

import com.prashantlabs.accounts.dto.AccountsDto;
import com.prashantlabs.accounts.dto.CardsDto;
import com.prashantlabs.accounts.dto.CustomerDetailsDto;
import com.prashantlabs.accounts.dto.LoansDto;
import com.prashantlabs.accounts.entity.Accounts;
import com.prashantlabs.accounts.entity.Customer;
import com.prashantlabs.accounts.exception.ResourceNotFoundException;
import com.prashantlabs.accounts.mapper.AccountsMapper;
import com.prashantlabs.accounts.mapper.CustomerMapper;
import com.prashantlabs.accounts.repository.AccountsRepository;
import com.prashantlabs.accounts.repository.CustomerRepository;
import com.prashantlabs.accounts.service.ICustomersService;
import com.prashantlabs.accounts.service.client.CardsFeignClient;
import com.prashantlabs.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if(null != loansDtoResponseEntity) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if(null != cardsDtoResponseEntity) {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }

        return customerDetailsDto;

    }
}
