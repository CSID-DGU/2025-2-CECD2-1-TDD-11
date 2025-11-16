package com.tdd.bookshelf.data.entity.response.api

class ApiException(val status: Int, message: String) : Exception(message)
