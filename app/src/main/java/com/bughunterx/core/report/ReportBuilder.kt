package com.bughunterx.core.report
import com.bughunterx.core.model.Finding
object ReportBuilder {
 fun markdown(target:String,findings:List<Finding>):String=buildString{
  appendLine("# BugHunter X Security Report")
  appendLine()
  appendLine("Target: $target")
  appendLine()
  appendLine("## Findings")
  findings.forEachIndexed { i,f -> appendLine("${i+1}. **${f.title}** — ${f.severity}"); appendLine("   ${f.description}") }
  appendLine()
  appendLine("## Scope")
  appendLine("This report is for an explicitly authorized security assessment.")
 }
}
