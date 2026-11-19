package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.DashboardOverviewDto;
import ecommerce.app.Ecommerce.dtos.LowStockProductDto;
import ecommerce.app.Ecommerce.dtos.MonthlySalesDto;
import ecommerce.app.Ecommerce.dtos.TopProductDto;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.repository.DashboardRepository;
import ecommerce.app.Ecommerce.repository.MonthlySalesProjection;
import ecommerce.app.Ecommerce.repository.ProductRepository;
import ecommerce.app.Ecommerce.repository.TopProductProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int OVERVIEW_LOW_STOCK_LIMIT = 100;
    private static final int LOW_STOCK_PAGE_LIMIT = 500;

    private static final List<OrderStatus> COUNTED = List.of(
            OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PROCESSING,
            OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    private static final List<OrderStatus> NEEDS_ACTION = List.of(
            OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PROCESSING );
    private DashboardRepository dashboardRepository;
    private ProductRepository productRepository;


    public DashboardService(DashboardRepository dashboardRepository , ProductRepository productRepository) {
        this.dashboardRepository = dashboardRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public DashboardOverviewDto getOverview(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        Long totalOrders =
                dashboardRepository.getTotalOrders(
                        startDate, endDate,COUNTED
                );

        Long totalCustomers =
                dashboardRepository.getTotalCustomers();

        Long pendingOrders =
                dashboardRepository.getPendingOrders(NEEDS_ACTION);

        List<LowStockProductDto> lowStock = dashboardRepository.findLowStockProducts(
                LOW_STOCK_THRESHOLD, PageRequest.of(0, OVERVIEW_LOW_STOCK_LIMIT));


        BigDecimal totalRevenue = dashboardRepository.getTotalRevenue(OrderStatus.DELIVERED , startDate , endDate);
        Long paidOrders = dashboardRepository.countPaidOrders(OrderStatus.DELIVERED , startDate , endDate);

        Long totalProducts = productRepository.getTotalProducts();

        BigDecimal averageOrderValue = BigDecimal.ZERO;

        if(paidOrders != null && paidOrders >0){
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(paidOrders) , 2 , RoundingMode.HALF_UP);
        }

        return new DashboardOverviewDto(
                totalRevenue,
                totalOrders,
                totalCustomers,
                pendingOrders,
                totalProducts , // Replace with total products query
                lowStock,
                averageOrderValue
        );
    }

    @Transactional(readOnly = true)
    public List<LowStockProductDto> getLowStockProducts() {
        return dashboardRepository.findLowStockProducts(
                LOW_STOCK_THRESHOLD, PageRequest.of(0, LOW_STOCK_PAGE_LIMIT));
    }




    @Transactional
    public List<MonthlySalesDto> getMonthlySales(Integer year) {

        if (year == null || year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Invalid year");
        }

        List<MonthlySalesProjection> sales =
                dashboardRepository.findMonthlySales(year , COUNTED);

        Map<Integer, MonthlySalesProjection> salesMap =
                sales.stream()
                        .collect(Collectors.toMap(
                                MonthlySalesProjection::getMonth,
                                Function.identity()
                        ));

        List<MonthlySalesDto> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {

            MonthlySalesProjection data = salesMap.get(month);

            result.add(new MonthlySalesDto(
                    month,
                    Month.of(month).getDisplayName(
                            TextStyle.FULL,
                            Locale.ENGLISH
                    ),
                    data != null ? data.getTotalOrders() : 0L,
                    data != null ? data.getTotalSales() : BigDecimal.ZERO
            ));
        }

        return result;
    }


    @Transactional
    public List<TopProductDto> getTopProducts(
            LocalDateTime from,
            LocalDateTime to
    ) {

        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException(
                    "Invalid date range"
            );
        }

        List<TopProductProjection> products =
                dashboardRepository.findTopProducts(
                        from,
                        to,
                        COUNTED,
                        PageRequest.of(0, 10)
                );

        return products.stream()
                .map(product -> new TopProductDto(
                        product.getProductId(),
                        product.getProductName(),
                        product.getSku(),
                        product.getTotalQuantitySold(),
                        product.getTotalRevenue(),
                        product.getImageUrl()
                ))
                .toList();
    }


}
