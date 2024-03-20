package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.SpendingCategory;

import java.util.List;
import java.util.Optional;

public interface SpendingCategoryRepository {

    Optional<List<SpendingCategory>> getAllSpendingCategories();

    Optional<SpendingCategory> getSpendingCategoryById(int categoryId);

    Optional<SpendingCategory> getSpendingCategoryByName(String categoryName);

    SpendingCategory create(SpendingCategory category);

    void update(SpendingCategory category);

    void delete(SpendingCategory category);
}