package com.jasik.momsnaggingapi.infra.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Arrays;

import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.converters.models.MonetaryAmount;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
//        SpringDocUtils.getConfig().replaceWithClass(LocalTime.class, String.class);
        Info info = new Info().title("MomsNagging API").version("v1")
                .description("Spring Boot를 이용한 MomsNagging API입니다.")
                .termsOfService("http://swagger.io/terms/")
                .contact(new Contact().name("team.jasik").url("https://jasik/github.com//").email("team.jasik@gmail.com"))
                .license(new License().name("Apache License Version 2.0").url("http://www.apache.org/licenses/LICENSE-2.0"));

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(Arrays.asList(securityRequirement))
                .info(info);
    }


}
