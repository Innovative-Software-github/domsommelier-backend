package com.innovativesoftware.domsommelier_backend.saved_management;

import com.innovativesoftware.domsommelier_backend.infrastructure.RedisService;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedDto;
import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedItemDto;
import com.innovativesoftware.domsommelier_backend.saved_management.service.SavedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Добавление/удаление в избранном идемпотентны: повтор из вкладки с устаревшим
 * состоянием возвращает актуальный список, а не 400/404.
 */
@ExtendWith(MockitoExtension.class)
class SavedServiceTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID SAVED_PRODUCT_ID = UUID.randomUUID();
    private static final String SAVED_KEY = "saved:" + CUSTOMER_ID;

    @Mock
    private RedisService redisService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SavedService savedService;

    private SavedDto saved;

    @BeforeEach
    void setUp() {
        saved = SavedDto.builder()
                .customerId(CUSTOMER_ID)
                .item(SavedItemDto.builder()
                        .product(SavedItemDto.SavedProductDto.builder()
                                .id(SAVED_PRODUCT_ID)
                                .name("Casillero del Diablo")
                                .build())
                        .build())
                .build();
    }

    @Test
    void addItem_alreadySaved_returnsCurrentListWithoutWriting() {
        when(productRepository.findById(SAVED_PRODUCT_ID)).thenReturn(Optional.of(mock(Product.class)));
        when(redisService.getObject(SAVED_KEY, SavedDto.class)).thenReturn(saved);

        SavedDto result = savedService.addItem(CUSTOMER_ID, SAVED_PRODUCT_ID);

        assertThat(result.getItems())
                .extracting(item -> item.getProduct().getId())
                .containsExactly(SAVED_PRODUCT_ID);
        verify(redisService, never()).save(anyString(), any());
    }

    @Test
    void addItem_unknownProduct_stillFails() {
        UUID unknownProductId = UUID.randomUUID();
        when(productRepository.findById(unknownProductId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savedService.addItem(CUSTOMER_ID, unknownProductId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void removeItem_notInSaved_returnsCurrentListWithoutWriting() {
        when(redisService.getObject(SAVED_KEY, SavedDto.class)).thenReturn(saved);

        SavedDto result = savedService.removeItem(CUSTOMER_ID, UUID.randomUUID());

        assertThat(result.getItems())
                .extracting(item -> item.getProduct().getId())
                .containsExactly(SAVED_PRODUCT_ID);
        verify(redisService, never()).save(anyString(), any());
    }

    @Test
    void removeItem_saved_removesAndWrites() {
        when(redisService.getObject(SAVED_KEY, SavedDto.class)).thenReturn(saved);

        SavedDto result = savedService.removeItem(CUSTOMER_ID, SAVED_PRODUCT_ID);

        assertThat(result.getItems()).isEmpty();
        verify(redisService).save(SAVED_KEY, result);
    }
}
