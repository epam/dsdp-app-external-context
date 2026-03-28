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

package com.epam.digital.data.platform.controller;

import com.epam.digital.data.platform.repository.ApplicationContextRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
    "platform.security.enabled=false"
})
abstract class BaseExternalContextControllerIT<T> {

  private static final String BASE_PATH = "/v1alpha/applications";
  private static final String APP_ID = "test-app-id";
  private static final String TEST_RESOURCE_ID = "test-resource-id";
  private static final int LONG_KEY_COUNT = 255;
  private static final String UNICODE_CONTENT = "{\"text\":\"Привіт, 世界\"}";
  private static final String RESOURCE_2_ID = "resource-2";
  private static final String RESOURCE_1_ID = "resource-1";

  @Autowired
  protected MockMvc mockMvc;

  @AfterEach
  void cleanUp() {
    getRepository().deleteAll();
  }

  protected abstract ApplicationContextRepository<T> getRepository();

  protected abstract String getResourcePath();

  protected abstract String getResourceIdName();

  @Test
  void put_shouldSaveAndRetrieveSuccessfully() throws Exception {
    String content = createTestJson("name", "test-name", "value", "test-value");
    putResource(APP_ID, TEST_RESOURCE_ID, content);
    verifyResourceContent(APP_ID, TEST_RESOURCE_ID, content);
  }

  @Test
  void put_shouldUpdateExisting() throws Exception {
    String initialContent = createTestJson("status", "initial");
    String updatedContent = createTestJson("status", "updated");

    putResource(APP_ID, TEST_RESOURCE_ID, initialContent);
    putResource(APP_ID, TEST_RESOURCE_ID, updatedContent);
    verifyResourceContent(APP_ID, TEST_RESOURCE_ID, updatedContent);
  }

  @Test
  void put_shouldReturnBadRequest_whenBadValue() throws Exception {
    verifyPutReturnsBadRequest(APP_ID, TEST_RESOURCE_ID, "null");
  }

  @Test
  void put_shouldHandleVeryLongKey() throws Exception {
    String longKey = "a".repeat(LONG_KEY_COUNT);
    String content = createTestJson("field", "value");

    putResource(APP_ID, longKey, content);
    verifyResourceContent(APP_ID, longKey, content);
  }

  @Test
  void put_shouldHandleUnicodeCharacters() throws Exception {
    String unicodeContent = UNICODE_CONTENT;
    putResource(APP_ID, TEST_RESOURCE_ID, unicodeContent);
    verifyResourceContent(APP_ID, TEST_RESOURCE_ID, unicodeContent);
  }

  @Test
  void put_shouldHandleLargeJsonObject() throws Exception {
    StringBuilder largeJson = new StringBuilder("{");
    for (int i = 0; i < 100; i++) {
      if (i > 0) largeJson.append(",");
      largeJson.append("\"field").append(i).append("\":\"value").append(i).append("\"");
    }
    largeJson.append("}");

    String content = largeJson.toString();
    putResource(APP_ID, TEST_RESOURCE_ID, content);
    verifyResourceContent(APP_ID, TEST_RESOURCE_ID, content);
  }

  @Test
  void get_shouldReturnNotFound_whenContextDoesNotExist() throws Exception {
    verifyResourceIsNull("non-existent-app", TEST_RESOURCE_ID);
  }

  @Test
  void get_shouldReturnNull_whenDoesNotExist() throws Exception {
    putResource(APP_ID, "resource-1", createTestJson("field", "value1"));

    verifyResourceIsNull(APP_ID, "non-existent-resource");
  }

  @Test
  void operationsSequence_putGetDeleteGet_shouldWorkCorrectly() throws Exception {
    String content = createTestJson("status", "active");

    putResource(APP_ID, TEST_RESOURCE_ID, content);
    putResource(APP_ID, "another-resource", createTestJson("field", "value"));
    verifyResourceContent(APP_ID, TEST_RESOURCE_ID, content);

    deleteResource(APP_ID, TEST_RESOURCE_ID, true);

    verifyResourceIsNull(APP_ID, TEST_RESOURCE_ID);
  }

