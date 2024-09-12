package com.cg.service;

import com.cg.dto.CustomerDto;
import com.cg.dto.LoanDto;
import com.cg.dto.LoanStatus;
import com.cg.dto.TransactionDto;
import com.cg.entity.CibilScore;
import com.cg.entity.Loan;
import com.cg.entity.Transaction;
import com.cg.exception.LoanServiceException;
import com.cg.repository.CibilScoreRepository;
import com.cg.repository.LoanRepository;
import com.cg.repository.TransactionRepository;
import com.cg.security.TokenAuthenticationFilter;
import com.cg.security.TokenServiceProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Scope
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Autowired
    private TokenServiceProvider tokenServiceProvider;

    @Autowired
    private HttpServletRequest request;

    @Value("${app.customerServiceUrl}")
    private String customerServiceUrl;

    @Value(("${app.cibil.default.cibil-score}"))
    private Integer defaultCibilScore;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, LoanDto> template;

    @Autowired
    CibilScoreRepository cibilScoreRepository;


    @Override
    public LoanDto createLoan(LoanDto loanDto, HttpServletRequest httpServletRequest) {

        Claims tokenDetails = getTokenDetails(request);
        CustomerDto customerDto = checkCustomerAndGetId(loanDto.getPanNumber());

        if (ObjectUtils.isEmpty(customerDto.getAccount())) {
            throw new LoanServiceException("Customer account details are not found for this customer! Please add first then create the loan");
        }
        if (!(tokenDetails.get("role").equals("USER") && tokenDetails.get("id").equals(customerDto.getPanNumber()))) {
            throw new LoanServiceException("Unauthorized", 401);
        }
        Loan loan1 = loanRepository.getLoan(loanDto.getLoanType(), loanDto.getPanNumber());
        if (!ObjectUtils.isEmpty(loan1)) {
            throw new LoanServiceException("Loan already exist with this loan type %s and the status was %s".formatted(loan1.getLoanType(), loan1.getStatus()));
        }

        Loan loan = getLoanInfo(loanDto);
        loan.setStatus(LoanStatus.CREATED.name());
        loan.setCreatedBy((String) tokenDetails.get("id"));
        Loan save = loanRepository.save(loan);
        log.info("Loan Request Created Successfully");
        return objectMapper.convertValue(save, LoanDto.class);
    }

    @Override
    public ResponseEntity<?> getAllLoans() {
//        Claims tokenDetails = getTokenDetails(request);
//
//        if (!tokenDetails.get("role").equals("ADMIN")) {
//            throw new LoanServiceException("You don't have Permission to view all loan Details", 403);
//        }

        //if (status.equalsIgnoreCase("ALL")){
            List<Loan> loanList = loanRepository.findAll();
        List<LoanDto> loanDtos =loanList.stream().map(LoanServiceImpl::convertToLoanDto)
                    .collect(Collectors.toList());
        //}
//        else {
//            List<Loan> loans = loanRepository.getByLoanStatus(status);
//            loanDtos =loans.stream().map(LoanServiceImpl::convertToLoanDto)
//                    .collect(Collectors.toList());
//        }
        return ResponseEntity.ok(loanDtos);
    }


    @Override
    public LoanDto getLoanById(Long loanId) {

        Claims tokenDetails = getTokenDetails(request);
        if (!tokenDetails.get("role").equals("ADMIN")) {
            throw new LoanServiceException("You don't have Permission to view all loan Details", 403);
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanServiceException("Loan not found with id " + loanId));
        CustomerDto customerDto = checkCustomerAndGetId(loan.getPanNumber());
        LoanDto loanDto = objectMapper.convertValue(loan, LoanDto.class);
        loanDto.setCustomerDto(customerDto);
        return loanDto;
    }

    @Override
    public LoanDto updateLoan(Long loanId, LoanDto loanDto) {

        CustomerDto customerDto = checkCustomerAndGetId(loanDto.getPanNumber());
        Claims tokenDetails = getTokenDetails(request);

        boolean loanStatus = false;
        if (tokenDetails.get("role").equals("ADMIN")) {

            Loan loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new LoanServiceException("Loan not found with id " + loanId));

            if (loan.getStatus().equalsIgnoreCase("CANCELLED")) {
                throw new LoanServiceException("Hey! The loan already been cancelled stage");
            }

            boolean isEligible = checkCIBILScore(loanDto.getPanNumber());
            if(!isEligible){
                loanDto.setStatus("CANCELLED");
            }
            if (!loanDto.getStatus().equalsIgnoreCase(loan.getStatus())) {
                loanStatus = true;
            }
            if (loan.getStatus().equalsIgnoreCase("APPROVED")) {
                if (loanDto.getStatus().equalsIgnoreCase("CANCELLED")) {
                    throw new LoanServiceException("Hey! You cannot cancel the Approved loan");
                }
                throw new LoanServiceException("The loan is already approved");
            }
            if (loanDto.getStatus().equalsIgnoreCase("APPROVED")) {
                if (loan.getStatus().equalsIgnoreCase("IN_PROGRESS")) {
                    loan.setStatus(loanDto.getStatus());
                } else {
                    throw new LoanServiceException("The loan is not in process, Please start the loan before approving");
                }
            }
            loan.setStatus(loanDto.getStatus());
            loan.setUpdatedBy(String.valueOf(tokenDetails.get("id")));
            loan = loanRepository.save(loan);
            loanDto = new LoanDto(loan);

            if (loan.getStatus().equalsIgnoreCase("APPROVED")) {
                Transaction transaction = new Transaction();
                transaction.setAccountId(customerDto.getAccount().getId());
                transaction.setTransactionId(UUID.randomUUID().toString());
                transaction.setPanNumber(loanDto.getPanNumber());
                transaction.setUpdatedBy(Long.valueOf(String.valueOf(tokenDetails.get("id"))));
                transaction.setTransactionStatus("SUCCESS");
                transaction.setLoan(loan);
                Transaction transactions = transactionRepository.save(transaction);
                TransactionDto transactionDto = objectMapper.convertValue(transactions, TransactionDto.class);
                loanDto.setTransaction(transactionDto);
            }
            loanDto.setCustomerDto(customerDto);
            if (loanStatus) {
                template.send("messageService", loanDto);
            }
            if (loan.getStatus().equalsIgnoreCase("CANCELLED"))
            {
                throw new LoanServiceException("Your loan has been cancelled due to CIBIL score is not enough and the minimum CIBIL score should be 800",400);
            }
            else {
                return loanDto;
            }

        } else {
            throw new LoanServiceException("You don't have Permission to update the loan Details   ", 403);
        }
    }


    private Loan getLoanInfo(LoanDto loanDto) {
        Loan loan = new Loan();
        loan.setLoanType(loanDto.getLoanType());
        loan.setLoanAmount(loanDto.getLoanAmount());
        loan.setPanNumber(loanDto.getPanNumber());
        loan.setStatus(loanDto.getStatus());
       // loan.setCustomerId(loan.getCustomerId());
        return loan;

    }

    private Claims getTokenDetails(HttpServletRequest request) {
        String token = tokenAuthenticationFilter.getJwtFromRequest(request);
        Claims claims = tokenServiceProvider.extractAllClaims(token);
        return claims;
    }

    private CustomerDto checkCustomerAndGetId(String panNumber) {

        try {
            String token = tokenAuthenticationFilter.getJwtFromRequest(request);

            RestTemplate restTemplate = new RestTemplate();
            String url = "%s/customer/{panNumber}".formatted(customerServiceUrl);
            ResponseEntity<CustomerDto> response;

            // Create headers and add Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer %s".formatted(token));

            // Create an HttpEntity with the headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            try{
            response = restTemplate.exchange(
                    builder.buildAndExpand(panNumber).toUri(), // Expand path variable
                    HttpMethod.GET,
                    entity,
                    CustomerDto.class
            );
            // Handle response if needed
        } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
            // Rethrow the same exception type
            throw new LoanServiceException(e.getMessage(), 400);
        } catch (RestClientException e) {
            // Rethrow a more general RestClientException if needed
            throw new LoanServiceException(e.getMessage(), 400);
        }

            return response.getBody();
        } catch (HttpClientErrorException | UnknownContentTypeException | HttpServerErrorException e) {
            throw new LoanServiceException("Customer not found");
        }
    }

    private boolean checkCIBILScore(String panNumber)
    {
        CibilScore cibilScore = cibilScoreRepository.findByPanNumber(panNumber);
         return cibilScore.getCibilScore()>=defaultCibilScore;
    }

    private static LoanDto convertToLoanDto(Loan loan) {

        return new LoanDto(loan);
    }
}
