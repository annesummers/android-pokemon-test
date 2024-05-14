package com.giganticsheep.network.offline.responder

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

internal class ResponderTest {

    @Test
    fun `test match body file responder matches`() {
        val body = "body"

        val responder = MatchBodyResponded(body)

        assertThat(responder.isMatch(body))
            .isTrue()
    }

    @Test
    fun `test match body file responder matches partial`() {
        val body = "body"
        val responder = MatchBodyResponded(body)

        assertThat(responder.isMatch("stuff and also body and stuff"))
            .isTrue()
    }

    @Test
    fun `test match body file responder does not match`() {
        val body = "body"

        val responder = MatchBodyResponded(body)

        assertThat(responder.isMatch("not matching"))
            .isFalse()
    }

    @Test
    fun `test match body file responder empty body`() {
        val body = "body"

        val responder = MatchBodyResponded(body)

        assertThat(responder.isMatch(""))
            .isFalse()
    }
}
