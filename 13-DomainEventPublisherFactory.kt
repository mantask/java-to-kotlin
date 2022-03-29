fun interface DomainEventPublisher {
    suspend fun publish(entity: Identifiable<UUID>, event: DomainEvent, userId: UUID?)

    suspend fun publish(entity: Identifiable<UUID>, event: DomainEvent) =
        publish(entity, event, null)
}

internal class DomainEventPublisherFactory(
    private val kafkaSender: KafkaSender<String, Any>,
    private val topicResolver: DomainEventAutoConfig.DomainEventTopicResolver,
) {
    fun build() =
        DomainEventPublisher { entity, event, userId ->
            val rec = ProducerRecord(topicResolver.resolve(entity), entity.id?.toString().orEmpty(), event as Any).apply {
                // TODO(MK): Consider using faster (but less secure) AlternativeJdkIdGenerator
                headers().add(HEADER_EVENT_ID, UUID.randomUUID().toByteArray())
                headers().add(HEADER_ENTITY, entity::class.jvmName.toByteArray())
                userId?.let { headers().add(HEADER_USER_ID, it.toByteArray()) }
            }.let { SenderRecord.create(it, it.key()) }
            kafkaSender.send(rec.toMono()).awaitSingle()
        }
}

fun String.toUuid(): UUID =
    UUID.fromString(this)

fun UUID.toByteArray(): ByteArray =
    toString().encodeToByteArray()
