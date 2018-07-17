package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrderTests {

    private Order order;
    private EventLog eventLog;
    private KitchenOrderRef ref;
    private OrderRef orderRef;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        ref = new KitchenOrderRef();
        orderRef = new OrderRef();
        order = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void can_build_new_order() {
        assertThat(order).isNotNull();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void new_order_is_new() {
        assertThat(order.isNew()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void start_order_prep_updates_state() {
        order.startPrep();
        assertThat(order.isPrepping()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void only_new_order_can_start_prep() {
        order.startPrep();
        assertThatIllegalStateException().isThrownBy(order::startPrep);
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void finish_order_prep_updates_state() {
        order.startPrep();
        order.finishPrep();
        assertThat(order.hasFinishedPrep()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void only_prepping_order_can_finish_prep() {
        assertThatIllegalStateException().isThrownBy(order::finishPrep);
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void start_order_bake_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        assertThat(order.isBaking()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void only_prepped_order_can_start_bake() {
        assertThatIllegalStateException().isThrownBy(order::startBake);
    }

    /**
     * TODO: Lab 1
     */
    @SuppressWarnings("Duplicates")
    @Test
    public void finish_order_bake_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        assertThat(order.hasFinishedBaking()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void only_baking_order_can_finish_bake() {
        assertThatIllegalStateException().isThrownBy(order::finishBake);
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void start_order_assembly_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        assertThat(order.hasStartedAssembly()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void only_baked_order_can_start_assembly() {
        assertThatIllegalStateException().isThrownBy(order::startAssembly);
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void finish_order_assembly_updates_state() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        order.finishAssembly();
        assertThat(order.hasFinishedAssembly()).isTrue();
    }

    /**
     * TODO: Lab 1
     */
    @Test
    public void only_assembling_order_can_finish_assembly() {
        assertThatIllegalStateException().isThrownBy(order::finishAssembly);
    }

    /**
     * TODO: Lab 2
     */
    @Test
    public void start_order_prep_fires_event() {
        order.startPrep();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepStartedEvent.class));
    }

    /**
     * TODO: Lab 2
     */
    @Test
    public void finish_order_prep_fires_event() {
        order.startPrep();
        order.finishPrep();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepFinishedEvent.class));
    }

    /**
     * TODO: Lab 2
     */
    @Test
    public void start_order_bake_fires_event() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeStartedEvent.class));
    }

    /**
     * TODO: Lab 2
     */
    @Test
    public void finish_order_bake_fires_event() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeFinishedEvent.class));
    }

    /**
     * TODO: Lab 2
     */
    @Test
    public void start_order_assembly_fires_event() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderAssemblyStartedEvent.class));
    }

    /**
     * TODO: Lab 2
     */
    @Test
    public void finish_order_assembly_fires_event() {
        order.startPrep();
        order.finishPrep();
        order.startBake();
        order.finishBake();
        order.startAssembly();
        order.finishAssembly();
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderPrepFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderBakeFinishedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderAssemblyStartedEvent.class));
        verify(eventLog).publish(eq(new Topic("kitchen_orders")), isA(OrderAssemblyFinishedEvent.class));
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderAddedEvent_returns_order() {
        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        assertThat(order.accumulatorFunction().apply(order.identity(), orderAddedEvent)).isEqualTo(order);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderPrepStartedEvent_returns_order() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
        expectedOrder.startPrep();

        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        order.accumulatorFunction().apply(order.identity(), orderAddedEvent);

        OrderPrepStartedEvent orderPrepStartedEvent = new OrderPrepStartedEvent(ref);
        assertThat(order.accumulatorFunction().apply(order, orderPrepStartedEvent)).isEqualTo(expectedOrder);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderPrepFinishedEvent_returns_order() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
        expectedOrder.startPrep();
        expectedOrder.finishPrep();

        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        order.accumulatorFunction().apply(order.identity(), orderAddedEvent);

        OrderPrepStartedEvent orderPrepStartedEvent = new OrderPrepStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepStartedEvent);

        OrderPrepFinishedEvent orderPrepFinishedEvent = new OrderPrepFinishedEvent(ref);
        assertThat(order.accumulatorFunction().apply(order, orderPrepFinishedEvent)).isEqualTo(expectedOrder);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderBakeStartedEvent_returns_order() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
        expectedOrder.startPrep();
        expectedOrder.finishPrep();
        expectedOrder.startBake();

        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        order.accumulatorFunction().apply(order.identity(), orderAddedEvent);

        OrderPrepStartedEvent orderPrepStartedEvent = new OrderPrepStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepStartedEvent);

        OrderPrepFinishedEvent orderPrepFinishedEvent = new OrderPrepFinishedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepFinishedEvent);

        OrderBakeStartedEvent orderBakeStartedEvent = new OrderBakeStartedEvent(ref);
        assertThat(order.accumulatorFunction().apply(order, orderBakeStartedEvent)).isEqualTo(expectedOrder);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderBakeStartedFinished_returns_order() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
        expectedOrder.startPrep();
        expectedOrder.finishPrep();
        expectedOrder.startBake();
        expectedOrder.finishBake();

        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        order.accumulatorFunction().apply(order.identity(), orderAddedEvent);

        OrderPrepStartedEvent orderPrepStartedEvent = new OrderPrepStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepStartedEvent);

        OrderPrepFinishedEvent orderPrepFinishedEvent = new OrderPrepFinishedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepFinishedEvent);

        OrderBakeStartedEvent orderBakeStartedEvent = new OrderBakeStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderBakeStartedEvent);

        OrderBakeFinishedEvent orderBakeFinishedEvent = new OrderBakeFinishedEvent(ref);
        assertThat(order.accumulatorFunction().apply(order, orderBakeFinishedEvent)).isEqualTo(expectedOrder);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderAssemblyStartedEvent_returns_order() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
        expectedOrder.startPrep();
        expectedOrder.finishPrep();
        expectedOrder.startBake();
        expectedOrder.finishBake();
        expectedOrder.startAssembly();

        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        order.accumulatorFunction().apply(order.identity(), orderAddedEvent);

        OrderPrepStartedEvent orderPrepStartedEvent = new OrderPrepStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepStartedEvent);

        OrderPrepFinishedEvent orderPrepFinishedEvent = new OrderPrepFinishedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepFinishedEvent);

        OrderBakeStartedEvent orderBakeStartedEvent = new OrderBakeStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderBakeStartedEvent);

        OrderBakeFinishedEvent orderBakeFinishedEvent = new OrderBakeFinishedEvent(ref);
        order.accumulatorFunction().apply(order, orderBakeFinishedEvent);

        OrderAssemblyStartedEvent orderAssemblyStartedEvent = new OrderAssemblyStartedEvent(ref);
        assertThat(order.accumulatorFunction().apply(order, orderAssemblyStartedEvent)).isEqualTo(expectedOrder);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void accumulator_apply_with_orderAssemblyFinishedEvent_returns_order() {
        Order expectedOrder = Order.builder()
                .ref(ref)
                .orderRef(orderRef)
                .eventLog(eventLog)
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.SMALL).build())
                .pizza(Order.Pizza.builder().size(Order.Pizza.Size.MEDIUM).build())
                .build();
        expectedOrder.startPrep();
        expectedOrder.finishPrep();
        expectedOrder.startBake();
        expectedOrder.finishBake();
        expectedOrder.startAssembly();
        expectedOrder.finishAssembly();

        OrderAddedEvent orderAddedEvent = new OrderAddedEvent(ref, order.state());
        order.accumulatorFunction().apply(order.identity(), orderAddedEvent);

        OrderPrepStartedEvent orderPrepStartedEvent = new OrderPrepStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepStartedEvent);

        OrderPrepFinishedEvent orderPrepFinishedEvent = new OrderPrepFinishedEvent(ref);
        order.accumulatorFunction().apply(order, orderPrepFinishedEvent);

        OrderBakeStartedEvent orderBakeStartedEvent = new OrderBakeStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderBakeStartedEvent);

        OrderBakeFinishedEvent orderBakeFinishedEvent = new OrderBakeFinishedEvent(ref);
        order.accumulatorFunction().apply(order, orderBakeFinishedEvent);

        OrderAssemblyStartedEvent orderAssemblyStartedEvent = new OrderAssemblyStartedEvent(ref);
        order.accumulatorFunction().apply(order, orderAssemblyStartedEvent);

        OrderAssemblyFinishedEvent orderAssemblyFinishedEvent = new OrderAssemblyFinishedEvent(ref);
        assertThat(order.accumulatorFunction().apply(order, orderAssemblyFinishedEvent)).isEqualTo(expectedOrder);
    }
}
