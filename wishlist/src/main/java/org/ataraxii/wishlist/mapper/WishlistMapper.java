package org.ataraxii.wishlist.mapper;

import org.ataraxii.wishlist.database.entity.ItemWishlist;
import org.ataraxii.wishlist.database.entity.Wishlist;
import org.ataraxii.wishlist.dto.item.ItemWishlistDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistItemsResponseDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistResponseDto;
import org.springframework.stereotype.Component;

@Component
public class WishlistMapper {
    public WishlistResponseDto toDto(Wishlist wishlist) {
        return WishlistResponseDto.builder()
                .id(wishlist.getId())
                .name(wishlist.getName())
                .build();
    }

    public WishlistItemsResponseDto toDtoWithItems(Wishlist wishlist) {
        return WishlistItemsResponseDto.builder()
                .id(wishlist.getId())
                .name(wishlist.getName())
                .items(
                        wishlist.getItemWishlist().stream()
                                .map(ItemWishlist::getItem)
                                .map(item -> ItemWishlistDto.builder()
                                        .id(item.getId())
                                        .name(item.getName())
                                        .url(item.getUrl())
                                        .build())
                                .toList()
                )
                .build();
    }
}
