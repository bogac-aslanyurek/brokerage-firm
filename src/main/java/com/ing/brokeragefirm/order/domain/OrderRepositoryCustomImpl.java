package com.ing.brokeragefirm.order.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Order> searchOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Order> query = cb.createQuery(Order.class);
        final Root<Order> root = query.from(Order.class);

        if (customerId != null) {
            query.where(cb.equal(root.get("customer").get("id"), customerId));
        }
        if (startDate != null && endDate != null) {
            query.where(cb.between(root.get("createDate"), startDate, endDate));
        }

        final TypedQuery<Order> resultQuery = entityManager.createQuery(query);
        return resultQuery.getResultList();
    }
}
