package com.innovativesoftware.domsommelier_backend.admin_management.customer.service;

import com.innovativesoftware.domsommelier_backend.admin_management.customer.mapper.CustomerDisplayMapper;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerDetailDto;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerFilterRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerListDto;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerOrderDto;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.spec.CustomerSpecifications;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.mapper.OrderDtoMapper;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerDisplayMapper customerDisplayMapper;
    private final OrderDtoMapper orderDtoMapper;

    @Transactional(readOnly = true)
    public Page<AdminCustomerListDto> getCustomers(AdminCustomerFilterRequest filter, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecifications.fromFilter(filter);
        return customerRepository.findAll(spec, pageable).map(this::mapToListDto);
    }

    @Transactional(readOnly = true)
    public AdminCustomerDetailDto getCustomer(UUID customerId) {
        Customer customer = findCustomerWithDefaultWineStore(customerId);
        return mapToDetailDto(customer);
    }

    @Transactional(readOnly = true)
    public Page<AdminCustomerOrderDto> getCustomerOrders(UUID customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Клиент не найден");
        }
        return orderRepository.findByCustomerId(customerId, pageable).map(this::mapToOrderDto);
    }

    private Customer findCustomerWithDefaultWineStore(UUID customerId) {
        return customerRepository.findWithDefaultWineStoreById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Клиент не найден"));
    }

    private AdminCustomerListDto mapToListDto(Customer customer) {
        WineStore wineStore = customer.getDefaultWineStore();
        return AdminCustomerListDto.builder()
                .id(customer.getId())
                .displayName(customerDisplayMapper.resolveDisplayName(customer))
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .role(customer.getRoleOrDefault())
                .defaultWineStoreName(wineStore != null ? wineStore.getName() : null)
                .build();
    }

    private AdminCustomerDetailDto mapToDetailDto(Customer customer) {
        WineStore wineStore = customer.getDefaultWineStore();
        return AdminCustomerDetailDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .secondName(customer.getSecondName())
                .middleName(customer.getMiddleName())
                .displayName(customerDisplayMapper.resolveDisplayName(customer))
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .role(customer.getRoleOrDefault())
                .defaultWineStoreId(wineStore != null ? wineStore.getId() : null)
                .defaultWineStoreName(wineStore != null ? wineStore.getName() : null)
                .build();
    }

    private AdminCustomerOrderDto mapToOrderDto(Order order) {
        return AdminCustomerOrderDto.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .statusName(order.getOrderStatus().getName())
                .totalAmount(orderDtoMapper.resolveTotalAmount(order))
                .wineStoreName(order.getWineStore() != null ? order.getWineStore().getName() : null)
                .build();
    }
}
