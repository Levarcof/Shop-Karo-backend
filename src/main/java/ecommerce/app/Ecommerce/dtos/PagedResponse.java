package ecommerce.app.Ecommerce.dtos;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElement,
        int totalPage,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
){
    public  static  <T> PagedResponse<T> from(Page<T> result){
        return  new PagedResponse<>(result.getContent() , result.getNumber() , result.getSize(),
                result.getTotalElements() , result.getTotalPages() , result.isFirst() , result.isLast() ,
                result.hasNext() , result.hasPrevious());

    }
}

