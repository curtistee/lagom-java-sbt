/*
 * 
 */
package com.example.hello.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;
import static com.lightbend.lagom.javadsl.api.Service.topic;

/**
 * When you use call, namedCall, or pathCall, Lagom will make a best effort attempt to map it down to REST in a semantic fashion, 
 * so that means if there is a request message, it will use POST, if there’s none, it will use GET. 
 * 
 * Every service call in Lagom has a request message type and a response message type. When the request or response message 
 * isn’t used, akka.NotUsed can be used in their place. 
 * 
 * Request and response message types fall into two categories: strict and streamed. 
 * A strict message is a single message that can be represented by a simple Java object. 
 * The message will be buffered into memory, and then parsed, for example, as JSON. The service calls here use strict messages.
 * 
 * A streamed message is a message of the type Source. Source is an Akka streams API that allows asynchronous streaming and handling of messages.
 */
public interface HelloService extends Service {

  /**
   * Example: curl http://localhost:9000/api/hello/Alice
   */
  ServiceCall<NotUsed, String> hello(String id);

  /**
   * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
   * "Hi"}' http://localhost:9000/api/hello/Alice
   */
  ServiceCall<GreetingMessage, Done> useGreeting(String id);

  /**
   * This gets published to Kafka.
   */
  Topic<HelloEvent> helloEvents();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("hello").withCalls(
        // These are used for inter-service requests. Lagom also supports WebSockets.
        // Note this uses Play Framework's routing mechanism.    
        pathCall("/api/hello/:id",  this::hello),
        pathCall("/api/hello/:id", this::useGreeting)
      ).withTopics(
        // This creates a topic call descriptor, identified by the given topic id &
        // a reference to a method, which returns a Topic instance. This is a static
        // method & can be chained. All topic data is serialized to JSON by default.    
        topic("hello-events", this::helloEvents)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .withProperty(KafkaProperties.partitionKeyStrategy(), HelloEvent::getName)
      ).withAutoAcl(true);
    // @formatter:on
  }
}
