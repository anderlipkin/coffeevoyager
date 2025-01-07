package com.coffeevoyager.core.ui

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

private data class Event<T>(
    val id: Long,
    val data: T
)

class ExactlyOnceEventFlow<T> : Flow<T> {

    private val _events = MutableStateFlow(listOf<Event<T>>())

    fun send(event: T) {
        _events.update { events ->
            events + Event(id = UUID.randomUUID().mostSignificantBits, data = event)
        }
    }

    fun sendDistinct(event: T) {
        _events.update { events ->
            if (events.any { it.data == event }) return
            events + Event(id = UUID.randomUUID().mostSignificantBits, data = event)
        }
    }

    override suspend fun collect(collector: FlowCollector<T>) {
        _events
            .map { events -> events.firstOrNull() }
            .distinctUntilChangedBy { event -> event?.id }
            .collect { event ->
                if (event != null) {
                    collector.emit(event.data)
                    consumeEvent(event)
                }
            }
    }

    private fun consumeEvent(consumedEvent: Event<T>) {
        _events.update { eventQueue ->
            eventQueue.filter { it.id != consumedEvent.id }
        }
    }
}

class ExactlyOnceEventChannelAsFlow<T> private constructor(
    private val events: Channel<T>
) : Flow<T> by events.receiveAsFlow() {

    constructor() : this(Channel(capacity = BUFFERED))

    suspend fun send(event: T) {
        events.send(event)
    }
}
