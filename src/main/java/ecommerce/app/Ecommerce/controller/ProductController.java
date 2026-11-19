package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.ProductDto;
import ecommerce.app.Ecommerce.dtos.ProductSuggestionDto;
import ecommerce.app.Ecommerce.entity.Product;
import ecommerce.app.Ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId){
        List<ProductDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping
    public  ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto>  products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/name")
    public ResponseEntity<List<ProductDto>> getProductsByName(@RequestParam String name){
        List<ProductDto> products = productService.getProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId){
        ProductDto productDto = productService.getProductById(productId);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductSuggestionDto>> getSuggestedProduct(@RequestParam String name){
        List<ProductSuggestionDto>  productSuggestionDto = productService.getSuggestedProduct(name);
        return ResponseEntity.ok(productSuggestionDto);

    }
}
