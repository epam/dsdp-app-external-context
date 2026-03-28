/*
 * Copyright 2026 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.client;

import com.epam.digital.data.platform.model.ApplicationStepInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.spring.EnableWireMock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ExternalContextService} Feign client.
 *
 * <p>Tests verify the Feign client's ability to communicate with the External Context Service
 * API using WireMock to simulate HTTP responses. All 10 Feign client methods are tested:
 * 5 for step management and 5 for variable management.
 */
@SpringBootTest(classes = ExternalContextServiceIT.TestConfig.class)
@TestPropertySource(properties = {
    "storage.externalContext.service.base-url=${wiremock.server.baseUrl}"
})
@EnableWireMock
class ExternalContextServiceIT {

  @Autowired
  private ExternalContextService externalContextService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String APPLICATION_ID = "test-app-123";
  private static final String STEP_ID = "step-001";
  private static final String VAR_KEY = "testVar";
  private static final String ACCESS_TOKEN = "test-access-token";

  @BeforeEach
  void setUp() {
    WireMock.resetAllRequests();
    WireMock.resetAllScenarios();
  }

  @Test
  void putStep_Success() throws JsonProcessingException {
    // Given
    var stepInfo = createTestStepInfo();
    var stepInfoJson = objectMapper.writeValueAsString(stepInfo);

    WireMock.stubFor(put(urlEqualTo("/" + APPLICATION_ID + "/steps/" + STEP_ID))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .withHeader("Content-Type", equalTo("application/json"))
        .withRequestBody(equalToJson(stepInfoJson))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")));

    // When
    externalContextService.putStep(APPLICATION_ID, STEP_ID, stepInfo, ACCESS_TOKEN);

    // Then
    WireMock.verify(putRequestedFor(urlEqualTo("/" + APPLICATION_ID + "/steps/" + STEP_ID)));
  }

  @Test
  void getStep_Success() throws JsonProcessingException {
    // Given
    var stepInfo = createTestStepInfo();
    var stepInfoJson = objectMapper.writeValueAsString(stepInfo);

    WireMock.stubFor(get(urlEqualTo("/" + APPLICATION_ID + "/steps/" + STEP_ID))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(stepInfoJson)));

    // When
    Optional<ApplicationStepInfo> result = externalContextService.getStep(
        APPLICATION_ID, STEP_ID, ACCESS_TOKEN);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(STEP_ID);
    assertThat(result.get().type()).isEqualTo(ApplicationStepInfo.StepType.FORM_SUBMISSION);
    assertThat(result.get().status()).isEqualTo(ApplicationStepInfo.Status.PENDING);
  }

  @Test
  void getAllSteps_Success() throws JsonProcessingException {
    // Given
    var stepInfo1 = createTestStepInfo();
    var stepInfo2 = new ApplicationStepInfo(
        "step-002",
        ApplicationStepInfo.StepType.SIGNATURE_UPLOAD,
        ApplicationStepInfo.Status.SUCCESS,
        null,
        Map.of("key", "value")
    );

    Map<String, ApplicationStepInfo> stepsMap = Map.of(
        STEP_ID, stepInfo1,
        "step-002", stepInfo2
    );
    var stepsJson = objectMapper.writeValueAsString(stepsMap);

    WireMock.stubFor(get(urlEqualTo("/" + APPLICATION_ID + "/steps"))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(stepsJson)));

    // When
    Map<String, ApplicationStepInfo> result = externalContextService.getAllSteps(
        APPLICATION_ID, ACCESS_TOKEN);

