package com.tet.tet_app;

import com.tet.tet_app.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class TetAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TetAppApplication.class, args);
	}

}
