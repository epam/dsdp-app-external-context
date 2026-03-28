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

import com.epam.digital.data.platform.service.ApplicationVarExternalContextService;
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
 * REST controller for managing application variable contexts.
 * Supports creating, updating, retrieving, deleting variables,
 * checking existence, and managing TTL (Time-To-Live) for variables and contexts.
 */
@RestController
@Validated
@RequestMapping("${api.base-path}")
@RequiredArgsConstructor
public class ApplicationVarExternalContextController {

  private final ApplicationVarExternalContextService service;

  @PutMapping("/{id}/vars/{key}")
  public ResponseEntity<Void> putVar(@PathVariable("id") @NotBlank String id,
                                     @PathVariable("key") @NotBlank String key,
                                     @RequestBody Object value) {
    service.put(id, key, value);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/vars/{key}")
  public ResponseEntity<Optional<Object>> getVar(@PathVariable("id") @NotBlank String id,
                                                 @PathVariable("key") @NotBlank String key) {
    Optional<Object> optVar = service.get(id, key);
    return ResponseEntity.ok(optVar);
  }

  @GetMapping("/{id}/vars")
  public ResponseEntity<Map<String, Object>> getAll(@PathVariable("id") @NotBlank String id) {
    Map<String, Object> entries = service.getAll(id);
    return ResponseEntity.ok(entries);
  }

  @DeleteMapping("/{id}/vars/{key}")
  public ResponseEntity<Boolean> deleteVar(@PathVariable("id") @NotBlank String id,
                                           @PathVariable("key") @NotBlank String key) {
    boolean deleted = service.delete(id, key);
    return ResponseEntity.ok(deleted);
  }

  @DeleteMapping("/{id}/vars")
  public ResponseEntity<Boolean> deleteContext(@PathVariable("id") @NotBlank String id) {
    boolean deleted = service.deleteContext(id);
    return ResponseEntity.ok(deleted);
  }
}