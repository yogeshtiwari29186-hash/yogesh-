package com.bughunterx.core
import java.net.URI
object ScopeGuard { fun isAllowed(raw:String):Boolean=runCatching{ val u=URI(raw.trim()); (u.scheme.equals("https",true)||u.scheme.equals("http",true))&&!u.host.isNullOrBlank() }.getOrDefault(false) }
