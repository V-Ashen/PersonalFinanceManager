package com.example.personalfinancemanager.network

import com.example.personalfinancemanager.data.Expense
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// A simple data class to represent the server's success/error response
data class SyncResponse(
    val status: String,
    val message: String
)

interface ApiService {
    @POST("sync/expenses") // This must match the route in your Python app
    suspend fun syncExpenses(@Body expenses: List<Expense>): Response<SyncResponse>
}