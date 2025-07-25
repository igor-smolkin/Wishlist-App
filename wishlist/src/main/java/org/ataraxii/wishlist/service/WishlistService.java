package org.ataraxii.wishlist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ataraxii.wishlist.database.entity.Wishlist;
import org.ataraxii.wishlist.database.repository.WishlistRepository;
import org.ataraxii.wishlist.dto.wishlist.WishlistDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistItemsResponseDto;
import org.ataraxii.wishlist.dto.wishlist.WishlistResponseDto;
import org.ataraxii.wishlist.exception.NotFoundException;
import org.ataraxii.wishlist.mapper.WishlistMapper;
import org.ataraxii.wishlist.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistMapper wishlistMapper;
    private final SecurityUtil securityUtil;

    @Transactional
    public WishlistResponseDto createWishlist(WishlistDto dto, UUID userId) {
        String username = securityUtil.getCurrentUsername();
        log.info("Создание вишлиста {} пользователем {}", dto.getName(), username);

        Wishlist wishlist = Wishlist.builder()
                .name(dto.getName())
                .comment(dto.getComment())
                .date(dto.getDate())
                .userId(userId)
                .build();

        wishlistRepository.save(wishlist);

        log.info("Вишлист {} успешно создан пользователем {}", dto.getName(), username);
        return wishlistMapper.toDto(wishlist);
    }

    public List<WishlistResponseDto> findAllWishlists(UUID userId) {
        String username = securityUtil.getCurrentUsername();
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        log.info("Найдено {} вишлистов у пользователя {}", wishlists.size(), username);
        return wishlists.stream()
                .map(wishlistMapper::toDto)
                .collect(Collectors.toList());
    }

    public WishlistItemsResponseDto findWishlistById(UUID userId, UUID id) {
        String username = securityUtil.getCurrentUsername();
        Wishlist wishlist = wishlistRepository.findByIdAndUserId(id, userId)
                .orElse(null);

        if (wishlist == null) {
            log.warn("Ошибка поиска вишлиста: вишлист с id={} не найден у пользователя {}", id, username);
            throw new NotFoundException("Вишлист с таким id не найдена");
        }
        log.info("Вишлист {} найден у пользователя {}", wishlist.getName(), username);
        return wishlistMapper.toDtoWithItems(wishlist);
    }

    // На переработке

//    public WishlistResponseDto updateWishlist(UUID userId, UUID itemId, WishlistDto dto) {
//        String username = securityUtil.getCurrentUsername();
//        Wishlist wishlist = wishlistRepository.findByIdAndUserId(itemId, userId)
//                .orElse(null);
//
//        if (wishlist == null) {
//            log.warn("Ошибка обновления вишлиста: вишлист с id={} не найден у пользователя {}", itemId, username);
//            throw new NotFoundException("Вишлист с таким id не найден");
//        }
//        log.info("Обновление вишлиста {} пользователем {}", wishlist.getName(), username);
//
//        String oldName = wishlist.getName();
//        wishlist.setName(dto.getName());
//        log.info("Имя вишлиста успешно изменено: {} -> {}", oldName, wishlist.getName());
//
//        wishlistRepository.save(wishlist);
//        return wishlistMapper.toDto(wishlist);
//    }

        public WishlistResponseDto updateWishlist(UUID userId, UUID wishlistId, WishlistDto dto) {
        String username = securityUtil.getCurrentUsername();

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> {
                    log.warn("Ошибка обновления вишлиста: вишлист с id='{}' не найден у пользователя '{}'", wishlistId, username);
                    return new NotFoundException("Вишлист с таким id не найден");
                });

        log.info("Обновление вишлиста '{}' пользователем '{}'", wishlist.getName(), username);

        if (dto.getName() != null) wishlist.setName(dto.getName());
        if (dto.getComment() != null) wishlist.setComment(dto.getComment());
        if (dto.getDate() != null) wishlist.setDate(dto.getDate());

        wishlistRepository.save(wishlist);
        return wishlistMapper.toDto(wishlist);
    }

    @Transactional
    public void deleteWishlist(UUID userId, UUID id) {
        String username = securityUtil.getCurrentUsername();
        Wishlist wishlist = wishlistRepository.findByIdAndUserId(id, userId)
                .orElse(null);
        if (wishlist == null) {
            log.warn("Ошибка удаления вишлиста: вишлист с id={} не найден у пользователя {}", id, username);
            throw new NotFoundException("Вишлист с таким id не найден");
        }
        wishlistRepository.delete(wishlist);
        log.info("Вишлист {} удален пользователем {}", wishlist.getName(), username);
    }

    public WishlistItemsResponseDto checkShared(UUID id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .filter(Wishlist::isShared)
                .orElse(null);

        if (wishlist == null) {
            log.warn("Вишлист с таким id не найден");
            throw new NotFoundException("Вишлист с таким id не найден");
        }
        return wishlistMapper.toDtoWithItems(wishlist);
    }

    @Transactional
    public void setShared(UUID userId, UUID id) {
        String username = securityUtil.getCurrentUsername();
        Wishlist wishlist = wishlistRepository.findByIdAndUserId(id, userId)
                .orElse(null);

        if (wishlist == null) {
            log.warn("Вишлист с id={} не найден у пользователя {}", id, username);
            throw new NotFoundException("Вишлист с таким id не найден");
        }

        wishlist.setShared(true);
        wishlistRepository.save(wishlist);
    }
}
