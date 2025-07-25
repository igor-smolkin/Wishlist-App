package org.ataraxii.wishlist.controller;

import lombok.RequiredArgsConstructor;
import org.ataraxii.wishlist.dto.item.ItemDto;
import org.ataraxii.wishlist.dto.item.ItemResponseDto;
import org.ataraxii.wishlist.security.SecurityUtil;
import org.ataraxii.wishlist.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/")
public class ItemController {

    private final ItemService itemService;
    private final SecurityUtil securityUtil;

    @PostMapping("/wishlists/{wishlistId}/items")
    public ResponseEntity<ItemResponseDto> createItem(
            @PathVariable UUID wishlistId,
            @RequestBody ItemDto dto) {
        UUID userId = securityUtil.getCurrentUserId();
        dto.setWishlistId(wishlistId);
        ItemResponseDto response = itemService.createItem(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // На переработке

//    @GetMapping("/items")
//    public ResponseEntity<List<ItemResponseDto>> findAllItems() {
//        UUID userId = securityUtil.getCurrentUserId();
//        List<ItemResponseDto> items = itemService.findAllItems(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(items);
//    }

    // На переработке

//    @GetMapping("/items/{id}")
//    public ResponseEntity<ItemResponseDto> findItemById(@PathVariable UUID id) {
//        UUID userId = securityUtil.getCurrentUserId();
//        ItemResponseDto response = itemService.findItemById(userId, id);
//        return ResponseEntity.ok().body(response);
//    }

    // На переработке

//    @PutMapping("/items/{id}")
//    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable UUID id,
//                                                      @RequestBody ItemDto dto) {
//        UUID userId = securityUtil.getCurrentUserId();
//        ItemResponseDto item = itemService.updateItem(userId, id, dto);
//        return ResponseEntity.status(HttpStatus.OK).body(item);
//    }

    @PatchMapping("/wishlists/{wishlistId}/items/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(
            @PathVariable UUID wishlistId,
            @PathVariable UUID itemId,
            @RequestBody ItemDto dto) {
        UUID userId = securityUtil.getCurrentUserId();
        dto.setWishlistId(wishlistId);
        ItemResponseDto item = itemService.updateItem(userId, wishlistId, itemId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    @DeleteMapping("/wishlists/{wishlistId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID wishlistId,
            @PathVariable UUID itemId) {
        UUID userId = securityUtil.getCurrentUserId();
        itemService.deleteItem(userId, wishlistId, itemId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
