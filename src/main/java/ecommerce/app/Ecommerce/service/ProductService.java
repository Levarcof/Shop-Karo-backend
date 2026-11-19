package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.ProductDto;
import ecommerce.app.Ecommerce.dtos.ProductSuggestionDto;
import ecommerce.app.Ecommerce.entity.Category;
import ecommerce.app.Ecommerce.entity.Product;
import ecommerce.app.Ecommerce.entity.ProductRatingSummary;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.CategoryRepository;
import ecommerce.app.Ecommerce.repository.ProductRatingSummaryRepository;
import ecommerce.app.Ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class ProductService {
    private  CategoryRepository categoryRepository;
    private  ProductRepository productRepository;
    private ProductRatingSummaryRepository productRatingSummaryRepository;


    public ProductService(CategoryRepository categoryRepository , ProductRepository productRepository , ProductRatingSummaryRepository productRatingSummaryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productRatingSummaryRepository = productRatingSummaryRepository;
    }

    public List<ProductDto> getProductsByCategory(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
        return category.getProducts()
                .stream()
                .map(this::convertIntoProductResponseDto)
                .toList();
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertIntoProductResponseDto)
                .toList();
    }

    public List<ProductDto> getProductsByName(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name field is null , so we can't process products");
        }
        return   productRepository.findByNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::convertIntoProductResponseDto)
                .toList();
    }

    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        return convertIntoProductResponseDto(product);
    }

    private ProductDto convertIntoProductResponseDto(Product product){
        ProductRatingSummary productRatingSummary = productRatingSummaryRepository.findByProductId(product.getId()).orElse(null);;
        Long ratingCount = null;
        BigDecimal averageRating = null;

        if(productRatingSummary != null){
            ratingCount = productRatingSummary.getTotalRatings();
            averageRating = productRatingSummary.getAverageRating();
        }
        ProductDto productDto = new ProductDto(averageRating , ratingCount, product.getId() , product.getSku(), product.getName(), product.getDescription(), product.getPrice() , product.getStockQuantity() , product.getImageUrl() , product.isActive(),  product.getCategory().getName() , product.getCreatedAt() , product.getUpdatedAt());
        return  productDto;
    }


    public  List<ProductSuggestionDto>  getSuggestedProduct(String name) {
        if(name == null || name.isBlank() || name.trim().length() <2){
            return List.of();
        }

        List<ProductSuggestionDto> products = productRepository.findTop10ByNameContainingIgnoreCase(name.trim());
        return products.stream().map(product->
                new ProductSuggestionDto(product.getId(), product.getName(),  product.getImageUrl() , product.getPrice()))
                .toList();

    }
}
