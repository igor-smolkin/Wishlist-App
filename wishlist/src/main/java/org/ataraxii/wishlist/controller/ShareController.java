package org.ataraxii.wishlist.controller;

import lombok.RequiredArgsConstructor;
import org.ataraxii.wishlist.dto.wishlist.WishlistItemsResponseDto;
import org.ataraxii.wishlist.security.SecurityUtil;
import org.ataraxii.wishlist.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ShareController {

    private final WishlistService wishlistService;
    private final SecurityUtil securityUtil;

    @GetMapping("/shared/wishlists/{wishlistId}")
    public ResponseEntity<WishlistItemsResponseDto> getSharedWishlist(@PathVariable UUID wishlistId) {
        WishlistItemsResponseDto wishlist = wishlistService.checkShared(wishlistId);
        return ResponseEntity.ok(wishlist);
    }

    @PatchMapping("/wishlists/share/{wishlistId}")
    public ResponseEntity<Void> setShareWishlist(@PathVariable UUID wishlistId) {
        UUID userId = securityUtil.getCurrentUserId();
        wishlistService.setShared(userId, wishlistId);
        return ResponseEntity.ok().build();
    }
}
