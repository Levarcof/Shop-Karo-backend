package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.*;
import ecommerce.app.Ecommerce.entity.*;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.CartItemRepository;
import ecommerce.app.Ecommerce.repository.CartRepository;
import ecommerce.app.Ecommerce.repository.ProductRepository;
import ecommerce.app.Ecommerce.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private CartRepository cartRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository , UserRepository userRepository , CartItemRepository cartItemRepository , ProductRepository productRepository)   {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public  CartResponseDto add(CartReqDto cartReqDto , Long userId) {
        Product product = productRepository.findById(cartReqDto.getProductId()).orElseThrow(()-> new ResourceNotFoundException("Product not found")) ;

        if (!product.isActive()) {
            throw new IllegalStateException("Product unavailable");
        }

        CartResponseDto responseDto = new CartResponseDto();
        Cart cart;
        Optional<Cart> exist = cartRepository.findByUserId(userId);


        if(exist.isPresent()){
            cart = exist.get();
        }
        else {
            User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
            cart = new Cart();
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart.setUser(user);
        }
        Optional<CartItem> existItem = cartItemRepository.findByProductIdAndCartId(cartReqDto.getProductId() , cart.getId());
        if(existItem.isPresent()){
            responseDto.setMessage("Product already in your cart cart");
            return  responseDto;
        }
        CartItem item = new CartItem();
        item.setAddedAt(LocalDateTime.now());
        item.setCart(cart);
        item.setProduct(product);

        cart.getItems().add(item);
        cartRepository.save(cart);
        responseDto.setMessage("Prodcut added in your cart");
        return responseDto;
    }

    public CartResponseDto delete(CartReqDto reqDto , Long userId){
        Product product = productRepository.findById(reqDto.getProductId()).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("Your Cart not found"));

        CartItem item = cartItemRepository.findByProductIdAndCartId(product.getId() , cart.getId()).orElseThrow(()->new ResourceNotFoundException("cart item not found"));
        cartItemRepository.delete(item);

        CartResponseDto cartResponseDto =  new CartResponseDto();
        cartResponseDto.setMessage("Delete successfully done");
        return cartResponseDto;
    }

    public  CartDetaiilsDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("Cart not found"));
        CartDetaiilsDto cartDetaiilsDto = new CartDetaiilsDto();

        if(cart != null && cart.getItems() != null){
            for(CartItem item : cart.getItems()){
                Product product = item.getProduct();
                ProductDetailsDto productDetailsDto = getProductsDetails(product);

                cartDetaiilsDto.getCartItems().add(productDetailsDto);
            }
        }
        return cartDetaiilsDto;
    }

    private ProductDetailsDto  getProductsDetails(Product product){
        ProductDetailsDto productDetailsDto = new ProductDetailsDto();
        productDetailsDto.setActive(product.isActive());
        productDetailsDto.setCategory(product.getCategory().getName());
        productDetailsDto.setDescription(product.getDescription());
        productDetailsDto.setName(product.getName());
        productDetailsDto.setPrice(product.getPrice());
        productDetailsDto.setImageUrl(product.getImageUrl());
        productDetailsDto.setStockQuantity(product.getStockQuantity());
        productDetailsDto.setId(product.getId());

        return  productDetailsDto;

    }
}
