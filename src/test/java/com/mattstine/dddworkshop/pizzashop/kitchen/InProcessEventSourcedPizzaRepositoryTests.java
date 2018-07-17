package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderRef;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InProcessEventSourcedPizzaRepositoryTests {

    private PizzaRepository repository;
    private EventLog eventLog;
    private PizzaRef ref;
    private Pizza pizza;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        repository = new InProcessEventSourcedPizzaRepository(eventLog,
                PizzaRef.class,
                Pizza.class,
                Pizza.PizzaState.class,
                PizzaAddedEvent.class,
                new Topic("pizzas"));
        ref = repository.nextIdentity();
        pizza = Pizza.builder()
                .ref(ref)
                .size(Pizza.Size.MEDIUM)
                .orderRef(new OrderRef())
                .eventLog(eventLog)
                .build();
    }

    /**
     * TODO: Lab 3
     */
    @Test
    public void provides_next_identity() {
        assertThat(ref).isNotNull();
    }

    /**
     * TODO: Lab 3
     */
    @Test
    public void add_fires_event() {
        repository.add(pizza);
        PizzaAddedEvent event = new PizzaAddedEvent(ref, pizza.state());
        verify(eventLog).publish(eq(new Topic("pizzas")), eq(event));
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void find_by_ref_hydrates_added_pizza() {
        repository.add(pizza);

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Collections.singletonList(new PizzaAddedEvent(ref, pizza.state())));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void find_by_ref_hydrates_prepping_pizza() {
        repository.add(pizza);
        pizza.startPrep();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void find_by_ref_hydrates_prepped_pizza() {
        repository.add(pizza);
        pizza.startPrep();
        pizza.finishPrep();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref),
                        new PizzaPrepFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void find_by_ref_hydrates_baking_pizza() {
        repository.add(pizza);
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref),
                        new PizzaPrepFinishedEvent(ref),
                        new PizzaBakeStartedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }

    /**
     * TODO: Lab 4
     */
    @Test
    public void find_by_ref_hydrates_baked_pizza() {
        repository.add(pizza);
        pizza.startPrep();
        pizza.finishPrep();
        pizza.startBake();
        pizza.finishBake();

        when(eventLog.eventsBy(new Topic("pizzas")))
                .thenReturn(Arrays.asList(new PizzaAddedEvent(ref, pizza.state()),
                        new PizzaPrepStartedEvent(ref),
                        new PizzaPrepFinishedEvent(ref),
                        new PizzaBakeStartedEvent(ref),
                        new PizzaBakeFinishedEvent(ref)));

        assertThat(repository.findByRef(ref)).isEqualTo(pizza);
    }
}
