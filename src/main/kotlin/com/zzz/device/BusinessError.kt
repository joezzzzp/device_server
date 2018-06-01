package com.zzz.device

import javax.servlet.http.HttpServletResponse

enum class BusinessError(val code: Int, val message: String) {
  INVALID_ACCESS_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "invalid access token")
}