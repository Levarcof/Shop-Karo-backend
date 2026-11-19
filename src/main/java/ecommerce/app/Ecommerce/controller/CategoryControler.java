package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.CategoryDto;
import ecommerce.app.Ecommerce.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryControler {

    CategoryService categoryService;

    public CategoryControler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> getAll(){
        List<CategoryDto> categories = categoryService.getALlCategory();
        return ResponseEntity.ok(categories);
    }
}
