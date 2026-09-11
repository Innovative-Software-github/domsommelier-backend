package com.innovativesoftware.domsommelier_backend.customer_management.discount;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Единственная точка, которая отвечает на вопрос «какая скидка у этого клиента».
 *
 * <p>Сейчас это просто процент на {@link Customer}. Когда появятся уровни лояльности
 * (Серебро/Золото по сумме заказов), меняется только реализация этих двух методов —
 * корзина, заказ, DTO и UI остаются как есть.
 */
@Service
@RequiredArgsConstructor
public class CustomerDiscountResolver {

    public static final int NO_DISCOUNT = 0;
    /** Верхняя граница на любую скидку — страховка от опечатки в админке. */
    public static final int MAX_PERCENT = 100;

    private final CustomerRepository customerRepository;

    /** Действующий процент личной скидки. {@code 0} — скидки нет. */
    public int resolvePercent(Customer customer) {
        if (customer == null || customer.getDiscountPercent() == null) {
            return NO_DISCOUNT;
        }
        return clamp(customer.getDiscountPercent());
    }

    /**
     * То же по id. Корзина зовёт это на каждом пересчёте, а не хранит процент внутри себя:
     * иначе смена скидки в админке не подхватилась бы в уже собранной корзине.
     * Неизвестный клиент (гость) — скидки нет.
     */
    @Transactional(readOnly = true)
    public int resolvePercent(UUID customerId) {
        if (customerId == null) {
            return NO_DISCOUNT;
        }
        return customerRepository.findById(customerId)
                .map(this::resolvePercent)
                .orElse(NO_DISCOUNT);
    }

    private int clamp(int percent) {
        return Math.max(NO_DISCOUNT, Math.min(MAX_PERCENT, percent));
    }
}
