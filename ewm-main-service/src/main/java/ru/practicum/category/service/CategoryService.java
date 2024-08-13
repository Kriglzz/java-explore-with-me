package ru.practicum.category.service;


import org.springframework.data.domain.PageRequest;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    /**
     * public
     */
    CategoryDto getCategoryById(Long categoryId);

    /**
     * public
     */
    List<CategoryDto> getAllCategories(PageRequest pageRequest);

    /**
     * admin
     */
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    /**
     * admin
     */
    void deleteCategory(Long categoryId);

    /**
     * admin
     */
    CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDTO);
}
