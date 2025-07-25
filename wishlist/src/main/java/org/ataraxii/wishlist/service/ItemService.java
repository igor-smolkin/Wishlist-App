package org.ataraxii.wishlist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ataraxii.wishlist.database.entity.Item;
import org.ataraxii.wishlist.database.entity.ItemWishlist;
import org.ataraxii.wishlist.database.entity.Wishlist;
import org.ataraxii.wishlist.database.repository.WishlistRepository;
import org.ataraxii.wishlist.database.repository.ItemWishlistRepository;
import org.ataraxii.wishlist.database.repository.ItemRepository;
import org.ataraxii.wishlist.dto.item.ItemDto;
import org.ataraxii.wishlist.dto.item.ItemResponseDto;
import org.ataraxii.wishlist.exception.NotFoundException;
import org.ataraxii.wishlist.mapper.ItemMapper;
import org.ataraxii.wishlist.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final WishlistRepository wishlistRepository;
    private final ItemWishlistRepository itemWishlistRepository;
    private final ItemMapper itemMapper;
    private final SecurityUtil securityUtil;

    @Transactional
    public ItemResponseDto createItem(ItemDto dto, UUID userId) {
        String username = securityUtil.getCurrentUsername();
        log.info("Создание предмета '{}' пользователем '{}'", dto.getName(), username);

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(dto.getWishlistId(), userId)
                .orElse(null);

        if (wishlist == null) {
            log.warn("Ошибка создания предмета: вишлист с id='{}' не найден у пользователя '{}'", dto.getWishlistId(), username);
            throw new NotFoundException("Вишлист не найден");
        }

        Item item = Item.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .comment(dto.getComment())
                .userId(userId)
                .build();

        itemRepository.save(item);
        log.info("Предмет '{}' успешно создан в вишлисте '{}' пользователем '{}'", item.getName(), wishlist.getName(), username);

        ItemWishlist itemWishlist = ItemWishlist.builder()
                .item(item)
                .wishlist(wishlist)
                .build();

        itemWishlistRepository.save(itemWishlist);

        return itemMapper.toDto(item);
    }

    // На переработке

//    public List<ItemResponseDto> findAllItems(UUID userId) {
//        String username = securityUtil.getCurrentUsername();
//        List<Item> items = itemRepository.findAllByUserId(userId);
//        log.info("Найдено '{}' предметов у пользователя '{}'", items.size(), username);
//        return items.stream()
//                .map(item -> {
//                    List<ItemWishlist> wishlists = item.getItemWishlist();
//                    UUID wishlistId = wishlists.get(0).getWishlist().getId();
//
//                    return itemMapper.toDto(item, wishlistId);
//
//                })
//                .collect(Collectors.toList());
//    }

    // На переработке

//    public ItemResponseDto findItemById(UUID userId, UUID itemId) {
//        String username = securityUtil.getCurrentUsername();
//        Item item = itemRepository.findItemByIdAndUserId(itemId, userId)
//                .orElseThrow(() -> new NotFoundException("Предмет с таким id не найден"));
//        log.info("Предмет '{}' найден у пользователя '{}'", item.getName(), username);
//        return itemMapper.toDto(userId, item);
//    }

    // На переработке

    @Transactional
    public ItemResponseDto updateItem(UUID userId, UUID wishlistId, UUID itemId, ItemDto dto) {
        String username = securityUtil.getCurrentUsername();

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка при изменении предмета: Вишлист с id='{}' не найден у пользователя '{}'", wishlistId, username);
                    return new NotFoundException("Вишлист не найден");
                });

        Item item = itemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка при изменении предмета: предмет с id='{}' не найден у пользователя '{}'", itemId, username);
                    return new NotFoundException("Предмет с таким id не найден");
                });

        ItemWishlist itemWishlist = itemWishlistRepository.findByItemIdAndWishlistId(itemId, wishlistId)
                .orElseThrow(() -> {
                    log.warn("Ошибка при изменении предмета: Предмет с id='{}' не принадлежит вишлисту с id='{}'", itemId, wishlistId);
                    return new NotFoundException("Предмет не принадлежит этому вишлисту");
                });

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getUrl() != null) item.setUrl(dto.getUrl());
        if (dto.getPrice() != null) item.setPrice(dto.getPrice());
        if (dto.getImageUrl() != null) item.setComment(dto.getComment());

        itemRepository.save(item);

        log.info("Предмет '{}' успешно изменен пользователем '{}'", item.getName(), username);

        itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Transactional
    public void deleteItem(UUID userId, UUID wishlistId, UUID itemId) {

        String username = securityUtil.getCurrentUsername();

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка при удалении предмета: Вишлист с id='{}' не найден у пользователя '{}'", wishlistId, username);
                    return new NotFoundException("Вишлист не найден");
                });

        Item item = itemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка при удалении предмета: Предмет с id='{}' не найден у пользователя '{}'", itemId, username);
                    return new NotFoundException("Предмет не найден");
                });

        ItemWishlist itemWishlist = itemWishlistRepository.findByItemIdAndWishlistId(itemId, wishlistId)
                .orElseThrow(() -> {
                    log.warn("Ошибка при удалении предмета: Предмет с id='{}' не принадлежит вишлисту с id='{}'", itemId, wishlistId);
                    return new NotFoundException("Предмет не принадлежит этому вишлисту");
                });

        itemRepository.delete(item);
        log.info("Предмет '{}' удален пользователем '{}'", item.getName(), username);
    }
}
