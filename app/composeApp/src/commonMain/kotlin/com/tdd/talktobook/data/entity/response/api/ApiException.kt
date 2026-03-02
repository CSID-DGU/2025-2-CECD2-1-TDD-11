package com.tdd.talktobook.data.entity.response.api

class ApiException(val status: Int, message: String) : Exception(message)
