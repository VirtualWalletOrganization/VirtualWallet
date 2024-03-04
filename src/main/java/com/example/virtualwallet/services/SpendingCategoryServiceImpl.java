package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.repositories.contracts.SpendingCategoryRepository;
import com.example.virtualwallet.services.contracts.SpendingCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpendingCategoryServiceImpl implements SpendingCategoryService {

    private final SpendingCategoryRepository categoryRepository;

    @Autowired
    public SpendingCategoryServiceImpl(SpendingCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<SpendingCategory> getAllSpendingCategories() {
        return categoryRepository.getAllSpendingCategories()
                .orElseThrow(() -> new EntityNotFoundException("Categories"));
    }

    @Override
    public SpendingCategory getSpendingCategoryById(int categoryId) {
        return categoryRepository.getSpendingCategoryById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category", "id", String.valueOf(categoryId)));
    }

    @Override
    public SpendingCategory getSpendingCategoryByName(String categoryName) {
        Optional<SpendingCategory> existingSpendingCategory = categoryRepository.getSpendingCategoryByName(categoryName);

        if (existingSpendingCategory.isEmpty()) {
            SpendingCategory spendingCategory = new SpendingCategory();
            spendingCategory.setName(categoryName);
            categoryRepository.create(spendingCategory);
            return spendingCategory;
        } else {
            return existingSpendingCategory.get();
        }
    }

    @Override
    public SpendingCategory createSpendingCategory(SpendingCategory category) {
        return categoryRepository.create(category);
    }

    @Override
    public SpendingCategory updateSpendingCategory(SpendingCategory category) {
        categoryRepository.update(category);
        return category;
    }

    @Override
    public void deleteSpendingCategory(int categoryId) {
        SpendingCategory category = getSpendingCategoryById(categoryId);
        categoryRepository.delete(category);
    }
}