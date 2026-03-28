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

package com.epam.digital.data.platform.service;

import com.epam.digital.data.platform.config.ExternalContextProperties;
import com.epam.digital.data.platform.model.ApplicationStep;
import com.epam.digital.data.platform.repository.ApplicationStepRepository;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationStepExternalContextServiceTest {

  @Mock
  private ApplicationStepRepository repository;

  private ApplicationStepExternalContextService service;

  private static final String APP_ID = "app-id";
  private static final String KEY = "key";
  private static final String VALUE =
      "{\"id\":\"step-id\",\"type\":\"validation\",\"status\":\"PENDING\",\"details\":{\"param1\":\"value1\"}}";

  @BeforeEach
  void setUp() {
    ExternalContextProperties properties = new ExternalContextProperties();
    properties.setTtl(Duration.ofMinutes(1));
    service = new ApplicationStepExternalContextService(repository, properties);
  }

  @Test
  void put_shouldCreateNewEntity_whenNotExists() {
    when(repository.findByApplicationIdAndKey(APP_ID, KEY)).thenReturn(Optional.empty());

    service.put(APP_ID, KEY, VALUE);

    verify(repository).save(any(ApplicationStep.class));
  }

  @Test
  void put_shouldCreateNewEntity_whenExists() {
    ApplicationStep existing = new ApplicationStep();
    when(repository.findByApplicationIdAndKey(APP_ID, KEY)).thenReturn(Optional.of(existing));

    service.put(APP_ID, KEY, VALUE);

    verify(repository).save(existing);
  }

  @Test
  void get_shouldReturnValue_whenExists() {
    ApplicationStep applicationStep = new ApplicationStep();
    applicationStep.setValue(VALUE);

    when(repository.findByApplicationIdAndKey(APP_ID, KEY)).thenReturn(Optional.of(applicationStep));

    Optional<Object> result = service.get(APP_ID, KEY);

    assertTrue(result.isPresent());
    assertEquals(VALUE, result.get());
  }

  @Test
  void getAll_shouldReturnMap() {
    ApplicationStep applicationStep1 = new ApplicationStep();
    applicationStep1.setKey("key1");
    applicationStep1.setValue("value1");

    ApplicationStep applicationStep2 = new ApplicationStep();
    applicationStep2.setKey("key2");
    applicationStep2.setValue("value2");

    when(repository.findAllByApplicationId(APP_ID)).thenReturn(List.of(applicationStep1, applicationStep2));

    Map<String, Object> result = service.getAll(APP_ID);

    assertEquals(2, result.size());
    assertEquals("value1", result.get("key1"));
  }

  @Test
  void delete_shouldReturnTrue_whenDeleted() {
    when(repository.deleteByApplicationIdAndKey(APP_ID, KEY)).thenReturn(1L);

    assertTrue(service.delete(APP_ID, KEY));
  }

  @Test
  void delete_shouldReturnFalse_whenDeleted() {
    when(repository.deleteByApplicationIdAndKey(APP_ID, KEY)).thenReturn(0L);

    assertFalse(service.delete(APP_ID, KEY));
  }

  @Test
  void deleteContext_shouldReturnTrue_whenDeleted() {
    when(repository.deleteAllByApplicationId(APP_ID)).thenReturn(4L);

    assertTrue(service.deleteContext(APP_ID));
  }

  @Test
  void deleteContext_shouldReturnFalse_whenNothingDeleted() {
    when(repository.deleteAllByApplicationId(APP_ID)).thenReturn(0L);

    assertFalse(service.deleteContext(APP_ID));
  }

}