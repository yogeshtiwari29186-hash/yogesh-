package com.bughunterx.core.analysis
import com.bughunterx.core.model.Finding
import com.bughunterx.core.model.Severity
import java.net.URI
object PassiveAnalyzer {
 fun analyzeTarget(raw:String):List<Finding>{
  val uri=runCatching{URI(raw.trim())}.getOrNull()?:return listOf(Finding("Invalid target","The supplied target is not a valid URL.",Severity.HIGH))
  val out=mutableListOf<Finding>()
  if(!uri.scheme.equals("https",true)) out+=Finding("HTTPS not confirmed","Prefer encrypted transport for authorized testing.",Severity.MEDIUM)
  else out+=Finding("HTTPS target","Target uses HTTPS and is eligible for passive analysis.",Severity.INFO)
  if(uri.port!=-1&&uri.port!=443&&uri.port!=80) out+=Finding("Non-default port","Review its intended scope and configuration.",Severity.LOW)
  out+=Finding("Passive checks ready","HTTP headers, TLS metadata, cookies and API documentation can be analyzed without exploit automation.",Severity.INFO)
  return out
 }
}
