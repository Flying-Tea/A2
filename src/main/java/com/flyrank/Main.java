package com.flyrank;

import org.springframework.boot.SpringApplication; // Import the SpringApplication class from the Spring Boot framework
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Comments are for my own future reference, to help me understand the code better and remember what each part does.

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args); // Start the Spring Boot application by invoking the run method of the SpringApplication class, passing in the Main class and command-line arguments
    }
    
    
}