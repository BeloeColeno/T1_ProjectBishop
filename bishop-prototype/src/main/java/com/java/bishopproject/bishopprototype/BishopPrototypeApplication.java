package com.java.bishopproject.bishopprototype;

import com.weyland.synthetic.config.SyntheticHumanAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SyntheticHumanAutoConfiguration.class)
public class BishopPrototypeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BishopPrototypeApplication.class, args);
	}

}
