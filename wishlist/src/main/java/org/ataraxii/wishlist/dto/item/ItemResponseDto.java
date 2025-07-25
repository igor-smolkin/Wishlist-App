package org.ataraxii.wishlist.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItemResponseDto {

    @NotBlank
    private UUID id;

    @NotBlank
    private String name;

    @Size(max = 255)
    private String url;

    private Integer price;

    @Size(max = 255)
    private String imageUrl;

    @Size(max = 255)
    private String comment;

    @NotBlank
    private UUID user;
}
