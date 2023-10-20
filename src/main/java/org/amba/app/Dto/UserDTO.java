package org.amba.app.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;

import java.util.UUID;

public record UserDTO(@JsonProperty("userUuid") UUID userId, String email) {
}
