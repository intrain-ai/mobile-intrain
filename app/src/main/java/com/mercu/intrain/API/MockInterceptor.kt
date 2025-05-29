package com.mercu.intrain.API

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        val responseString = when {
            path.contains("/courses/user/") && path.contains("/enrollments") -> getEnrolledCourses()
            path.contains("/completed-courses") -> getCompletedCourses()
            path.contains("/courses") -> getAllCourses()
            else -> "[]"
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(
                responseString.toByteArray()
                    .toResponseBody("application/json".toMediaTypeOrNull())
            )
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun getEnrolledCourses(): String {
        return """
        [
              {
                "course_title": "UI/UX Design",
                "completed_at": null,
                "course_id": "c001",
                "enrolled_at": "2025-05-01T08:00:00",
                "id": "enr001",
                "is_completed": false,
                "user_id": "u001",
                "enrolled_status": true,
                "provider": "Coursera",
                "progress": 20
              },
              {
                "course_title": "Android Kotlin Developer",
                "completed_at": null,
                "course_id": "c002",
                "enrolled_at": "2025-05-03T09:00:00",
                "id": "enr002",
                "is_completed": false,
                "user_id": "u001",
                "enrolled_status": true,
                "provider": "Dicoding",
                "progress": 65
              },
              {
                "course_title": "Data Science with Python",
                "completed_at": "2025-05-18T14:35:00",
                "course_id": "c003",
                "enrolled_at": "2025-04-25T10:00:00",
                "id": "enr003",
                "is_completed": true,
                "user_id": "u001",
                "enrolled_status": true,
                "provider": "edX",
                "progress": 100
              },
              {
                "course_title": "Introduction to Cybersecurity",
                "completed_at": "2025-05-10T16:20:00",
                "course_id": "c004",
                "enrolled_at": "2025-04-20T12:00:00",
                "id": "enr004",
                "is_completed": true,
                "user_id": "u001",
                "enrolled_status": true,
                "provider": "Udemy",
                "progress": 100
              }
        ]

        """.trimIndent()
    }

    private fun getCompletedCourses(): String {
        return """
        [
            {
                "completed_at": "2025-04-26T17:04:50",
                "course_id": "00bb8aa2-5b26-4cd3-b061-8f7a20eb94dc",
                "enrolled_at": "2025-04-26T17:01:21",
                "id": "8803c851-59fa-4479-b989-7b61ed08ed19",
                "is_completed": true,
                "user_id": "5f684a9c-2bcb-4e40-b176-03277860a9f7"
            }
        ]
        """.trimIndent()
    }

    private fun getAllCourses(): String {
        return """
        [
            {
                "created_at": "2025-04-26T15:07:58",
                "description": "Description for C# Programming Fundamentals.",
                "id": "00bb8aa2-5b26-4cd3-b061-8f7a20eb94dc",
                "provider": "Udemy",
                "title": "C# Programming Fundamentals",
                "url": "https://example.com/c#-programming-fundamentals"
            },
            {
                "created_at": "2025-04-26T15:07:58",
                "description": "Description for Digital Marketing Essentials.",
                "id": "02710137-f971-41c7-b898-d6b98f07858f",
                "provider": "Udemy",
                "title": "Digital Marketing Essentials",
                "url": "https://example.com/digital-marketing-essentials"
            },
            {
                "created_at": "2025-04-26T15:07:58",
                "description": "Description for Network Administration Fundamentals.",
                "id": "0f3d9cbe-b91d-4a15-864e-51800ea2bee6",
                "provider": "Udemy",
                "title": "Network Administration Fundamentals",
                "url": "https://example.com/network-administration-fundamentals"
            }
        ]
        """.trimIndent()
    }
}
