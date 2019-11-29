package com.besseggen.identity.configuration

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.security.Principal
import java.util.LinkedHashMap

@Controller
class UserInfoEndpoint {

    @GetMapping("/userinfo")
    @ResponseBody
    fun userInfo(principal: Principal): Map<String, Any>? {
        val authentication = principal as AbstractAuthenticationToken
        val map: MutableMap<String, Any> = LinkedHashMap()
        map["username"] = authentication.name
        return map
    }
}