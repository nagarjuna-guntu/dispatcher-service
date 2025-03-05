package com.polarbookshop.dispatcher_service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;

import com.polarbookshop.dispatcher_service.domain.OrderAcceptedMessage;
import com.polarbookshop.dispatcher_service.domain.OrderDispatchedMessage;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@FunctionalSpringBootTest
@Disabled("These tests are only necessary when using the functions alone (no bindings)")
public class DispatchingFunctionsIntegrationTests {
	
	@Autowired
	private FunctionCatalog functionCatalog;

	@Test
	void packOrder() {
		Function<OrderAcceptedMessage, Long> pack = functionCatalog.lookup(Function.class, "pack");
		long orderId = 121;
		var input = new OrderAcceptedMessage(orderId);
		assertThat(pack.apply(input)).isEqualTo(orderId);
	}

	@Test
	void labelOrder() {
		Function<Flux<Long>, Flux<OrderDispatchedMessage>> label = functionCatalog.lookup(Function.class, "label");
		Flux<Long> orderId = Flux.just(121L);
		var output = new OrderDispatchedMessage(121L);

		StepVerifier.create(label.apply(orderId))
				.expectNextMatches(dispatchedOrder ->
						dispatchedOrder.equals(output))
				.verifyComplete();
	}

	@Test
	void packAndLabelOrder() {
		Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>> packAndLabel =
				functionCatalog.lookup(Function.class, "pack|label");
		long orderId = 121;
		var input = new OrderAcceptedMessage(orderId);
		var output = new OrderDispatchedMessage(orderId);

		StepVerifier.create(packAndLabel.apply(input))
				.expectNextMatches(dispatchedOrder ->
						dispatchedOrder.equals(output))
				.verifyComplete();
	}

}
