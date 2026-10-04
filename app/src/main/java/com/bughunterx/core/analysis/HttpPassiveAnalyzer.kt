package com.bughunterx.core.analysis

import com.bughunterx.core.model.Finding
import com.bughunterx.core.model.Severity
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

object HttpPassiveAnalyzer {
 fun analyze(raw: String): List<Finding> {
  val uri = runCatching { URI(raw.trim()) }.getOrNull()
   ?: return listOf(Finding("Invalid target", "The target URL could not be parsed.", Severity.HIGH))
  return runCatching {
   val connection = URL(uri.toString()).openConnection() as HttpURLConnection
   connection.requestMethod = "HEAD"
   connection.connectTimeout = 7000
   connection.readTimeout = 7000
   connection.instanceFollowRedirects = false
   connection.connect()
   val headers = connection.headerFields
   val result = mutableListOf<Finding>()
   if (uri.scheme.equals("http", true))
    result += Finding("HTTP transport", "The target is using HTTP. Prefer HTTPS.", Severity.MEDIUM)
   if (uri.scheme.equals("https", true) && headers.keys.none { it?.equals("Strict-Transport-Security", true) == true })
    result += Finding("HSTS header not observed", "Strict-Transport-Security was not present.", Severity.LOW)
   if (headers.keys.none { it?.equals("Content-Security-Policy", true) == true })
    result += Finding("CSP header not observed", "Content-Security-Policy was not present.", Severity.LOW)
   if (headers.keys.none { it?.equals("X-Content-Type-Options", true) == true })
    result += Finding("MIME sniffing protection not observed", "X-Content-Type-Options was not present.", Severity.LOW)
   if (headers.keys.none { it?.equals("Referrer-Policy", true) == true })
    result += Finding("Referrer-Policy not observed", "Referrer-Policy was not present.", Severity.INFO)
   result += Finding("HTTP metadata collected", "Passive HEAD response status " + connection.responseCode + ". No exploit or credential attack was performed.", Severity.INFO)
   connection.disconnect()
   result
  }.getOrElse {
   listOf(Finding("Passive HTTP check failed", "Safe HEAD request could not be completed.", Severity.INFO))
  }
 }
}
