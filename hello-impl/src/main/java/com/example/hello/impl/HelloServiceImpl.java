package com.example.hello.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.example.hello.api.GreetingMessage;
import com.example.hello.api.HelloService;
import com.example.hello.impl.HelloCommand.SayHello;
import com.example.hello.impl.HelloCommand.UseGreetingMessage;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

/**
 * Implementation of the HelloService.
 */
public class HelloServiceImpl implements HelloService {

  private final PersistentEntityRegistry persistentEntityRegistry;

  @Inject
  public HelloServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
    this.persistentEntityRegistry = persistentEntityRegistry;
    persistentEntityRegistry.register(HelloEntity.class);
  }

  @Override
  public ServiceCall<NotUsed, String> hello(String id) {
    return request -> {
      // Look up the hello entity for the given ID.
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
      // Ask the entity the SayHello command.
      return ref.ask(new SayHello(id));
    };
  }

  @Override
  public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
    return request -> {
      // Look up the hello world entity for the given ID.
      PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
      // Tell the entity to use the greeting message specified.
      return ref.ask(new UseGreetingMessage(request.message));
    };

  }

  @Override
  public Topic<com.example.hello.api.HelloEvent> helloEvents() {
    // We want to publish all the shards of the hello event
    return TopicProducer.taggedStreamWithOffset(HelloEvent.TAG.allTags(), (tag, offset) ->

      // Load the event stream for the passed in shard tag
      persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {

        // Now we want to convert from the persisted event to the published event.
        // Although these two events are currently identical, in future they may
        // change and need to evolve separately, by separating them now we save
        // a lot of potential trouble in future.
        com.example.hello.api.HelloEvent eventToPublish;

        if (eventAndOffset.first() instanceof HelloEvent.GreetingMessageChanged) {
          HelloEvent.GreetingMessageChanged messageChanged = (HelloEvent.GreetingMessageChanged) eventAndOffset.first();
          eventToPublish = new com.example.hello.api.HelloEvent.GreetingMessageChanged(
            messageChanged.getName(), messageChanged.getMessage()
          );
        } else {
          throw new IllegalArgumentException("Unknown event: " + eventAndOffset.first());
        }

        // We return a pair of the translated event, and its offset, so that
        // Lagom can track which offsets have been published.
        return Pair.create(eventToPublish, eventAndOffset.second());
      })
    );
  }
}
