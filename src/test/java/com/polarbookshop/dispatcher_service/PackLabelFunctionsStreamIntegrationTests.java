package com.polarbookshop.dispatcher_service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarbookshop.dispatcher_service.domain.OrderAcceptedMessage;
import com.polarbookshop.dispatcher_service.domain.OrderDispatchedMessage;

@EnableTestBinder
@SpringBootTest
class PackLabelFunctionsStreamIntegrationTests {
	
	@Autowired
	private InputDestination input;
	
	@Autowired
	private OutputDestination outout;
	
	@Autowired
	private ObjectMapper mapper;

	@Test
	void whenOrderAcceptedThenDispatched() throws IOException {
		long orderId = 21;
		var inputPayload = new OrderAcceptedMessage(orderId);
		Message<OrderAcceptedMessage> inputMessage = MessageBuilder.withPayload(inputPayload).build();
		var expectedOutputPayload = new OrderDispatchedMessage(orderId);
		Message<OrderDispatchedMessage> expectedOutputMessage = MessageBuilder.withPayload(expectedOutputPayload).build();
		
		this.input.send(inputMessage);
		var output = this.outout.receive();
		
		assertThat(mapper.readValue(output.getPayload(), OrderDispatchedMessage.class))
				.isEqualTo(expectedOutputMessage.getPayload());
		
	}

}
