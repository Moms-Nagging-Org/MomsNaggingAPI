package com.jasik.momsnaggingapi.domain.schedule.repository;

import com.jasik.momsnaggingapi.domain.schedule.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUsed(Boolean used);

    Optional<Category> findByCategoryName(String categoryName);

}
