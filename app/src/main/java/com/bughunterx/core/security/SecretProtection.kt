package com.bughunterx.core.security
object SecretProtection { fun mask(value:String):String=if(value.length<=6)"••••••" else value.take(3)+"••••••"+value.takeLast(3) }
