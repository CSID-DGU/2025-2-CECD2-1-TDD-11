package com.tdd.talktobook.data.entity.response.api

class ApiException(val status: Int, val msg: String) : RuntimeException("code: $status, message: $msg")
