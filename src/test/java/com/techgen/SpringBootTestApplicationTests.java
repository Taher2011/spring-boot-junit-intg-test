package com.techgen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootTest
class SpringBootTestApplicationTests {

	@Test
	void contextLoads() {
	}

	@Bean
	public ObjectMapper objectMapper(){
		return new ObjectMapper();
	}


}
