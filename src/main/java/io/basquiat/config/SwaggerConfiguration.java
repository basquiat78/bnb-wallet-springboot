package io.basquiat.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

/**
 * 
 * common swagger configuration for WebFlux
 * 
 * created by basquiat
 *
 */
@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfiguration {
	
	@Bean
	public Docket api() { 
		return new Docket(DocumentationType.SWAGGER_2).select()
													  .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
													  .build()
													  .genericModelSubstitutes(Optional.class, Flux.class, Mono.class);
	}

}
