package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyItemWrapperDTO {

    @JsonProperty("item")
    private TinyItemDTO item;
}

