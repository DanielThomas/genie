/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.common.dto.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Base class for search results containing common fields.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Getter
@EqualsAndHashCode(of = "id")
public class BaseSearchResult implements Serializable {

    private static final long serialVersionUID = -273035797399359914L;

    private final String id;
    private final String name;

    /**
     * Constructor.
     *
     * @param id   The id of the object in the search result.
     * @param name The name of the object in the search result.
     */
    @JsonCreator
    public BaseSearchResult(
        @NotBlank @JsonProperty("id") final String id,
        @NotBlank @JsonProperty("name") final String name
    ) {
        this.id = id;
        this.name = name;
    }

    /**
     * Convert this object to a string representation.
     *
     * @return This application data represented as a JSON structure
     */
    @Override
    public String toString() {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (final JsonProcessingException ioe) {
            return ioe.getLocalizedMessage();
        }
    }
}
