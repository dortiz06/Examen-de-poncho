package com.huertosurbanos.parcelas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "parcels",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_parcel_name_owner",
                columnNames = {"name", "owner_or_lead"}
        )
)
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "soil_type", nullable = false, length = 20)
    private SoilType soilType;

    @Column(name = "owner_or_lead", nullable = false, length = 80)
    private String ownerOrLead;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void assignCreatedAt() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public SoilType getSoilType() {
        return soilType;
    }

    public void setSoilType(SoilType soilType) {
        this.soilType = soilType;
    }

    public String getOwnerOrLead() {
        return ownerOrLead;
    }

    public void setOwnerOrLead(String ownerOrLead) {
        this.ownerOrLead = ownerOrLead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
