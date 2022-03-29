@FunctionalInterface
public interface DomainEventPublisher {
    Mono<Void> publish(Persistable<UUID> entity, DomainEvent event, UUID userId);

    default Mono<Void> publish(Persistable<UUID> entity, DomainEvent event) {
        return publish(entity, event, null);
    }
}

@AllArgsConstructor
class DomainEventPublisherFactory {
    private final KafkaSender<String, Object> kafkaSender;
    private final DomainEventTopicResolver topicResolver;

    public DomainEventPublisher build() {
        return (entity, event, userId) -> kafkaSender.createOutbound()
            .send(createProducerRecord(topicResolver.resolve(entity), entity, event, userId))
            .then();
    }

    private Mono<ProducerRecord<String, Object>> createProducerRecord(
        String topic,
        Persistable<UUID> entity,
        Object payload,
        UUID userId
    ) {
        return Mono.fromSupplier(() -> {
            var rec = new ProducerRecord<>(topic, getIdString(entity), payload);
            // TODO(MK): Consider using faster (but less secure) AlternativeJdkIdGenerator
            rec.headers().add(DomainEventHeaders.EVENT_ID, toBytes(UUID.randomUUID()));
            rec.headers().add(DomainEventHeaders.ENTITY, entity.getClass().getName().getBytes());
            Optional.ofNullable(userId)
                .map(this::toBytes)
                .ifPresent(it -> rec.headers().add(DomainEventHeaders.USER_ID, it));
            return rec;
        });
    }

    private byte[] toBytes(UUID id) {
        return id.toString().getBytes();
    }

    private String getIdString(Persistable<UUID> entity) {
        return Optional.ofNullable(entity)
            .map(Persistable::getId)
            .map(UUID::toString)
            .orElse(null);
    }
}

