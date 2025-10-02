package com.example.graalvm.config;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Function;

@Configuration
@Profile("aws")
@Slf4j
public class LambdaFunctionConfig {
    
    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> handleRequest() {
        return request -> {
            log.info("Received Lambda request: {} {}", request.getHttpMethod(), request.getPath());
            
            // For now, return a simple response
            // In a full implementation, this would route to Spring MVC
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody("{\"message\": \"Hello from Lambda!\", \"path\": \"" + request.getPath() + "\"}");
            response.getHeaders().put("Content-Type", "application/json");
            
            return response;
        };
    }
}
