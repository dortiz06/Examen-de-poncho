package com.huertosurbanos.parcelas.repository;

import com.huertosurbanos.parcelas.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

    boolean existsByNameAndOwnerOrLead(String name, String ownerOrLead);
}
