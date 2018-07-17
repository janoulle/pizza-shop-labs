package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentService;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Matt Stine
 */
public class OrderServiceTests {

    private EventLog eventLog;
    private OrderRepository repository;
    private OrderService orderService;
    private PaymentService paymentService;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = mock(OrderRepository.class);
        paymentService = mock(PaymentService.class);
        orderService = new OrderService(eventLog, repository, paymentService);
    }

    @Test
    public void subscribes_to_payments_topic() {
        verify(eventLog).subscribe(eq(new Topic("payments")), isA(EventHandler.class));
    }

    @Test
    public void adds_new_order_to_repository() {
        when(repository.nextIdentity()).thenReturn(new OrderRef());
        orderService.createOrder(Order.Type.PICKUP);
        verify(repository).add(isA(Order.class));
    }

    @Test
    public void returns_ref_to_new_order() {
        OrderRef ref = new OrderRef();
        when(repository.nextIdentity()).thenReturn(ref);
        OrderRef orderRef = orderService.createOrder(Order.Type.PICKUP);
        assertThat(orderRef).isEqualTo(orderRef);
    }

    @Test
    public void adds_pizza_to_order() {
        OrderRef orderRef = new OrderRef();
        Order order = Order.builder()
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .ref(orderRef)
                .build();

        when(repository.findByRef(orderRef)).thenReturn(order);

        Pizza pizza = Pizza.builder().size(Pizza.Size.MEDIUM).build();
        orderService.addPizza(orderRef, pizza);

        assertThat(order.getPizzas()).contains(pizza);
    }

    @Test
    public void requests_payment_for_order() {
        OrderRef orderRef = new OrderRef();
        Order order = Order.builder()
                .type(Order.Type.PICKUP)
                .eventLog(eventLog)
                .ref(orderRef)
                .build();
        when(repository.findByRef(orderRef)).thenReturn(order);

        PaymentRef paymentRef = new PaymentRef();
        when(paymentService.createPaymentOf(Amount.of(10, 0))).thenReturn(paymentRef);

        orderService.requestPayment(orderRef);
        assertThat(order.getPaymentRef()).isEqualTo(paymentRef);

        verify(paymentService).requestPaymentFor(eq(paymentRef));
    }

}