  @Test
  void getAll_shouldReturnAllForApplication() throws Exception {
    putResource(APP_ID, RESOURCE_1_ID, createTestResourceJson(RESOURCE_1_ID));
    putResource(APP_ID, RESOURCE_2_ID, createTestResourceJson(RESOURCE_2_ID));

    mockMvc.perform(get(buildResourceUrl(), APP_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.['" + RESOURCE_1_ID + "'].id", Matchers.is(RESOURCE_1_ID)))
        .andExpect(jsonPath("$.['" + RESOURCE_2_ID + "'].id", Matchers.is(RESOURCE_2_ID)));
  }

  @Test
  void getAll_shouldReturnNotFound_whenContextDoesNotExist() throws Exception {
    verifyGetAllReturnsEmpty("non-existent-app");
  }

  @Test
  void deleteOneFromContextWhereMultipleExists_shouldRemoveAndReturnTrue() throws Exception {
    putResource(APP_ID, TEST_RESOURCE_ID, createTestJson("field", "value1"));
    putResource(APP_ID, "another-resource", createTestJson("field", "value2"));

    deleteResource(APP_ID, TEST_RESOURCE_ID, true);

    verifyResourceIsNull(APP_ID, TEST_RESOURCE_ID);
  }

  @Test
  void delete_shouldReturnFalse_whenDoesNotExist() throws Exception {
    deleteResource(APP_ID, "non-existent-resource", false);
  }

  @Test
  void delete_shouldNotAffectOtherResourcesInSameContext() throws Exception {
    putMultipleResources(APP_ID, "resource-1", "resource-2", "resource-3");

    deleteResource(APP_ID, "resource-2", true);

    verifyResourceContent(APP_ID, "resource-1", createTestResourceJson("resource-1"));
    verifyResourceContent(APP_ID, "resource-3", createTestResourceJson("resource-3"));
  }

  @Test
  void delete_shouldReturnTrueOnlyOnFirstDeletion() throws Exception {
    putResource(APP_ID, TEST_RESOURCE_ID, createTestJson("field", "value"));

    deleteResource(APP_ID, TEST_RESOURCE_ID, true);
    deleteResource(APP_ID, TEST_RESOURCE_ID, false);
  }

  @Test
  void deleteContext_shouldRemoveAllAndReturnTrue() throws Exception {
    putMultipleResources(APP_ID, "resource-1", "resource-2");

    deleteContext(APP_ID, true);

    verifyGetAllReturnsEmpty(APP_ID);
  }

  @Test
  void deleteContext_shouldReturnFalse_whenContextDoesNotExist() throws Exception {
    deleteContext("non-existent-app", false);
  }

  @Test
  void deleteContext_shouldRemoveAllResourcesButNotAffectOtherContexts() throws Exception {
    String anotherAppId = "another-app-id";
    putResource(APP_ID, "resource-1", createTestJson("field", "value1"));
    putResource(APP_ID, "resource-2", createTestJson("field", "value2"));
    putResource(anotherAppId, "resource-1", createTestJson("field", "other-value"));

    deleteContext(APP_ID, true);

    verifyResourceContent(anotherAppId, "resource-1", createTestJson("field", "other-value"));
  }

  @Test
  void deleteContext_shouldReturnTrueOnlyOnFirstDeletion() throws Exception {
    putResource(APP_ID, TEST_RESOURCE_ID, createTestJson("field", "value"));

    deleteContext(APP_ID, true);
    deleteContext(APP_ID, false);
  }

  @Test
  void put_WithEmptyBody_shouldReturnBadRequest() throws Exception {
    verifyPutReturnsBadRequest(APP_ID, TEST_RESOURCE_ID, "");
  }

  private String buildResourceUrl(String... segments) {
    StringBuilder url = new StringBuilder(BASE_PATH + "/{id}/" + getResourcePath());
    for (String segment : segments) {
      url.append("/").append(segment);
    }
    return url.toString();
  }

  private String createTestJson(String... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Key-value pairs must be even");
    }

    StringBuilder json = new StringBuilder("{");
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      if (i > 0) json.append(",");
      json.append("\"").append(keyValuePairs[i]).append("\":\"")
          .append(keyValuePairs[i + 1]).append("\"");
    }
    json.append("}");
    return json.toString();
  }

  private String createTestResourceJson(String resourceId) {
    return createTestJson("id", resourceId);
  }

  private void putResource(String appId, String resourceId, String content) throws Exception {
    mockMvc.perform(put(buildResourceUrl("{" + getResourceIdName() + "}"), appId, resourceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isOk());
  }

  private void verifyResourceContent(String appId, String resourceId, String expectedContent) throws Exception {
    mockMvc.perform(get(buildResourceUrl("{" + getResourceIdName() + "}"), appId, resourceId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedContent));
  }

  private void verifyResourceIsNull(String appId, String resourceId) throws Exception {
    mockMvc.perform(get(buildResourceUrl("{" + getResourceIdName() + "}"), appId, resourceId))
        .andExpect(status().isOk())
        .andExpect(content().string("null"));
  }

  private void deleteResource(String appId, String resourceId, boolean expectedResult) throws Exception {
    mockMvc.perform(delete(buildResourceUrl("{" + getResourceIdName() + "}"), appId, resourceId))
        .andExpect(status().isOk())
        .andExpect(content().string(String.valueOf(expectedResult)));
  }

  private void deleteContext(String appId, boolean expectedResult) throws Exception {
    mockMvc.perform(delete(buildResourceUrl(), appId))
        .andExpect(status().isOk())
        .andExpect(content().string(String.valueOf(expectedResult)));
  }

  private void verifyGetAllReturnsEmpty(String appId) throws Exception {
    mockMvc.perform(get(buildResourceUrl(), appId))
        .andExpect(status().isOk())
        .andExpect(content().string("{}"));
  }

  private void verifyPutReturnsBadRequest(String appId, String resourceId, String content) throws Exception {
    mockMvc.perform(put(buildResourceUrl("{" + getResourceIdName() + "}"), appId, resourceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isBadRequest());
  }

  private void putMultipleResources(String appId, String... resourceIds) throws Exception {
    for (String resourceId : resourceIds) {
      putResource(appId, resourceId, createTestResourceJson(resourceId));
    }
  }
}


