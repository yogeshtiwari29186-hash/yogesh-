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
import com.bughunterx.core.model.*
import com.bughunterx.ui.theme.BugHunterXTheme
class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { App() } }
}
@Composable private fun App() {
 var target by remember { mutableStateOf("") }
 var authorized by remember { mutableStateOf(false) }
 var status by remember { mutableStateOf("Ready") }
 var findings by remember { mutableStateOf(emptyList<Finding>()) }
 BugHunterXTheme { Scaffold(topBar={TopAppBar(title={Column{Text("BugHunter X");Text("Authorized Security Research",style=MaterialTheme.typography.labelSmall)}})}) { p ->
  LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
   item { ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Text("Scope Firewall",style=MaterialTheme.typography.titleLarge);Text("Explicit authorization is required before analysis.")}}}
   item { OutlinedTextField(target,{target=it},label={Text("Target URL")},placeholder={Text("https://example.com")},singleLine=true,modifier=Modifier.fillMaxWidth())}
   item { Row{Checkbox(authorized,{authorized=it});Text("I confirm this target is explicitly authorized.")}}
   item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.fillMaxWidth()){
    Button(enabled=authorized&&target.isNotBlank(),onClick={status=if(ScopeGuard.isAllowed(target))"Scope validation passed." else "Blocked: invalid HTTP(S) URL."},modifier=Modifier.weight(1f)){Text("Validate")}
    OutlinedButton(enabled=authorized&&ScopeGuard.isAllowed(target),onClick={findings=listOf(Finding("Passive analysis ready","Security analysis pipeline initialized for the authorized target.",Severity.INFO));status="Analysis completed."},modifier=Modifier.weight(1f)){Text("Analyze")}
   }}
   item { ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("Status",style=MaterialTheme.typography.titleMedium);Text(status)}}}
   item { Text("Modules",style=MaterialTheme.typography.titleLarge);Text("Recon • HTTP/TLS • Headers • API Analysis • Findings • Evidence Vault • Reports • Submission")}
   items(findings) { f -> ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text(f.title,style=MaterialTheme.typography.titleMedium);Text(f.description);Text("Severity: "+f.severity)}}}
   item { Text("Safety: authorized research only. No brute force, credential attacks, exploit automation, or unrestricted third-party scanning.",style=MaterialTheme.typography.bodySmall)}
  }
 }}
}
