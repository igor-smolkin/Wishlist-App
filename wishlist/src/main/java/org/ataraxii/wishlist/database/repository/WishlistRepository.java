package org.ataraxii.wishlist.database.repository;

import org.ataraxii.wishlist.database.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    Optional<Wishlist> findByIdAndUserId(UUID id, UUID userId);

    List<Wishlist> findByUserId(UUID userId);

    UUID id(UUID id);
}