    // Then
    assertThat(result).hasSize(2)
        .containsKeys(STEP_ID, "step-002")
        .hasEntrySatisfying(STEP_ID,
            entry -> assertThat(entry.type()).isEqualTo(ApplicationStepInfo.StepType.FORM_SUBMISSION))
        .hasEntrySatisfying("step-002",
            entry -> assertThat(entry.type()).isEqualTo(ApplicationStepInfo.StepType.SIGNATURE_UPLOAD));
  }

  @Test
  void deleteStep_Success() {
    // Given
    WireMock.stubFor(delete(urlEqualTo("/" + APPLICATION_ID + "/steps/" + STEP_ID))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("true")));

    // When
    Boolean result = externalContextService.deleteStep(APPLICATION_ID, STEP_ID, ACCESS_TOKEN);

    // Then
    assertThat(result).isTrue();
    WireMock.verify(deleteRequestedFor(urlEqualTo("/" + APPLICATION_ID + "/steps/" + STEP_ID)));
  }

  @Test
  void deleteStepsContext_Success() {
    // Given
    WireMock.stubFor(delete(urlEqualTo("/" + APPLICATION_ID + "/steps"))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("true")));

    // When
    Boolean result = externalContextService.deleteStepsContext(APPLICATION_ID, ACCESS_TOKEN);

    // Then
    assertThat(result).isTrue();
    WireMock.verify(deleteRequestedFor(urlEqualTo("/" + APPLICATION_ID + "/steps")));
  }

  @Test
  void putVar_Success() throws JsonProcessingException {
    // Given
    var varValue = Map.of("name", "John Doe", "age", 30);
    var varValueJson = objectMapper.writeValueAsString(varValue);

    WireMock.stubFor(put(urlEqualTo("/" + APPLICATION_ID + "/vars/" + VAR_KEY))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .withHeader("Content-Type", equalTo("application/json"))
        .withRequestBody(equalToJson(varValueJson))
        .willReturn(aResponse()
            .withStatus(200)));

    // When
    externalContextService.putVar(APPLICATION_ID, VAR_KEY, varValue, ACCESS_TOKEN);

    // Then
    WireMock.verify(putRequestedFor(urlEqualTo("/" + APPLICATION_ID + "/vars/" + VAR_KEY)));
  }

  @Test
  void getVar_Success() throws JsonProcessingException {
    // Given
    var varValue = Map.of("name", "John Doe", "age", 30);
    var varValueJson = objectMapper.writeValueAsString(varValue);

    WireMock.stubFor(get(urlEqualTo("/" + APPLICATION_ID + "/vars/" + VAR_KEY))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(varValueJson)));

    // When
    Optional<String> result = externalContextService.getVar(
        APPLICATION_ID, VAR_KEY, ACCESS_TOKEN);

    // Then
    assertThat(result).isPresent()
        .contains(varValueJson);
  }

  @Test
  void getAllVars_Success() throws JsonProcessingException {
    // Given
    Map<String, Object> varsMap = new HashMap<>();
    varsMap.put("var1", "value1");
    varsMap.put("var2", 123);
    varsMap.put("var3", Map.of("nested", "object"));

    var varsJson = objectMapper.writeValueAsString(varsMap);

    WireMock.stubFor(get(urlEqualTo("/" + APPLICATION_ID + "/vars"))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(varsJson)));

    // When
    Map<String, Object> result = externalContextService.getAllVars(
        APPLICATION_ID, ACCESS_TOKEN);

    // Then
    assertThat(result).hasSize(3)
        .containsKeys("var1", "var2", "var3")
        .containsEntry("var1", "value1")
        .containsEntry("var2", 123)
        .containsEntry("var3", Map.of("nested", "object"));
  }

  @Test
  void deleteVar_Success() {
    // Given
    WireMock.stubFor(delete(urlEqualTo("/" + APPLICATION_ID + "/vars/" + VAR_KEY))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("true")));

    // When
    Boolean result = externalContextService.deleteVar(APPLICATION_ID, VAR_KEY, ACCESS_TOKEN);

    // Then
    assertThat(result).isTrue();
    WireMock.verify(deleteRequestedFor(urlEqualTo("/" + APPLICATION_ID + "/vars/" + VAR_KEY)));
  }

  @Test
  void deleteVarsContext_Success() {
    // Given
    WireMock.stubFor(delete(urlEqualTo("/" + APPLICATION_ID + "/vars"))
        .withHeader("X-Access-Token", equalTo(ACCESS_TOKEN))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("true")));

    // When
    Boolean result = externalContextService.deleteVarsContext(APPLICATION_ID, ACCESS_TOKEN);

    // Then
    assertThat(result).isTrue();
    WireMock.verify(deleteRequestedFor(urlEqualTo("/" + APPLICATION_ID + "/vars")));
  }

  // ==================== Helper Methods ====================

  private ApplicationStepInfo createTestStepInfo() {
    Map<String, Object> formData = Map.of("field1", "value1", "field2", "value2");
    Map<String, Object> evalContext = Map.of("userId", "user123", "role", "admin");

    var form = ApplicationStepInfo.Form.builder()
        .key("test-form")
        .token("form-token-123")
        .language("en")
        .evalContext(evalContext)
        .data(formData)
        .build();

    return new ApplicationStepInfo(
        STEP_ID,
        ApplicationStepInfo.StepType.FORM_SUBMISSION,
        ApplicationStepInfo.Status.PENDING,
        form,
        Map.of("description", "Test step", "priority", 1)
    );
  }

  // ==================== Test Configuration ====================

  /**
   * Test configuration for ExternalContextService integration tests.
   *
   * <p>Enables Feign clients and autoconfiguration for the test context.
   */
  @EnableAutoConfiguration
  @EnableFeignClients(basePackages = "com.epam.digital.data.platform.client")
  static class TestConfig {
  }
}
