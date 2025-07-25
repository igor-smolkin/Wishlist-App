package org.ataraxii.wishlist.controller;

import lombok.RequiredArgsConstructor;
import org.ataraxii.wishlist.dto.wishlist.WishlistDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistItemsResponseDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistResponseDto;
import org.ataraxii.wishlist.security.SecurityUtil;
import org.ataraxii.wishlist.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/app/")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final SecurityUtil securityUtil;

    @PostMapping("/wishlists")
    public ResponseEntity<WishlistResponseDto> createWishlist(@RequestBody WishlistDto dto) {
        UUID userId = securityUtil.getCurrentUserId();
        WishlistResponseDto response = wishlistService.createWishlist(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/wishlists")
    public ResponseEntity<List<WishlistResponseDto>> findAllWishlists() {
        UUID userId = securityUtil.getCurrentUserId();
        List<WishlistResponseDto> response = wishlistService.findAllWishlists(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/wishlists/{wishlistId}")
    public ResponseEntity<WishlistItemsResponseDto> findWishlistById(@PathVariable UUID wishlistId) {
        UUID userId = securityUtil.getCurrentUserId();
        WishlistItemsResponseDto response = wishlistService.findWishlistById(userId, wishlistId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // На переработке //

//    @PutMapping("/wishlists/{id}")
//    public ResponseEntity<WishlistResponseDto> updateWishlist(@PathVariable UUID id,
//                                                              @RequestBody WishlistDto dto) {
//        UUID userId = securityUtil.getCurrentUserId();
//        WishlistResponseDto response = wishlistService.updateWishlist(userId, id ,dto);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @PatchMapping("/wishlists/{wishlistId}")
    public ResponseEntity<WishlistResponseDto> updateWishlist(
            @PathVariable UUID wishlistId,
            @RequestBody WishlistDto dto) {
        UUID userId = securityUtil.getCurrentUserId();
        WishlistResponseDto response = wishlistService.updateWishlist(userId, wishlistId ,dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
