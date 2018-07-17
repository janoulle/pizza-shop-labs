package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Ref;
import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.services.RefStringGenerator;
import lombok.Value;

/**
 * @author Matt Stine
 */
@Value
public final class OrderRef implements Ref {
    public static final OrderRef IDENTITY = new OrderRef("");
    String reference;

    public OrderRef() {
        reference = RefStringGenerator.generateRefString();
    }

    public OrderRef(String reference) {
        this.reference = reference;
    }

    @Override
    public String getReference() {
        return reference;
    }
}
