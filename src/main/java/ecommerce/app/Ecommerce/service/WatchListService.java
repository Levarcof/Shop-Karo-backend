package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.ProductDetailsDto;
import ecommerce.app.Ecommerce.dtos.WatchListDetailDto;
import ecommerce.app.Ecommerce.dtos.WatchListReqDto;
import ecommerce.app.Ecommerce.dtos.WatchListResponseDto;
import ecommerce.app.Ecommerce.entity.Product;
import ecommerce.app.Ecommerce.entity.User;
import ecommerce.app.Ecommerce.entity.WatchList;
import ecommerce.app.Ecommerce.entity.WatchListItem;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.ProductRepository;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.repository.WatchListItemRepository;
import ecommerce.app.Ecommerce.repository.WatchListRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WatchListService {
    private WatchListRepository watchListRepository;
    private WatchListItemRepository watchListItemRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    public WatchListService(WatchListRepository watchListRepository, WatchListItemRepository watchListItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.watchListRepository = watchListRepository;
        this.watchListItemRepository = watchListItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WatchListResponseDto add(WatchListReqDto reqDto , Long userId){
        Product product = productRepository.findById(reqDto.getProductsId()).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        WatchList watchList;
        Optional<WatchList> exist = watchListRepository.findByUserId(userId);
        if(exist.isPresent()){
            watchList = exist.get();
        }
        else{
            User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
            watchList = new WatchList();
            watchList.setCreatedAt(LocalDateTime.now());
            watchList.setUser(user);
            watchList.setUpdatedAt(LocalDateTime.now());
        }
         WatchListResponseDto watchListResponseDto = new WatchListResponseDto();
        Optional<WatchListItem> existItem  = watchListItemRepository.findByProductIdAndWatchListId(product.getId() , watchList.getId());
        if(existItem.isPresent()){
            watchListResponseDto.setMessage("This products is already in you watchlist");
            return  watchListResponseDto;
        }
        WatchListItem watchListItem = new WatchListItem();
        watchListItem.setWatchList(watchList);
        watchListItem.setProduct(product);
        watchListItem.setAddedAt(LocalDateTime.now());

        watchList.getItems().add(watchListItem);
        watchListRepository.save(watchList);

        watchListResponseDto.setMessage("Product  added in your watchList");
        return  watchListResponseDto;
    }


    public  WatchListResponseDto delete(WatchListReqDto reqDto, Long userId) {
        Product product = productRepository.findById(reqDto.getProductsId()).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        WatchList watchList = watchListRepository.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("Your watchlist not found"));

        WatchListItem watchListItem = watchListItemRepository.findByProductIdAndWatchListId(product.getId() , watchList.getId()).orElseThrow(()->new ResourceNotFoundException("WatchList item not found"));
        watchListItemRepository.delete(watchListItem);

      WatchListResponseDto watchListResponseDto =  new WatchListResponseDto();
      watchListResponseDto.setMessage("Delete successfully done");
      return watchListResponseDto;
    }

    public  WatchListDetailDto getWatchList(Long userId) {
        WatchList watchList = watchListRepository.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("Watchlist not found"));
        WatchListDetailDto watchListDetailDto = new WatchListDetailDto();

         if(watchList.getItems() != null && !watchList.getItems().isEmpty()){
             for(WatchListItem item : watchList.getItems()){
                 Product product = item.getProduct();
                 ProductDetailsDto productDetailsDto = getProductsDetails(product);

                 watchListDetailDto.getWatchListItems().add(productDetailsDto);
             }
         }

         return watchListDetailDto;
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
