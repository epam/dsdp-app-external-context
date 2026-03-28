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
import java.util.Map;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for interacting with the External Context Service API.
 *
 * <p>Provides REST client methods for managing application steps and variables in external context storage.
 * This client handles both step-based context (for process flow management) and variable-based context
 * (for storing arbitrary key-value data).
 *
 * <p>All operations require an access token for authentication and authorization.
 *
 * @see ApplicationStepInfo
 */
@FeignClient(
    name = "externalContextService",
    url = "${storage.externalContext.service.base-url}"
)
public interface ExternalContextService {

  /**
   * Creates or updates a step in the application's external context.
   *
   * <p>Stores step information including status, type, form data, and additional details.
   * If the step already exists, it will be updated; otherwise, a new step is created.
   *
   * @param id the unique application identifier
   * @param stepId the unique step identifier within the application
   * @param stepInfo the step information to store or update
   * @param accessToken the access token for authentication
   */
  @PutMapping(value = "/{id}/steps/{stepId}", consumes = "application/json", produces =
      "application/json")
  void putStep(@PathVariable("id") String id,
               @PathVariable("stepId") String stepId,
               @RequestBody ApplicationStepInfo stepInfo,
               @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Retrieves a specific step from the application's external context.
   *
   * @param id the unique application identifier
   * @param stepId the unique step identifier within the application
   * @param accessToken the access token for authentication
   * @return an Optional containing the step information if found, or empty if the step doesn't exist
   */
  @GetMapping(value = "/{id}/steps/{stepId}", produces = "application/json")
  Optional<ApplicationStepInfo> getStep(@PathVariable("id") String id,
                                        @PathVariable("stepId") String stepId,
                                        @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Retrieves all steps for the specified application from external context.
   *
   * @param id the unique application identifier
   * @param accessToken the access token for authentication
   * @return a map of step IDs to their corresponding step information
   */
  @GetMapping(value = "/{id}/steps", produces = "application/json")
  Map<String, ApplicationStepInfo> getAllSteps(@PathVariable("id") String id,
                                               @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Deletes a specific step from the application's external context.
   *
   * @param id the unique application identifier
   * @param stepId the unique step identifier to delete
   * @param accessToken the access token for authentication
   * @return true if the step was deleted, false if it didn't exist
   */
  @DeleteMapping(value = "/{id}/steps/{stepId}", produces = "application/json")
  Boolean deleteStep(@PathVariable("id") String id,
                     @PathVariable("stepId") String stepId,
                     @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Deletes all steps for the specified application from external context.
   *
   * @param id the unique application identifier
   * @param accessToken the access token for authentication
   * @return true if steps were deleted, false if no steps existed
   */
  @DeleteMapping(value = "/{id}/steps", produces = "application/json")
  Boolean deleteStepsContext(@PathVariable("id") String id,
                             @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Creates or updates a variable in the application's external context.
   *
   * <p>Variables provide flexible key-value storage for arbitrary application data.
   * If the variable already exists, it will be updated; otherwise, a new variable is created.
   *
   * @param id the unique application identifier
   * @param key the variable key identifier
   * @param value the variable value (can be any JSON-serializable object)
   * @param accessToken the access token for authentication
   */
  @PutMapping(value = "/{id}/vars/{key}", consumes = "application/json")
  void putVar(@PathVariable("id") String id,
              @PathVariable("key") String key,
              @RequestBody Object value,
              @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Retrieves a specific variable from the application's external context.
   *
   * @param id the unique application identifier
   * @param key the variable key identifier
   * @param accessToken the access token for authentication
   * @return an Optional containing the variable value as JSON string if found, or empty if the variable doesn't exist
   */
  @GetMapping(value = "/{id}/vars/{key}", produces = "application/json")
  Optional<String> getVar(@PathVariable("id") String id,
                          @PathVariable("key") String key,
                          @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Retrieves all variables for the specified application from external context.
   *
   * @param id the unique application identifier
   * @param accessToken the access token for authentication
   * @return a map of variable keys to their corresponding values
   */
  @GetMapping(value = "/{id}/vars", produces = "application/json")
  Map<String, Object> getAllVars(@PathVariable("id") String id,
                                 @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Deletes a specific variable from the application's external context.
   *
   * @param id the unique application identifier
   * @param stepId the variable key identifier to delete
   * @param accessToken the access token for authentication
   * @return true if the variable was deleted, false if it didn't exist
   */
  @DeleteMapping(value = "/{id}/vars/{stepId}", produces = "application/json")
  Boolean deleteVar(@PathVariable("id") String id,
                    @PathVariable("stepId") String stepId,
                    @RequestHeader("X-Access-Token") String accessToken);

  /**
   * Deletes all variables for the specified application from external context.
   *
   * @param id the unique application identifier
   * @param accessToken the access token for authentication
   * @return true if variables were deleted, false if no variables existed
   */
  @DeleteMapping(value = "/{id}/vars", produces = "application/json")
  Boolean deleteVarsContext(@PathVariable("id") String id,
                            @RequestHeader("X-Access-Token") String accessToken);

}
