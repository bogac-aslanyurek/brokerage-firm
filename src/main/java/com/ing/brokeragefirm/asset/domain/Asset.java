package com.ing.brokeragefirm.asset.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "T_ASSET", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMER_ID", "ASSET_NAME"}))
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Column(name = "ASSET_NAME")
    private String assetName;

    @Column(name = "SIZE")
    private Double size;

}