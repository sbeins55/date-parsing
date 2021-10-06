package com.sbeins.controller

import java.time.LocalDate
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

@Controller
class TestController {

    @Get("/date_resources")
    fun test(
        @Nullable @Format(value = "yyyy-MM-dd") @QueryValue(value = "test_date") testDate: LocalDate?
    ): LocalDate {
        return testDate ?: LocalDate.parse("9999-01-01")
    }

}
