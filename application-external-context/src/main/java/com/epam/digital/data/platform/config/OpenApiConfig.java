/*
 * Copyright 2026 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Swagger UI security.
 */
@Configuration
public class OpenApiConfig {

  public static final String ACCESS_TOKEN_SECURITY_SCHEME = "accessToken";
  public static final String TOKEN_NAME = "X-Access-Token";

  /**
   * Creates and configures the OpenAPI bean for Swagger UI.
   *
   * @return configured OpenAPI instance
   */
  @Bean
  public OpenAPI openApi() {
    var info = new Info().title("Application external context service API")
        .description("api for managing application external contexts")
        .version("1.0");

    var components = new Components().addSecuritySchemes(
        ACCESS_TOKEN_SECURITY_SCHEME,
        new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name(TOKEN_NAME)
    );

    return new OpenAPI()
        .openapi("3.0.3")
        .info(info)
        .components(components);
  }
}

