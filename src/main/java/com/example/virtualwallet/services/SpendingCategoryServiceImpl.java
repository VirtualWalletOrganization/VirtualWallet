package com.example.virtualwallet.services;

import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.repositories.contracts.SpendingCategoryRepository;
import com.example.virtualwallet.services.contracts.SpendingCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpendingCategoryServiceImpl implements SpendingCategoryService {

    private final SpendingCategoryRepository categoryRepository;

    @Autowired
    public SpendingCategoryServiceImpl(SpendingCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<SpendingCategory> getAllSpendingCategories() {
        return categoryRepository.getAllSpendingCategories();
    }

    @Override
    public SpendingCategory getSpendingCategoryById(int categoryId) {
        return categoryRepository.getSpendingCategoryById(categoryId);
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
        SpendingCategory category = categoryRepository.getSpendingCategoryById(categoryId);
        categoryRepository.delete(category);
    }
}