package online.aleksdraka.ecommerceapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "online.aleksdraka.ecommerceapi.services")
@EnableAspectJAutoProxy
public class ProjectConfig {
}
