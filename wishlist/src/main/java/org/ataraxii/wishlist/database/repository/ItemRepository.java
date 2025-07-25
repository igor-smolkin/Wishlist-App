package org.ataraxii.wishlist.database.repository;

import org.ataraxii.wishlist.database.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findItemByIdAndUserId(UUID itemId, UUID userId);

    List<Item> findAllByUserId(UUID userId);

    Optional<Item> findByIdAndUserId(UUID id, UUID userId);
}
