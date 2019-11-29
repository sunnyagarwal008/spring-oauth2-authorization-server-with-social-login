package com.besseggen.identity.controller

import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.HtmlUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@SessionAttributes("authorizationRequest")
class AuthorizationApprovalController {

    @RequestMapping("/oauth/confirm_access")
    fun getAccessConfirmation(model: MutableMap<String?, Any?>, request: HttpServletRequest): ModelAndView {
        val approvalContent: String = createTemplate(model, request)
        if (request.getAttribute("_csrf") != null) {
            model["_csrf"] = request.getAttribute("_csrf")
        }
        val approvalView: View = object : View {
            override fun getContentType(): String? {
                return "text/html"
            }

            override fun render(model: Map<String?, *>?, request: HttpServletRequest, response: HttpServletResponse) {
                response.contentType = contentType
                response.writer.append(approvalContent)
            }
        }
        return ModelAndView(approvalView, model)
    }

    protected fun createTemplate(model: Map<String?, Any?>, request: HttpServletRequest): String {
        val authorizationRequest = model["authorizationRequest"] as AuthorizationRequest?
        val clientId = authorizationRequest!!.clientId
        val builder = StringBuilder()
        builder.append("<html><body><h1>OAuth Approval</h1>")
        builder.append("<p>Can you authorize \"").append(HtmlUtils.htmlEscape(clientId))
        builder.append("\" to access your protected resources?</p>")
        builder.append("<form id=\"confirmationForm\" name=\"confirmationForm\" action=\"")
        var requestPath = ServletUriComponentsBuilder.fromContextPath(request).build().path
        if (requestPath == null) {
            requestPath = ""
        }
        builder.append(requestPath).append("/oauth/authorize\" method=\"post\">")
        builder.append("<input name=\"user_oauth_approval\" value=\"true\" type=\"hidden\"/>")
        var csrfTemplate: String? = null
        val csrfToken = (if (model.containsKey("_csrf")) model["_csrf"] else request.getAttribute("_csrf")) as CsrfToken?
        if (csrfToken != null) {
            csrfTemplate = "<input type=\"hidden\" name=\"" + HtmlUtils.htmlEscape(csrfToken.parameterName) +
                    "\" value=\"" + HtmlUtils.htmlEscape(csrfToken.token) + "\" />"
        }
        if (csrfTemplate != null) {
            builder.append(csrfTemplate)
        }
        val authorizeInputTemplate = "<label><input name=\"authorize\" value=\"Authorize\" type=\"submit\"/></label></form>"
        if (model.containsKey("scopes") || request.getAttribute("scopes") != null) {
            builder.append(createScopes(model, request))
            builder.append(authorizeInputTemplate)
        } else {
            builder.append(authorizeInputTemplate)
            builder.append("<form id=\"denialForm\" name=\"denialForm\" action=\"")
            builder.append(requestPath).append("/oauth/authorize\" method=\"post\">")
            builder.append("<input name=\"user_oauth_approval\" value=\"false\" type=\"hidden\"/>")
            if (csrfTemplate != null) {
                builder.append(csrfTemplate)
            }
            builder.append("<label><input name=\"deny\" value=\"Deny\" type=\"submit\"/></label></form>")
        }
        builder.append("</body></html>")
        return builder.toString()
    }

    private fun createScopes(model: Map<String?, Any?>, request: HttpServletRequest): CharSequence? {
        val builder = StringBuilder("<ul>")
        val scopes = (if (model.containsKey("scopes")) model["scopes"] else request.getAttribute("scopes")) as Map<String, String>?
        for (scope in scopes!!.keys) {
            val approved = if ("true" == scopes[scope]) " checked" else ""
            val denied = if ("true" != scopes[scope]) " checked" else ""
            val scope1 = HtmlUtils.htmlEscape(scope)
            builder.append("<li><div class=\"form-group\">")
            builder.append(scope1).append(": <input type=\"radio\" name=\"")
            builder.append(scope1).append("\" value=\"true\"").append(approved).append(">Approve</input> ")
            builder.append("<input type=\"radio\" name=\"").append(scope1).append("\" value=\"false\"")
            builder.append(denied).append(">Deny</input></div></li>")
        }
        builder.append("</ul>")
        return builder.toString()
    }
}