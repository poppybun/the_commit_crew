package com.thecommitcrew;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.thecommitcrew.persistence.repository.InstrumentRepository;
import com.thecommitcrew.persistence.mapper.AccountMapper;
import com.thecommitcrew.persistence.mapper.InstrumentMapper;
import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.persistence.repository.OrderRepository;
import com.thecommitcrew.persistence.repository.PositionRepository;

@SpringBootTest
class MainTests {

  @MockitoBean
  private InstrumentRepository instrumentRepository;
  
  @MockitoBean
  private AccountRepository accountRepository;
  
  @MockitoBean
  private OrderRepository orderRepository;
  
  @MockitoBean
  private PositionRepository positionRepository;
  
  @MockitoBean
  private AccountMapper accountMapper;
  
  @MockitoBean
  private InstrumentMapper instrumentMapper;

  @Test
  void contextLoads() {
  }

}