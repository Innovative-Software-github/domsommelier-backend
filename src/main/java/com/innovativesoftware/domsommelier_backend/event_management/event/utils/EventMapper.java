package com.innovativesoftware.domsommelier_backend.event_management.event.utils;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.entity.EventPhoto;
import com.innovativesoftware.domsommelier_backend.event_management.event.entity.FaqItem;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.FaqItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    private EventMapper() {
    }

    public static EventDTO toDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setType(event.getType());
        dto.setPrice(event.getPrice());
        dto.setDatetime(event.getDatetime());
        dto.setTitle(event.getTitle());
        dto.setSmallCover(event.getSmallCover());
        dto.setLargeCover(event.getLargeCover());
        dto.setCity(event.getCity());
        dto.setAddress(event.getAddress());
        dto.setDescription(event.getDescription());
        dto.setRegistrationLink(event.getRegistrationLink());
        dto.setAbout(event.getAbout());
        dto.setHowItGoes(event.getHowItGoes());
        dto.setFaq(toFaqDtos(event.getFaqItems()));
        dto.setWineStoreId(resolveWineStoreId(event));
        return dto;
    }

    public static void applyScalars(Event event, EventDTO dto) {
        event.setType(dto.getType());
        event.setPrice(dto.getPrice());
        event.setDatetime(dto.getDatetime());
        event.setTitle(dto.getTitle());
        event.setSmallCover(dto.getSmallCover());
        event.setLargeCover(dto.getLargeCover());
        event.setDescription(dto.getDescription());
        event.setRegistrationLink(dto.getRegistrationLink());
        event.setAbout(dto.getAbout());
        event.setHowItGoes(dto.getHowItGoes());
        applyFaqItems(event, dto.getFaq());
    }

    /**
     * Заменяет FAQ целиком, переиспользуя существующий список Hibernate
     * (на update нельзя подменять ссылку на управляемую коллекцию — см. тот же
     * приём в AbstractProductWriteStrategy.replaceStrings).
     */
    private static void applyFaqItems(Event event, List<FaqItemDto> faqDtos) {
        List<FaqItem> target = event.getFaqItems();
        if (target == null) {
            target = new ArrayList<>();
            event.setFaqItems(target);
        }
        target.clear();
        if (faqDtos != null) {
            faqDtos.stream()
                    .filter(item -> item != null && (isNotBlank(item.getQuestion()) || isNotBlank(item.getAnswer())))
                    .map(item -> new FaqItem(trimToNull(item.getQuestion()), trimToNull(item.getAnswer())))
                    .forEach(target::add);
        }
    }

    private static List<FaqItemDto> toFaqDtos(List<FaqItem> faqItems) {
        if (faqItems == null) {
            return List.of();
        }
        return faqItems.stream()
                .map(item -> {
                    FaqItemDto dto = new FaqItemDto();
                    dto.setQuestion(item.getQuestion());
                    dto.setAnswer(item.getAnswer());
                    return dto;
                })
                .toList();
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static void applyWineStoreLocation(Event event, WineStore wineStore) {
        event.setWineStore(wineStore);
        event.setCity(wineStore.getCity());
        event.setAddress(wineStore.getAddress());
    }

    public static EventFullDTO toFullDto(Event event) {
        String cover = resolveCover(event);
        return EventFullDTO.builder()
                .id(event.getId().toString())
                .type(event.getType())
                .price(event.getPrice())
                .dateTime(formatToIsoWithMillisZ(event.getDatetime()))
                .title(event.getTitle())
                .smallCover(cover)
                .largeCover(cover)
                .city(event.getCity())
                .address(event.getAddress())
                .description(event.getDescription())
                .registrationLink(event.getRegistrationLink())
                .about(event.getAbout())
                .howItGoes(event.getHowItGoes())
                .faq(toFaqDtos(event.getFaqItems()))
                .wineStoreId(resolveWineStoreId(event))
                .wineStoreName(resolveWineStoreName(event))
                .build();
    }

    public static EventListDTO toListDto(Event event) {
        return EventListDTO.builder()
                .id(event.getId().toString())
                .type(event.getType())
                .price(event.getPrice())
                .dateTime(formatToIsoWithMillisZ(event.getDatetime()))
                .title(event.getTitle())
                .smallCover(resolveCover(event))
                .city(event.getCity())
                .wineStoreId(resolveWineStoreId(event))
                .wineStoreName(resolveWineStoreName(event))
                .build();
    }

    /**
     * Event.smallCover/largeCover никогда реально не заполняются (в админке это
     * скрытые пустые поля) — обложкой считаем первое реально загруженное фото
     * из EventPhotoManager (как productPhoto[0] у товаров).
     */
    private static String resolveCover(Event event) {
        List<EventPhoto> photos = event.getPhotos();
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        return photos.get(0).getUrl();
    }

    private static Long resolveWineStoreId(Event event) {
        WineStore wineStore = event.getWineStore();
        return wineStore != null ? wineStore.getId() : null;
    }

    private static String resolveWineStoreName(Event event) {
        WineStore wineStore = event.getWineStore();
        return wineStore != null ? wineStore.getName() : null;
    }

    private static String formatToIsoWithMillisZ(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_INSTANT);
    }
}
