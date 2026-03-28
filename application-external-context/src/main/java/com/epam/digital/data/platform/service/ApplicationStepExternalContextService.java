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
import org.springframework.stereotype.Service;

/**
 * Service for managing application step contexts.
 * Provides methods to create, update, retrieve, delete steps,
 * check existence, and manage TTL (Time-To-Live) for contexts.
 */
@Service
public class ApplicationStepExternalContextService extends ApplicationContextService<ApplicationStep> {

  public ApplicationStepExternalContextService(ApplicationStepRepository repository,
                                               ExternalContextProperties properties) {
    super(repository, ApplicationStep::new, properties);
  }
}