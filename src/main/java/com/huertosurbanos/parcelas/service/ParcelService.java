package com.huertosurbanos.parcelas.service;

import com.huertosurbanos.parcelas.dto.ParcelRequest;
import com.huertosurbanos.parcelas.dto.ParcelResponse;
import com.huertosurbanos.parcelas.exception.DuplicateParcelException;
import com.huertosurbanos.parcelas.exception.ParcelNotFoundException;
import com.huertosurbanos.parcelas.model.Parcel;
import com.huertosurbanos.parcelas.repository.ParcelRepository;
import com.huertosurbanos.parcelas.util.ParcelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelMapper parcelMapper;

    public ParcelService(ParcelRepository parcelRepository, ParcelMapper parcelMapper) {
        this.parcelRepository = parcelRepository;
        this.parcelMapper = parcelMapper;
    }

    @Transactional
    public ParcelResponse create(ParcelRequest request) {
        Parcel parcel = parcelMapper.toEntity(request);

        if (parcelRepository.existsByNameAndOwnerOrLead(parcel.getName(), parcel.getOwnerOrLead())) {
            throw new DuplicateParcelException(parcel.getName(), parcel.getOwnerOrLead());
        }

        try {
            Parcel saved = parcelRepository.save(parcel);
            return parcelMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateParcelException(parcel.getName(), parcel.getOwnerOrLead());
        }
    }

    @Transactional(readOnly = true)
    public List<ParcelResponse> findAll() {
        return parcelRepository.findAll().stream()
                .map(parcelMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParcelResponse findById(UUID id) {
        return parcelRepository.findById(id)
                .map(parcelMapper::toResponse)
                .orElseThrow(() -> new ParcelNotFoundException(id));
    }
}
