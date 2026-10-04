package com.bughunterx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bughunterx.core.ScopeGuard
import com.bughunterx.core.analysis.PassiveAnalyzer
import com.bughunterx.core.model.*
import com.bughunterx.core.report.ReportBuilder
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

 BugHunterXTheme {
  Scaffold(topBar = {
   TopAppBar(title = {
    Column {
     Text("BugHunter X")
     Text("Security Research Dashboard", style = MaterialTheme.typography.labelSmall)
    }
   })
  }) { padding ->
   LazyColumn(
    Modifier.fillMaxSize().padding(padding).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
   ) {
    item {
     Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
     Text("Authorized passive security analysis")
    }
    item {
     ElevatedCard(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
       Text("Target & Scope", style = MaterialTheme.typography.titleLarge)
       OutlinedTextField(
        value = target,
        onValueChange = { target = it },
        label = { Text("Target URL") },
        placeholder = { Text("https://example.com") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
       )
       Row {
        Checkbox(authorized, { authorized = it })
        Text("I confirm this target is explicitly authorized.")
       }
       Button(
        enabled = authorized && target.isNotBlank(),
        onClick = {
         if (ScopeGuard.isAllowed(target)) {
          findings = PassiveAnalyzer.analyzeTarget(target)
          status = "Passive analysis completed"
          report = ReportBuilder.markdown(target, findings)
         } else {
          findings = listOf(Finding("Scope blocked", "Use a valid HTTP(S) target and confirm authorization.", Severity.HIGH))
          status = "Blocked"
          report = ""
         }
        },
        modifier = Modifier.fillMaxWidth()
       ) { Text("Run Passive Analysis") }
      }
     }
    }
    item {
     Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      MetricCard("Status", status, Modifier.weight(1f))
      MetricCard("Findings", findings.size.toString(), Modifier.weight(1f))
     }
    }
    item { Text("Findings", style = MaterialTheme.typography.titleLarge) }
    if (findings.isEmpty()) {
     item { Text("No findings yet. Validate an authorized target to begin.") }
    } else {
     items(findings) { finding ->
      ElevatedCard(Modifier.fillMaxWidth()) {
       Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(finding.title, style = MaterialTheme.typography.titleMedium)
        Text(finding.description)
        AssistChip(onClick = {}, label = { Text(finding.severity.name) })
       }
      }
     }
    }
    item {
     ElevatedCard(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
       Text("Evidence Vault", style = MaterialTheme.typography.titleLarge)
       Text("Sensitive evidence is designed to remain protected and masked by default.")
       Text("API keys • Tokens • Cookies • PII • Requests • Responses")
      }
     }
    }
    item {
     ElevatedCard(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
       Text("Report Preview", style = MaterialTheme.typography.titleLarge)
       if (report.isBlank()) Text("Run an analysis to generate the exact report preview.")
       else {
        Text(report.take(1200))
        Text("Submission requires explicit review/approval.", style = MaterialTheme.typography.bodySmall)
       }
      }
     }
    }
    item {
     Text(
      "Safety boundary: authorized research only. No brute force, credential attacks, exploit automation, or unrestricted third-party scanning.",
      style = MaterialTheme.typography.bodySmall
     )
    }
   }
  }
 }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
 ElevatedCard(modifier) {
  Column(Modifier.padding(14.dp)) {
   Text(title, style = MaterialTheme.typography.labelMedium)
   Text(value, style = MaterialTheme.typography.titleMedium)
  }
 }
}
