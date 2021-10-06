package com.sbeins.controller

import java.time.LocalDate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import jakarta.inject.Inject

@MicronautTest
class TestControllerSpec : FreeSpec() {

    @Inject
    @field:Client("/")
    private lateinit var client: HttpClient

    init {

        "testing valid date values with @Format and type LocalDate" - {
            listOf(
                Triple("valid date", "2021-12-31", LocalDate.parse("2021-12-31")),
                Triple("blank date", "", LocalDate.parse("9999-01-01")),
                Triple("null date", null, LocalDate.parse("9999-01-01"))
            ).forEach { (description: String, testDate: String?, expected: LocalDate?) ->
                "$description current valid behavior" {
                    val httpRequest = GET<LocalDate?>("/date_resources?test_date=$testDate")
                    val response = client.toBlocking().retrieve(httpRequest, Argument.of(LocalDate::class.java))

                    response shouldBe expected
                }
            }
        }

        "testing invalid date values with @Format and type LocalDate actual behavior" - {
            listOf(
                Triple("invalid date not a leap year", "2022-02-31", LocalDate.parse("2022-02-28")),
                Triple("invalid date month without 31 days", "2022-04-31", LocalDate.parse("2022-04-30")),
                Triple("invalid date for day always", "2022-02-32", LocalDate.parse("9999-01-01")),
            ).forEach { (description: String, testDate: String?, expected: LocalDate?) ->
                "$description are currently allowed" {
                    val httpRequest = GET<LocalDate>("/date_resources?test_date=$testDate")
                    val response = client.toBlocking().retrieve(httpRequest, Argument.of(LocalDate::class.java))

                    response shouldBe expected
                }
            }
        }

        "testing invalid date values with @Format and type LocalDate expected behavior" - {
            listOf(
                Triple("invalid date not a leap year", "2022-02-31", HttpStatus.BAD_REQUEST),
                Triple("invalid date month without 31 days", "2022-04-31", HttpStatus.BAD_REQUEST),
                Triple("invalid date for day always", "2022-02-32", HttpStatus.BAD_REQUEST),
            ).forEach { (description: String, testDate: String?, expected: HttpStatus) ->
                "$description should not be allowed" {
                    val httpRequest = GET<LocalDate>("/date_resources?test_date=$testDate")
                    val response = shouldThrow<HttpClientResponseException> {
                        client.toBlocking().retrieve(httpRequest, Argument.of(LocalDate::class.java))
                    }

                    response.status shouldBe expected
                }
            }

        }

    }
}
