package org.ataraxii.wishlist.unit;

import org.ataraxii.wishlist.database.entity.User;
import org.ataraxii.wishlist.database.entity.Wishlist;
import org.ataraxii.wishlist.database.repository.WishlistRepository;
import org.ataraxii.wishlist.dto.wishlist.WishlistDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistItemsResponseDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistResponseDto;
import org.ataraxii.wishlist.exception.NotFoundException;
import org.ataraxii.wishlist.mapper.WishlistMapper;
import org.ataraxii.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistMapper wishlistMapper;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void createFolder_success_returnWishlist() {

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        WishlistDto dto = WishlistDto.builder()
                .name("test-wishlist")
                .build();

        UUID folderId = UUID.randomUUID();

        Wishlist testWishlist = org.ataraxii.wishlist.database.entity.Wishlist.builder()
                .id(folderId)
                .name("test-wishlist")
                .user(testUser)
                .build();

        WishlistResponseDto expectedResponse = WishlistResponseDto.builder()
                .id(folderId)
                .name("test-wishlist")
                .build();

        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(testWishlist);
        when(wishlistMapper.toDto(any(Wishlist.class))).thenReturn(expectedResponse);

        WishlistResponseDto actualResponse = wishlistService.createWishlist(dto, testUser);

        assertEquals(expectedResponse, actualResponse);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void findFolderById_correctId_returnWishlist() {
        UUID folderId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        Wishlist testWishlist = Wishlist.builder()
                .id(folderId)
                .name("test-wishlist")
                .user(testUser)
                .build();

        WishlistItemsResponseDto expectedResponse = WishlistItemsResponseDto.builder()
                .id(folderId)
                .name("test-wishlist")
                .items(null)
                .build();

        when(wishlistRepository.findByIdAndUser(folderId, testUser)).thenReturn(Optional.of(testWishlist));
        when(wishlistMapper.toDtoWithItems(testWishlist)).thenReturn(expectedResponse);

        WishlistItemsResponseDto actualResponse = wishlistService.findWishlistById(testUser, folderId);

        assertEquals(expectedResponse, actualResponse);
        verify(wishlistRepository).findByIdAndUser(folderId, testUser);
    }

    @Test
    void findWishlistById_incorrectId_returnNotFoundException() {

        UUID folderId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        when(wishlistRepository.findByIdAndUser(folderId, testUser)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            wishlistService.findWishlistById(testUser, folderId);
        });
    }

    @Test
    void updateWishlist_correctId_returnUpdatedItem() {

        UUID folderId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        Wishlist testWishlist = org.ataraxii.wishlist.database.entity.Wishlist.builder()
                .id(folderId)
                .name("test-wishlist")
                .user(testUser)
                .build();

        Wishlist updatedWishlist = org.ataraxii.wishlist.database.entity.Wishlist.builder()
                .id(folderId)
                .name("updated-wishlist")
                .user(testUser)
                .build();

        WishlistDto updatedWishlistDto = WishlistDto.builder()
                .name("updated-wishlist")
                .build();

        WishlistResponseDto expectedResponse = WishlistResponseDto.builder()
                .id(folderId)
                .name("test-wishlist")
                .build();

        when(wishlistRepository.findByIdAndUser(folderId, testUser)).thenReturn(Optional.of(testWishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(updatedWishlist);
        when(wishlistMapper.toDto(any(Wishlist.class))).thenReturn(expectedResponse);

        WishlistResponseDto actualResponse = wishlistService.updateWishlist(testUser, folderId, updatedWishlistDto);

        assertEquals(expectedResponse, actualResponse);

        verify(wishlistRepository).findByIdAndUser(folderId, testUser);
        verify(wishlistRepository).save(any(Wishlist.class));
        verify(wishlistMapper).toDto(any(Wishlist.class));
    }

    @Test
    void updateWishlist_incorrectId_returnNotFoundException() {

        UUID incorrectItemId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        WishlistDto updatedWishlistDto = WishlistDto.builder()
                .name("updated-wishlist")
                .build();

        when(wishlistRepository.findByIdAndUser(incorrectItemId, testUser)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            wishlistService.updateWishlist(testUser, incorrectItemId, updatedWishlistDto);
        });

        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void deleteWishlist_correctId_success() {

        UUID folderId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        Wishlist testWishlist = org.ataraxii.wishlist.database.entity.Wishlist.builder()
                .id(folderId)
                .name("test-wishlist")
                .user(testUser)
                .build();

        when(wishlistRepository.findByIdAndUser(folderId, testUser)).thenReturn(Optional.of(testWishlist));

        wishlistService.deleteWishlist(testUser, folderId);

        verify(wishlistRepository).delete(any());
    }

    @Test
    void deleteWishlist_incorrectId_returnNotFoundException() {

        UUID incorrectFolderId = UUID.randomUUID();

        User testUser = User.builder()
                .id(UUID.fromString("ea7c7381-a19c-49a9-92cd-d32c3db25092"))
                .username("testuser")
                .password("testpassword")
                .createdAt(Instant.now())
                .build();

        when(wishlistRepository.findByIdAndUser(incorrectFolderId, testUser)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            wishlistService.deleteWishlist(testUser, incorrectFolderId);
        });

        verify(wishlistRepository, never()).delete(any());
    }
}
