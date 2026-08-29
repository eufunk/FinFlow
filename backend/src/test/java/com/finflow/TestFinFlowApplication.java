package com.finflow;

import org.springframework.boot.SpringApplication;

public class TestFinFlowApplication {

	public static void main(String[] args) {
		SpringApplication.from(FinFlowApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
