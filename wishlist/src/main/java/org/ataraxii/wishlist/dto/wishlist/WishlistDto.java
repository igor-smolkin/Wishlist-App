package org.ataraxii.wishlist.dto.wishlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class WishlistDto {
    @Size(max = 32)
    @NotBlank
    private String name;

    @Size(max = 255)
    private String comment;

    private Instant date;
}
