package com.thecommitcrew;

import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.persistence.repository.InstrumentRepository;
import com.thecommitcrew.persistence.repository.OrderRepository;
import com.thecommitcrew.persistence.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class MainTests {

  @MockitoBean
  private AccountRepository accountRepository;

  @MockitoBean
  private PositionRepository positionRepository;

  @MockitoBean
  private OrderRepository orderRepository;

  @MockitoBean
  private InstrumentRepository instrumentRepository;

  @Test
  void contextLoads() {
  }

}