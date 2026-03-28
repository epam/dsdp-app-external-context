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
import com.epam.digital.data.platform.model.BaseEntity;
import com.epam.digital.data.platform.repository.ApplicationContextRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base service for managing application contexts.
 * Provides CRUD operations, existence checks, and TTL (Time-To-Live) management.
 *
 * @param <T> the entity type that extends BaseEntity
 */
@RequiredArgsConstructor
public abstract class ApplicationContextService<T extends BaseEntity> {
  private final ApplicationContextRepository<T> repository;
  private final Supplier<T> entitySupplier;
  private final ExternalContextProperties properties;

  /**
   * Creates or updates an entry for the given application and key.
   *
   * @param applicationId the ID of the application
   * @param key           the unique identifier of the entry
   * @param value         the value to store
   */
  @Transactional
  public void put(String applicationId, String key, Object value) {
    T entity = repository
        .findByApplicationIdAndKey(applicationId, key)
        .orElseGet(entitySupplier);

    entity.setApplicationId(applicationId);
    entity.setKey(key);
    entity.setValue(value);
    entity.setExpireAt(Instant.now().plus(properties.getTtl()));

    repository.save(entity);
  }

  /**
   * Retrieves an entry from the application context.
   *
   * @param applicationId the ID of the application
   * @param key           the unique identifier of the entry
   * @return an Optional containing the entry value if it exists, or empty otherwise
   */
  public Optional<Object> get(String applicationId, String key) {
    return repository
        .findByApplicationIdAndKey(applicationId, key)
        .map(BaseEntity::getValue);
  }

  /**
   * Retrieves all entries for a given application context.
   *
   * @param applicationId the ID of the application
   * @return a map of entry keys to values; returns an empty map if no entries found
   */
  public Map<String, Object> getAll(String applicationId) {
    return repository
        .findAllByApplicationId(applicationId)
        .stream()
        .collect(Collectors.toMap(BaseEntity::getKey, BaseEntity::getValue));
  }

  /**
   * Deletes a specific entry from the application context.
   *
   * @param applicationId the ID of the application
   * @param key           the unique identifier of the entry
   * @return true if the entry was deleted, false if it did not exist
   */
  @Transactional
  public boolean delete(String applicationId, String key) {
    return repository.deleteByApplicationIdAndKey(applicationId, key) > 0;
  }

  /**
   * Deletes all entries for the given application context.
   *
   * @param applicationId the ID of the application
   * @return true if at least one entry was deleted, false if no entries existed
   */
  @Transactional
  public boolean deleteContext(String applicationId) {
    return repository.deleteAllByApplicationId(applicationId) > 0;
  }

}
