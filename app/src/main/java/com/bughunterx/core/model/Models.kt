package com.bughunterx.core.model

enum class ScanState { READY, VALIDATED, ANALYZING, COMPLETE, BLOCKED }
enum class Severity { INFO, LOW, MEDIUM, HIGH, CRITICAL }

data class Finding(val title: String, val description: String, val severity: Severity)
data class ScopeTarget(val value: String, val authorized: Boolean)
data class EvidenceItem(val id: String, val type: String, val sensitive: Boolean, val redactedPreview: String)

data class User(val id: String, val email: String, val displayName: String)
data class Organization(val id: String, val name: String)
data class Workspace(val id: String, val organizationId: String, val name: String)
data class Program(val id: String, val workspaceId: String, val name: String)
data class ScopeRule(val id: String, val programId: String, val assetPattern: String, val allowed: Boolean)
data class Asset(val id: String, val programId: String, val value: String, val inScope: Boolean)
data class Endpoint(val id: String, val assetId: String, val method: String, val path: String)
data class Scan(val id: String, val assetId: String, val state: ScanState)
data class ScanJob(val id: String, val scanId: String, val state: ScanState)
data class EvidenceAccess(val id: String, val evidenceId: String, val actorUserId: String, val action: String)
data class Report(val id: String, val findingId: String, val body: String)
data class Submission(
    val id: String,
    val reportId: String,
    val destination: String,
    val status: String,
    val timestamp: String
)
data class SubmissionSnapshot(
    val id: String,
    val submissionId: String,
    val findingIncluded: Boolean,
    val impactIncluded: Boolean,
    val reproductionIncluded: Boolean,
    val requestIncluded: Boolean,
    val responseIncluded: Boolean,
    val evidenceIds: List<String>,
    val approved: Boolean
)
data class AuditEvent(val id: String, val actorUserId: String, val action: String, val targetType: String, val targetId: String, val timestamp: String)
data class Integration(val id: String, val name: String, val enabled: Boolean)
data class Plugin(val id: String, val name: String, val enabled: Boolean)
data class Notification(val id: String, val userId: String, val title: String, val read: Boolean)
