package com.bughunterx.core.security

data class EvidenceSelection(
    val evidenceId: String,
    val includeInSubmission: Boolean
)

object EvidencePolicy {
    fun canSubmit(selected: List<EvidenceSelection>): Boolean =
        selected.any { it.includeInSubmission }
}
