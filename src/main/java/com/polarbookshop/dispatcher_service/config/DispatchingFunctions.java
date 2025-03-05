package com.polarbookshop.dispatcher_service.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polarbookshop.dispatcher_service.domain.OrderAcceptedMessage;
import com.polarbookshop.dispatcher_service.domain.OrderDispatchedMessage;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
public class DispatchingFunctions {

	@Bean
	Function<OrderAcceptedMessage, Long> pack() {
		return orderAcceptedMessage -> {
			log.info("The order with id {} is packed.", orderAcceptedMessage.orderId());
			return orderAcceptedMessage.orderId();
		};

	}

	@Bean
	Function<Flux<Long>, Flux<OrderDispatchedMessage>> label() {
		return orderFlux -> orderFlux.map(orderId -> {
			log.info("The order with id {} is labeled.", orderId);
			return new OrderDispatchedMessage(orderId);
		});
	}

}
