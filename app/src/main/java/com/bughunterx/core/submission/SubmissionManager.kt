package com.bughunterx.core.submission

import com.bughunterx.core.evidence.EvidenceVault
import com.bughunterx.core.model.Finding
import java.util.UUID

object SubmissionManager {
    private val snapshots = mutableListOf<SubmissionSnapshot>()

    fun create(
        finding: Finding?,
        reportBody: String,
        evidenceIds: Set<String>,
        approved: Boolean
    ): SubmissionSnapshot? {
        if (!approved || evidenceIds.isEmpty() || reportBody.isBlank()) return null

        val validIds = EvidenceVault.select(evidenceIds).map { it.id }
        if (validIds.isEmpty()) return null

        val snapshot = SubmissionSnapshot(
            findingTitle = finding?.title ?: "Security finding",
            body = reportBody,
            evidenceIds = validIds,
            approved = true
        )
        snapshots += snapshot
        return snapshot
    }

    fun all(): List<SubmissionSnapshot> = snapshots.toList()

    fun latest(): SubmissionSnapshot? = snapshots.lastOrNull()

    fun clear() {
        snapshots.clear()
    }
}
