package com.bughunterx.core.report

import com.bughunterx.core.submission.SubmissionSnapshot

object ReportExporter {
    fun markdown(snapshot: SubmissionSnapshot): String = buildString {
        appendLine("# BugHunter X Submission")
        appendLine()
        appendLine("## Finding")
        appendLine(snapshot.findingTitle)
        appendLine()
        appendLine("## Report")
        appendLine(snapshot.body)
        appendLine()
        appendLine("## Evidence")
        snapshot.evidenceIds.forEachIndexed { index, id ->
            appendLine("${index + 1}. $id")
        }
        appendLine()
        appendLine("## Approval")
        appendLine("Approved: ${snapshot.approved}")
    }

    fun json(snapshot: SubmissionSnapshot): String {
        val title = escape(snapshot.findingTitle)
        val body = escape(snapshot.body)
        val ids = snapshot.evidenceIds.joinToString(",") { ""${escape(it)}"" }
        return """{"findingTitle":"$title","body":"$body","evidenceIds":[$ids],"approved":${snapshot.approved}}"""
    }

    fun html(snapshot: SubmissionSnapshot): String {
        val title = escapeHtml(snapshot.findingTitle)
        val body = escapeHtml(snapshot.body).replace("
", "<br>")
        val evidence = snapshot.evidenceIds.joinToString("") {
            "<li>${escapeHtml(it)}</li>"
        }
        return """<!doctype html>
<html><head><meta charset="utf-8"><title>BugHunter X Submission</title></head>
<body><h1>BugHunter X Submission</h1>
<h2>$title</h2><h3>Report</h3><p>$body</p>
<h3>Evidence</h3><ul>$evidence</ul>
<p>Approved: ${snapshot.approved}</p></body></html>"""
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace(""", "\"")
            .replace("
", "\n").replace("", "\r")

    private fun escapeHtml(value: String): String =
        value.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace(""", "&quot;")
            .replace("'", "&#39;")
}
