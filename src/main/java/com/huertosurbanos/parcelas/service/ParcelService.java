package com.huertosurbanos.parcelas.service;

import com.huertosurbanos.parcelas.dto.ParcelRequest;
import com.huertosurbanos.parcelas.dto.ParcelResponse;
import com.huertosurbanos.parcelas.model.Parcel;
import com.huertosurbanos.parcelas.repository.ParcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    @Transactional
    public ParcelResponse create(ParcelRequest request) {
        Parcel parcel = new Parcel();
        parcel.setName(request.name());
        parcel.setLatitude(request.latitude());
        parcel.setLongitude(request.longitude());
        parcel.setSoilType(request.soilType());
        parcel.setOwnerOrLead(request.ownerOrLead());
        Parcel saved = parcelRepository.save(parcel);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ParcelResponse> findAll() {
        return parcelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParcelResponse findById(UUID id) {
        return parcelRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la parcela con id " + id));
    }

    private ParcelResponse toResponse(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId(),
                parcel.getName(),
                parcel.getLatitude(),
                parcel.getLongitude(),
                parcel.getSoilType(),
                parcel.getOwnerOrLead(),
                parcel.getCreatedAt()
        );
    }
}
