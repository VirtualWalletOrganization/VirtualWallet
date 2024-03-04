package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.SpendingCategory;

import java.util.List;
import java.util.Optional;

public interface SpendingCategoryService {
    List<SpendingCategory> getAllSpendingCategories();

    SpendingCategory getSpendingCategoryById(int categoryId);

    SpendingCategory getSpendingCategoryByName(String categoryName);

    SpendingCategory createSpendingCategory(SpendingCategory category);

    SpendingCategory updateSpendingCategory(SpendingCategory category);

    void deleteSpendingCategory(int categoryId);
}
