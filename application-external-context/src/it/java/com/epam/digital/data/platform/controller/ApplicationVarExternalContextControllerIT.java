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

import com.epam.digital.data.platform.ExternalContextServiceApplication;
import com.epam.digital.data.platform.model.ApplicationVar;
import com.epam.digital.data.platform.repository.ApplicationContextRepository;
import com.epam.digital.data.platform.repository.ApplicationVarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ExternalContextServiceApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ApplicationVarExternalContextControllerIT extends BaseExternalContextControllerIT<ApplicationVar> {

  @Autowired
  private ApplicationVarRepository repository;

  @Override
  protected ApplicationContextRepository<ApplicationVar> getRepository() {
    return repository;
  }

  @Override
  protected String getResourcePath() {
    return "vars";
  }

  @Override
  protected String getResourceIdName() {
    return "key";
  }
}
