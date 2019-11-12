package congvanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CongVanServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CongVanServiceApplication.class, args);
    }
}
