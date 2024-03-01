package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.SpendingCategory;

import java.util.List;

public interface SpendingCategoryRepository {
    List<SpendingCategory> getAllSpendingCategories();

    SpendingCategory getSpendingCategoryById(int categoryId);

    SpendingCategory create(SpendingCategory category);

    void update(SpendingCategory category);

    void delete(SpendingCategory category);
}
