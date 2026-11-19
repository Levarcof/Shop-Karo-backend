package ecommerce.app.Ecommerce.dtos;

import java.util.ArrayList;
import java.util.List;

public class WatchListDetailDto {
    private List<ProductDetailsDto> watchListItems = new ArrayList<>();

    public List<ProductDetailsDto> getWatchListItems() {
        return watchListItems;
    }

    public void setWatchListItems(List<ProductDetailsDto> watchListItems) {
        this.watchListItems = watchListItems;
    }
}
