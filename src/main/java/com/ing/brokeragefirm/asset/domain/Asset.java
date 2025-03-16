package com.ing.brokeragefirm.asset.domain;


import com.ing.brokeragefirm.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "T_ASSET", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMER_ID", "NAME"}))
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SIZE")
    private Double size;

    @Column(name = "USABLE_SIZE")
    private Double usableSize;

}