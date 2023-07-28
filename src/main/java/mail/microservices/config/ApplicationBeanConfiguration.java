package mail.microservices.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationBeanConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public DateAndTimeFormat dateAndTimeFormat() {
        return new DateAndTimeFormat();
    }
    @Bean
    public JavaMailSenderImpl getJavaMailSender() {
      return new JavaMailSenderImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GetPathBySystem getPathBySystem() {
        return new GetPathBySystem();
    }

    @Bean
    public JsonObject jsonObject() {
        return new JsonObject();
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
