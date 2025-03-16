package com.ing.brokeragefirm.order.domain;

import com.ing.brokeragefirm.order.model.ListOrderRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Order> searchOrders(ListOrderRequest request) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Order> query = cb.createQuery(Order.class);
        final Root<Order> root = query.from(Order.class);

        if (request.customerId() != null) {
            query.where(cb.equal(root.get("customer").get("id"), request.customerId()));
        }
        if (request.startDate() != null && request.endDate() != null) {
            query.where(cb.between(root.get("createDate"), request.startDate().atStartOfDay(),
                    request.endDate().atTime(LocalTime.MAX)));
        }

        final TypedQuery<Order> resultQuery = entityManager.createQuery(query);
        return resultQuery.getResultList();
    }
}
