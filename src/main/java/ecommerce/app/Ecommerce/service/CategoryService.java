package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.CategoryDto;
import ecommerce.app.Ecommerce.entity.Category;
import ecommerce.app.Ecommerce.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public List<CategoryDto> getALlCategory() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertTOCategoryDto)
                .toList();
    }

    private CategoryDto convertTOCategoryDto(Category category){
        return  new CategoryDto(category.getName() , category.getDescription() , category.getId());
    }
}
