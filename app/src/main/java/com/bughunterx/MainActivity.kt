package com.bughunterx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bughunterx.core.ScopeGuard
import com.bughunterx.core.analysis.HttpPassiveAnalyzer
import com.bughunterx.core.analysis.PassiveAnalyzer
import com.bughunterx.core.evidence.EvidenceVault
import com.bughunterx.core.model.*
import com.bughunterx.core.report.ReportBuilder
import com.bughunterx.core.report.ReportExporter
import com.bughunterx.core.security.EvidencePolicy
import com.bughunterx.core.security.EvidenceSelection
import com.bughunterx.core.submission.SubmissionManager
import com.bughunterx.core.submission.SubmissionSnapshot
import com.bughunterx.ui.theme.BugHunterXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BugHunterXApp() }
    }
}

@Composable
private fun BugHunterXApp() {
    var target by remember { mutableStateOf("") }
    var authorized by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Ready") }
    var findings by remember { mutableStateOf(emptyList<Finding>()) }
    var report by remember { mutableStateOf("") }
    var evidence by remember { mutableStateOf(EvidenceVault.all()) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var approval by remember { mutableStateOf(false) }
    var snapshot by remember { mutableStateOf<SubmissionSnapshot?>(null) }
    var evidenceType by remember { mutableStateOf("HTTP response") }
    var evidenceValue by remember { mutableStateOf("") }
    var sensitive by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun refresh() { evidence = EvidenceVault.all() }
    fun share(text: String, mime: String) {
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_TEXT, text)
        }, "Export BugHunter X report"))
    }

    BugHunterXTheme {
        Scaffold(topBar = { TopAppBar(title = {
            Column { Text("BugHunter X"); Text("Security Research Dashboard", style = MaterialTheme.typography.labelSmall) }
        }) }) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item { Text("Dashboard", style = MaterialTheme.typography.headlineMedium); Text("Authorized passive security analysis") }
                item {
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Target & Scope", style = MaterialTheme.typography.titleLarge)
                            OutlinedTextField(target, { target = it }, label = { Text("Target URL") }, placeholder = { Text("https://example.com") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            Row { Checkbox(authorized, { authorized = it }); Text("I confirm this target is explicitly authorized.") }
                            Button(enabled = authorized && target.isNotBlank(), onClick = {
                                if (ScopeGuard.isAllowed(target)) {
                                    findings = PassiveAnalyzer.analyzeTarget(target) + HttpPassiveAnalyzer.analyze(target)
                                    status = "Passive analysis completed"
                                    report = ReportBuilder.markdown(target, findings)
                                    EvidenceVault.add("Analysis metadata", "Passive analysis completed for " + target, false)
                                    refresh()
                                } else {
                                    findings = listOf(Finding("Scope blocked", "Use a valid HTTP(S) target and confirm authorization.", Severity.HIGH))
                                    status = "Blocked"; report = ""
                                }
                            }, modifier = Modifier.fillMaxWidth()) { Text("Run Passive Analysis") }
                        }
                    }
                }
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard("Status", status, Modifier.weight(1f)); MetricCard("Findings", findings.size.toString(), Modifier.weight(1f)); MetricCard("Evidence", evidence.size.toString(), Modifier.weight(1f))
                } }
                item { Text("Findings", style = MaterialTheme.typography.titleLarge) }
                items(findings) { finding -> ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(finding.title, style = MaterialTheme.typography.titleMedium); Text(finding.description); AssistChip(onClick = {}, label = { Text(finding.severity.name) }) } } }
                item {
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Evidence Vault", style = MaterialTheme.typography.titleLarge)
                            Text("Sensitive values stay masked by default.")
                            OutlinedTextField(evidenceType, { evidenceType = it }, label = { Text("Evidence type") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(evidenceValue, { evidenceValue = it }, label = { Text("Evidence value") }, modifier = Modifier.fillMaxWidth())
                            Row { Checkbox(sensitive, { sensitive = it }); Text("Sensitive (mask by default)") }
                            Button(enabled = evidenceValue.isNotBlank() && evidenceType.isNotBlank(), onClick = { EvidenceVault.add(evidenceType, evidenceValue, sensitive); evidenceValue = ""; refresh() }, modifier = Modifier.fillMaxWidth()) { Text("Add Evidence") }
                            evidence.forEach { e -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) { Text(e.type, style = MaterialTheme.typography.titleSmall); Text(e.redactedPreview) }
                                Checkbox(selected.contains(e.id), { checked -> selected = if (checked) selected + e.id else selected - e.id })
                            } }
                        }
                    }
                }
                item {
                    val canSubmit = EvidencePolicy.canSubmit(evidence.map { EvidenceSelection(it.id, selected.contains(it.id)) }) && report.isNotBlank()
                    ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Submission Preview", style = MaterialTheme.typography.titleLarge)
                        Text("Selected evidence: " + selected.size)
                        Row { Checkbox(approval, { approval = it }, enabled = canSubmit); Text("I approve this exact submission snapshot.") }
                        Button(enabled = canSubmit && approval, onClick = {
                            snapshot = SubmissionManager.create(findings.firstOrNull(), report, selected, true)
                            status = if (snapshot != null) "Submission approved" else "Submission rejected"
                        }, modifier = Modifier.fillMaxWidth()) { Text("Create Approved Snapshot") }
                    } }
                }
                snapshot?.let { s ->
                    item {
                        ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Export", style = MaterialTheme.typography.titleLarge)
                            Text("Only the approved snapshot is exportable.")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { share(ReportExporter.markdown(s), "text/markdown") }) { Text("Markdown") }
                                Button(onClick = { share(ReportExporter.json(s), "application/json") }) { Text("JSON") }
                                Button(onClick = { share(ReportExporter.html(s), "text/html") }) { Text("HTML") }
                            }
                            Text("Approved evidence: " + s.evidenceIds.size)
                        } }
                    }
                }
                item { Text("Safety boundary: authorized research only. No brute force, credential attacks, exploit automation, or unrestricted third-party scanning.", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier) { Column(Modifier.padding(14.dp)) { Text(title, style = MaterialTheme.typography.labelMedium); Text(value, style = MaterialTheme.typography.titleMedium) } }
}