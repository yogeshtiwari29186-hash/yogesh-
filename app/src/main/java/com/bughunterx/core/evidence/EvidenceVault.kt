package com.bughunterx.core.evidence

import com.bughunterx.core.model.EvidenceItem
import com.bughunterx.core.security.SecretProtection
import java.util.UUID

object EvidenceVault {
    private val items = mutableListOf<EvidenceItem>()

    fun add(type: String, value: String, sensitive: Boolean): EvidenceItem {
        val item = EvidenceItem(
            id = UUID.randomUUID().toString(),
            type = type.trim(),
            sensitive = sensitive,
            redactedPreview = if (sensitive) SecretProtection.mask(value) else value.take(160)
        )
        items += item
        return item
    }

    fun all(): List<EvidenceItem> = items.toList()

    fun find(id: String): EvidenceItem? = items.firstOrNull { it.id == id }

    fun select(ids: Set<String>): List<EvidenceItem> =
        ids.mapNotNull(::find)

    fun clear() {
        items.clear()
    }
}
