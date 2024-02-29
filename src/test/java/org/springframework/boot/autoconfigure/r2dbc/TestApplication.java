package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TestApplication {
	
    public static void main(String[] args) 
    {
        SpringApplication.from(TestApplication::main).with(TestApplication.class).run(args);
    }
}
