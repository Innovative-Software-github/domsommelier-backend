package com.innovativesoftware.domsommelier_backend.geo_management.city.service;

import com.innovativesoftware.domsommelier_backend.geo_management.city.entity.City;
import com.innovativesoftware.domsommelier_backend.geo_management.city.model.CityDto;
import com.innovativesoftware.domsommelier_backend.geo_management.city.model.CityRequestDto;
import com.innovativesoftware.domsommelier_backend.geo_management.city.repository.CityRepository;
import com.innovativesoftware.domsommelier_backend.geo_management.city.util.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    /** Активные города для витрины. */
    @Transactional(readOnly = true)
    public List<CityDto> getActiveCities() {
        return cityRepository.findAllByActiveTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(CityMapper::toDto)
                .toList();
    }

    /** Все города (включая неактивные) — для админки. */
    @Transactional(readOnly = true)
    public List<CityDto> getAllCities() {
        return cityRepository.findAllByOrderBySortOrderAscNameAsc()
                .stream()
                .map(CityMapper::toDto)
                .toList();
    }

    /** Проверка существования активного города по slug — для валидации в других сервисах. */
    @Transactional(readOnly = true)
    public boolean existsActiveBySlug(String slug) {
        return cityRepository.findBySlug(normalizeSlug(slug))
                .map(City::isActive)
                .orElse(false);
    }

    @Transactional
    public CityDto create(CityRequestDto request) {
        String slug = normalizeSlug(request.getSlug());
        if (cityRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Город с slug '" + slug + "' уже существует");
        }

        City city = new City();
        city.setSlug(slug);
        city.setName(request.getName().trim());
        city.setActive(request.getActive() == null || request.getActive());
        city.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        return CityMapper.toDto(cityRepository.save(city));
    }

    @Transactional
    public CityDto update(Long id, CityRequestDto request) {
        City city = getOrThrow(id);
        String slug = normalizeSlug(request.getSlug());

        if (cityRepository.existsBySlugAndIdNot(slug, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Город с slug '" + slug + "' уже существует");
        }

        city.setSlug(slug);
        city.setName(request.getName().trim());
        if (request.getActive() != null) {
            city.setActive(request.getActive());
        }
        if (request.getSortOrder() != null) {
            city.setSortOrder(request.getSortOrder());
        }

        return CityMapper.toDto(cityRepository.save(city));
    }

    @Transactional
    public void delete(Long id) {
        City city = getOrThrow(id);
        cityRepository.delete(city);
    }

    private City getOrThrow(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Город не найден: " + id));
    }

    private String normalizeSlug(String slug) {
        return slug == null ? null : slug.trim().toLowerCase();
    }
}
