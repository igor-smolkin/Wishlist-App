package org.ataraxii.wishlist.dto.wishlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDto {

    @NotBlank
    private UUID id;

    @NotBlank
    @Size(max = 32)
    private String name;

    @Size(max = 255)
    private String comment;

    private Instant date;

    private boolean shared;
}
