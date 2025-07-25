package org.ataraxii.wishlist.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ataraxii.wishlist.dto.item.ItemWishlistDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemsResponseDto {
    private UUID id;
    private String name;
    private List<ItemWishlistDto> items;
}
