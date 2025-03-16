package com.ing.brokeragefirm.order.domain;

import com.ing.brokeragefirm.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "T_ORDER")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    @Column(name = "ASSET_NAME")
    private String assetName;

    @Column(name = "ORDER_SIDE")
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    @Column(name = "SIZE")
    private Double size;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private OrderStatus status; //

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    public enum OrderStatus {
        PENDING, MATCHED, CANCELED
    }

    public enum OrderSide {
        BUY, SELL
    }
}