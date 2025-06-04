package it.polito.waii_24.g20.crm.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import it.polito.waii_24.g20.crm.exceptions.IllegalStateTransitionException
import java.beans.PropertyEditorSupport
import java.util.*

interface JobOfferState {
    fun next(next: JobOfferStateLabel) : JobOfferState {
        throw IllegalStateTransitionException("Cannot transition from $this to $next")
    }
    fun getValue() : JobOfferStateLabel
}

enum class JobOfferStateLabel {
    CREATED,
    SELECTION_PHASE,
    CANDIDATE_PROPOSAL,
    ABORTED,
    CONSOLIDATED,
    DONE;

    override fun toString(): String {
        return when(this) {
            CREATED -> "created"
            SELECTION_PHASE -> "selection_phase"
            CANDIDATE_PROPOSAL -> "candidate_proposal"
            ABORTED -> "aborted"
            CONSOLIDATED -> "consolidated"
            DONE -> "done"
        }
    }

    fun create() : JobOfferState {
        return when (this) {
            CREATED -> CreatedJobOfferState()
            SELECTION_PHASE -> SelectionPhaseJobOfferState()
            CANDIDATE_PROPOSAL -> CandidateProposalJobOfferState()
            CONSOLIDATED -> ConsolidatedJobOfferState()
            ABORTED -> AbortedJobOfferState()
            DONE -> DoneJobOfferState()
        }
    }

    companion object {
        fun parseString(value: String) : JobOfferStateLabel {
            return when(value.lowercase(Locale.getDefault())) {
                "created" -> CREATED
                "selection_phase", "selection phase" -> SELECTION_PHASE
                "candidate_proposal", "candidate proposal" -> CANDIDATE_PROPOSAL
                "aborted" -> ABORTED
                "consolidated" -> CONSOLIDATED
                "done" -> DONE
                else -> throw IllegalArgumentException("Invalid label")
            }
        }
    }
}

class JobOfferStateLabelDeserializer : JsonDeserializer<JobOfferStateLabel>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JobOfferStateLabel {
        return JobOfferStateLabel.parseString(p.text)
    }
}

class JobOfferStateLabelEditor : PropertyEditorSupport() {
    override fun setAsText(text: String) {
        value = JobOfferStateLabel.parseString(text)
    }
}

class CreatedJobOfferState : JobOfferState {
    override fun next(next: JobOfferStateLabel): JobOfferState {
        return when (next) {
            JobOfferStateLabel.SELECTION_PHASE -> SelectionPhaseJobOfferState()
            JobOfferStateLabel.ABORTED -> AbortedJobOfferState()
            else -> throw IllegalStateTransitionException("Invalid state transition from CREATED to $next")
        }
    }

    override fun getValue(): JobOfferStateLabel {
        return JobOfferStateLabel.CREATED
    }
}

class SelectionPhaseJobOfferState : JobOfferState {
    override fun next(next: JobOfferStateLabel): JobOfferState {
        return when (next) {
            JobOfferStateLabel.CANDIDATE_PROPOSAL -> CandidateProposalJobOfferState()
            JobOfferStateLabel.ABORTED -> AbortedJobOfferState()
            else -> throw IllegalStateTransitionException("Invalid state transition from SELECTION_PHASE to $next")
        }
    }

    override fun getValue(): JobOfferStateLabel {
        return JobOfferStateLabel.SELECTION_PHASE
    }
}

class CandidateProposalJobOfferState : JobOfferState {
    override fun next(next: JobOfferStateLabel): JobOfferState {
        return when (next) {
            JobOfferStateLabel.CONSOLIDATED -> ConsolidatedJobOfferState()
            JobOfferStateLabel.SELECTION_PHASE -> SelectionPhaseJobOfferState()
            JobOfferStateLabel.ABORTED -> AbortedJobOfferState()
            else -> throw IllegalStateTransitionException("Invalid state transition from CANDIDATE_PROPOSAL to $next")
        }
    }

    override fun getValue(): JobOfferStateLabel {
        return JobOfferStateLabel.CANDIDATE_PROPOSAL
    }
}

class ConsolidatedJobOfferState : JobOfferState {
    override fun next(next: JobOfferStateLabel): JobOfferState {
        return when (next) {
            JobOfferStateLabel.DONE -> DoneJobOfferState()
            JobOfferStateLabel.SELECTION_PHASE -> SelectionPhaseJobOfferState()
            JobOfferStateLabel.ABORTED -> AbortedJobOfferState()
            else -> throw IllegalStateTransitionException("Invalid state transition from CONSOLIDATED to $next")
        }
    }

    override fun getValue(): JobOfferStateLabel {
        return JobOfferStateLabel.CONSOLIDATED
    }
}

class AbortedJobOfferState : JobOfferState {
    override fun getValue(): JobOfferStateLabel {
        return JobOfferStateLabel.ABORTED
    }
}

class DoneJobOfferState : JobOfferState {
    override fun getValue(): JobOfferStateLabel {
        return JobOfferStateLabel.DONE
    }

    override fun next(next: JobOfferStateLabel): JobOfferState {
        return when (next) {
            JobOfferStateLabel.SELECTION_PHASE -> SelectionPhaseJobOfferState()
            else -> throw IllegalStateTransitionException("Invalid state transition from DONE to $next")
        }
    }
}