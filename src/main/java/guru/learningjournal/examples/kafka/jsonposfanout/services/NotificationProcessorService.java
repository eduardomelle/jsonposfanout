/**
 * 
 */
package guru.learningjournal.examples.kafka.jsonposfanout.services;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import guru.learningjournal.examples.kafka.jsonposfanout.bindings.PosListenerBinding;
import guru.learningjournal.examples.kafka.jsonposfanout.model.PosInvoice;
import guru.learningjournal.examples.kafka.model.Notification;
import lombok.extern.log4j.Log4j2;

/**
 * 
 */
@Service
@Log4j2
@EnableBinding(PosListenerBinding.class)
public class NotificationProcessorService {

	@Autowired
	private RecordBuilder recordBuilder;

	@StreamListener("notification-input-channel")
	@SendTo("notification-output-channel")
	public KStream<String, Notification> process(KStream<String, PosInvoice> input) {
		KStream<String, Notification> notificationKStream = input
				.filter((k, v) -> v.getCustomerType().equalsIgnoreCase("PRIME"))
				.mapValues(v -> this.recordBuilder.getNotification(v));
		notificationKStream.foreach((k, v) -> {
			log.info(String.format("Notification:- Key: %s, Value: %s", k, v));
		});

		return notificationKStream;
	}

}
