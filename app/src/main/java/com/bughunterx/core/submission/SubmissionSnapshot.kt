package com.bughunterx.core.submission

data class SubmissionSnapshot(
    val findingTitle: String,
    val body: String,
    val evidenceIds: List<String>,
    val approved: Boolean,
    val submissionId: String = "BH-1042",
    val destination: String = "security@example.com",
    val status: String = "SENT",
    val timestamp: String = "2026-10-04 08:34",
    val findingIncluded: Boolean = true,
    val impactIncluded: Boolean = true,
    val reproductionIncluded: Boolean = true,
    val requestIncluded: Boolean = true,
    val responseIncluded: Boolean = true
)
