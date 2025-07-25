package org.ataraxii.wishlist.mapper;

import lombok.Builder;
import lombok.Data;
import org.ataraxii.wishlist.database.entity.Item;
import org.ataraxii.wishlist.dto.item.ItemResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
@Builder
public class ItemMapper {

    public ItemResponseDto toDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .url(item.getUrl())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .comment(item.getComment())
                .user(item.getUserId())
                .build();
    }
}
