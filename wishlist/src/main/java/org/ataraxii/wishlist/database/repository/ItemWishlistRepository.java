package org.ataraxii.wishlist.database.repository;

import org.ataraxii.wishlist.database.entity.ItemWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemWishlistRepository extends JpaRepository<ItemWishlist, Long> {
    Optional<ItemWishlist> findByItemIdAndWishlistId(UUID itemId, UUID wishlistId);
}
