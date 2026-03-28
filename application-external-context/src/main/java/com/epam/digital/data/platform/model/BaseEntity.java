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

package com.epam.digital.data.platform.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Abstract base class for application context entities.
 * Contains common fields for storing key-value pairs with optional TTL support.
 *
 * <p>Each entity represents a single key-value pair within an application context,
 * identified by the combination of applicationId and key.
 * The expireAt field is optional and enables automatic TTL-based cleanup when set.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {
  @Id
  private String id;
  private String applicationId;
  private String key;
  private Object value;
  @Indexed(expireAfter = "0s")
  private Instant expireAt;
}
