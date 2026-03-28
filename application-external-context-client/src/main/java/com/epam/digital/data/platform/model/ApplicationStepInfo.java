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

package com.epam.digital.data.platform.model;

import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents information about a specific step within an application workflow process.
 * <p>
 * This immutable record encapsulates the attributes of an application step,
 * including its unique identifier, type classification, current processing status,
 * and additional contextual details that may be required for step execution.
 * </p>
 *
 * @param id      the unique identifier of the application step, must not be null or empty.
 *                This identifier should be unique within the context of a single application
 *                workflow and is used for step tracking and reference purposes.
 * @param type    the classification or category of the application step, indicating the
 *                nature of processing or operation being performed. Must not be null.
 * @param status  the current processing status of the application step, represented by
 *                the {@link Status} enum. This provides type-safe status representation
 *                and ensures only valid status values can be assigned to a step.
 *                Must not be null.
 * @param form    the form-related information for steps that involve form processing.
 *                Contains form data, evaluation context, token, and localization settings.
 *                May be null for step types that do not involve form operations.
 * @param details a flexible map containing additional contextual information, metadata,
 *                or configuration parameters specific to this application step.
 *                The content and structure of this map depend on the step type and
 *                processing requirements. May contain execution results, error messages,
 *                timestamps, configuration values, or any other relevant data.
 *                Can be null or empty if no additional details are required.
 * @see Status
 * @see StepType
 * @see Form
 */
public record ApplicationStepInfo(
    String id,
    StepType type,
    Status status,
    Form form,
    Map<String, Object> details
) implements Serializable {

  /**
   * Enumeration representing the possible statuses of an application step within
   * a workflow process.
   * <p>
   * This enum provides type-safe status representation and ensures consistent
   * status management across the application workflow system. Each status represents
   * a distinct state in the step lifecycle, from initial creation through final
   * completion or termination.
   * </p>
   */
  public enum Status {
    /**
     * Indicates that the application step is awaiting processing or execution.
     * This is typically the initial state when a step is created but has not yet
     * been started or completed.
     */
    PENDING,
  
    /**
     * Indicates that the application step has been successfully completed.
     * All required operations have been executed without errors, and the step
     * has reached its terminal successful state.
     */
    SUCCESS,
  
    /**
     * Indicates that the application step has been temporarily suspended or paused.
     * This status is used when step execution is on hold, possibly waiting for
     * external input, manual intervention, or another asynchronous operation to complete.
     */
    SUSPENDED,

    /**
     * Indicates that the application step has encountered an error or failure during
     * processing. This status signifies that the step did not complete successfully
     * and may require error handling, retries, or manual resolution.
     */
    REJECTED
  }

  /**
   * Enumeration representing the various types of application steps supported
   * within the workflow system.
   * <p>
   * Each step type corresponds to a specific category of operation or processing
   * that can be performed as part of an application workflow. The step type determines
   * the behavior, validation rules, and processing logic applied to the step.
   * </p>
   */
  public enum StepType {
    /**
     * Represents a form submission step where user-provided data is collected
     * and processed. This step type typically involves data validation, transformation,
     * and persistence of form inputs.
     */
    FORM_SUBMISSION,
  
    /**
     * Represents a signature upload step where digital signatures or signature
     * documents are uploaded and verified. This step type handles signature
     * file processing and validation.
     */
    SIGNATURE_UPLOAD,
  
    /**
     * Represents a Sima İmza integration step.
     * This step type handles electronic signature operations through the Sima İmza service.
     */
    SIMA,
  
    /**
     * Represents an ASAN İmza (ASAN Signature) integration step.
     * This step type manages electronic signature operations through the ASAN
     * signature service, commonly used in Azerbaijan's digital government services.
     */
    ASAN_IMZA
  }

  /**
   * Represents form-related information for application steps that involve form processing.
   * <p>
   * This class encapsulates all necessary data for form handling within an application step,
   * including the form identifier, authorization token, language preferences, evaluation context,
   * and the actual form data. It provides a structured way to manage form-related operations
   * throughout the application workflow.
   * </p>
   */
  @Builder
  @Getter
  public static class Form implements Serializable {
    /**
     * The unique key identifying the form template or definition.
     * This key is used to locate and load the appropriate form schema,
     * validation rules, and rendering configuration.
     */
    private final String key;
  
    /**
     * The token for retrieving user information related to this form instance.
     * This token is used to fetch user-specific data and context needed
     * for form processing.
     */
    @Setter
    private String token;
  
    /**
     * The language code for form localization (e.g., "en", "uk", "az").
     * This determines the language in which form labels, messages,
     * and validation errors are displayed to the user.
     */
    private String language;
  
    /**
     * The evaluation context containing variables and parameters used for
     * dynamic form behavior, conditional logic, and computed fields.
     * <p>
     * This map provides runtime context that can be referenced in form expressions,
     * visibility conditions, validation rules, and default value calculations.
     * The keys are variable names, and the values are serializable objects
     * representing the variable data.
     * </p>
     */
    private final Map<String, Object> evalContext;
  
    /**
     * The actual form data containing user inputs and field values.
     * <p>
     * This map holds the current state of all form fields, where keys correspond
     * to field identifiers and values represent the field data. The structure and
     * content depend on the specific form schema and user interactions.
     * All values must be serializable to support persistence and transmission.
     * </p>
     */
    private final Map<String, Object> data;
  }
}
