package com.thecommitcrew;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.thecommitcrew.persistence.repository.InstrumentRepository;
import com.thecommitcrew.persistence.repository.AccountRepository;

@SpringBootTest()
class MainTests {

  @MockBean
  private InstrumentRepository instrumentRepository;
  
  @MockBean
  private AccountRepository accountRepository;

  @Test
  void contextLoads() {
  }

}