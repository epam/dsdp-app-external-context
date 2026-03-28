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

import com.epam.digital.data.platform.service.ApplicationStepExternalContextService;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing application step contexts.
 * Supports creating, updating, retrieving, deleting steps,
 * checking existence, and managing TTL (Time-To-Live) for steps and contexts.
 */
@RestController
@Validated
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class ApplicationStepExternalContextController {

  private final ApplicationStepExternalContextService service;

  @PutMapping("/{id}/steps/{stepId}")
  public ResponseEntity<Void> putStep(@PathVariable("id") @NotBlank String id,
                                      @PathVariable("stepId") @NotBlank String stepId,
                                      @RequestBody Object value) {
    service.put(id, stepId, value);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/steps/{stepId}")
  public ResponseEntity<Optional<Object>> getStep(@PathVariable("id") @NotBlank String id,
                                                  @PathVariable("stepId") @NotBlank String stepId) {
    Optional<Object> stepInfo = service.get(id, stepId);
    return ResponseEntity.ok(stepInfo);
  }

  @GetMapping("/{id}/steps")
  public ResponseEntity<Map<String, Object>> getAllSteps(@PathVariable("id") @NotBlank String id) {
    Map<String, Object> steps = service.getAll(id);
    return ResponseEntity.ok(steps);
  }

  @DeleteMapping("/{id}/steps/{stepId}")
  public ResponseEntity<Boolean> deleteStep(@PathVariable("id") @NotBlank String id,
                                            @PathVariable("stepId") @NotBlank String stepId) {
    boolean deleted = service.delete(id, stepId);
    return ResponseEntity.ok(deleted);
  }

  @DeleteMapping("/{id}/steps")
  public ResponseEntity<Boolean> deleteContext(@PathVariable("id") @NotBlank String id) {
    boolean deleted = service.deleteContext(id);
    return ResponseEntity.ok(deleted);
  }
}