package it.polito.waii_24.g20.crm.common

enum class MessageStateValue
{
    RECEIVED,
    READ,
    DISCARDED,
    PROCESSING,
    DONE,
    FAILED;

    override fun toString(): String {
        return when (this) {
            RECEIVED -> "received"
            READ -> "read"
            DISCARDED -> "discarded"
            PROCESSING -> "processing"
            DONE -> "done"
            FAILED -> "failed"
        }
    }

    companion object {
        fun stringToEnum(s: String): MessageStateValue {
            return MessageStateValue.valueOf(s.uppercase())
        }

        fun createMessageState(s: String): MessageState {
            val stateValue = MessageStateValue.stringToEnum(s)
            return when (stateValue) {
                RECEIVED -> ReceivedMessageState()
                READ -> ReadMessageState()
                DISCARDED -> DiscardedMessageState()
                PROCESSING -> ProcessingMessageState()
                DONE -> DoneMessageState()
                FAILED -> FailedMessageState()
            }
        }
    }
}

interface MessageState {
    fun getValue(): MessageStateValue
    fun next(nxt: MessageStateValue): MessageState {
        throw IllegalArgumentException("Invalid State transition") //InvalidMessageStateException("Invalid state transition")
    }
}

class ReceivedMessageState : MessageState {
    override fun next(nxt: MessageStateValue): MessageState {
        return when (nxt) {
            MessageStateValue.READ -> ReadMessageState()
            MessageStateValue.DISCARDED -> DiscardedMessageState()
            else -> throw IllegalArgumentException("Invalid State transition") //InvalidMessageStateException("Invalid state transition")
        }
    }

    override fun getValue(): MessageStateValue {
        return MessageStateValue.RECEIVED
    }
}

class ReadMessageState : MessageState {
    override fun next(nxt: MessageStateValue): MessageState {
        return when (nxt) {
            MessageStateValue.DISCARDED -> DiscardedMessageState()
            MessageStateValue.PROCESSING -> ProcessingMessageState()
            MessageStateValue.DONE -> DoneMessageState()
            MessageStateValue.FAILED -> FailedMessageState()
            else -> throw IllegalArgumentException("Invalid State transition") //InvalidMessageStateException("Invalid state transition")
        }
    }

    override fun getValue(): MessageStateValue {
        return MessageStateValue.READ
    }
}

class DiscardedMessageState : MessageState {
    override fun getValue(): MessageStateValue {
        return MessageStateValue.DISCARDED
    }
}

class ProcessingMessageState : MessageState {
    override fun next(nxt: MessageStateValue): MessageState {
        return when (nxt) {
            MessageStateValue.DONE -> DoneMessageState()
            MessageStateValue.FAILED -> FailedMessageState()
            else -> throw IllegalArgumentException("Invalid State transition") //InvalidMessageStateException("Invalid state transition")
        }
    }

    override fun getValue(): MessageStateValue {
        return MessageStateValue.PROCESSING
    }
}

class DoneMessageState : MessageState {
    override fun getValue(): MessageStateValue {
        return MessageStateValue.DONE
    }
}

class FailedMessageState : MessageState {
    override fun getValue(): MessageStateValue {
        return MessageStateValue.FAILED
    }
}