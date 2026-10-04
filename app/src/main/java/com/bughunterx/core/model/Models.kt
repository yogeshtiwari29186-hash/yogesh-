package com.bughunterx.core.model
enum class ScanState { READY, VALIDATED, ANALYZING, COMPLETE, BLOCKED }
enum class Severity { INFO, LOW, MEDIUM, HIGH, CRITICAL }
data class Finding(val title:String,val description:String,val severity:Severity)
data class ScopeTarget(val value:String,val authorized:Boolean)
data class EvidenceItem(val id:String,val type:String,val sensitive:Boolean,val redactedPreview:String)
