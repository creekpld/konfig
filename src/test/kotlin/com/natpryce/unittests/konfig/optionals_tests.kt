package com.natpryce.unittests.konfig

import com.natpryce.konfig.*
import org.junit.Test
import java.lang.System.setProperty
import java.net.URI
import java.net.URISyntaxException
import java.util.*


class OptionalsTests {

    object Keys : PropertyKeys() {

        val foo = stringType

        val bar = Key(name = "bar", parse = stringType, isOptional = true)

        object baz : PropertyGroup() {

            val mode = Key(
                name = "baz.mode",
                parse = stringType,
                isOptional = true)

            val uri by propertyType(
                type = URI::class.java,
                parse = parser<URI, URISyntaxException> { URI(it) })

            val a = Key(
                name = "baz.a",
                parse = stringType,
                isOptional = true)

            val b = Key(
                name = "baz.b",
                parse = stringType,
                isOptional = true,
                requiredOn = listOf(Pair(mode, "yes")))
        }
    }


    @Test
    fun test_optional_keys_valid() {

        val config = ConfigurationProperties(location = Location("location"), properties = Properties().apply {
            setProperty("foo", "abcdefg")
            setProperty("baz.mode", "no")
            setProperty("baz.uri", "http://example.com")
        })

        config.list().forEach {
            it.second.forEach { (k, v) ->
                println("$k=$v")
            }
        }

        try {
            config.validate(Keys)
            assert(true)
        } catch (e: Misconfiguration) {
            e.printStackTrace()
            assert(false)
        }

    }


    @Test
    fun test_required_keys_invalid() {

        val config = ConfigurationProperties(location = Location("location"), properties = Properties().apply {
            setProperty("foo", "abcdefg")
            setProperty("baz.mode", "yes")
            setProperty("baz.uri", "http://example.com")
            //setProperty("baz.b", "OhNo")
        })

        config.list().forEach {
            it.second.forEach { (k, v) ->
                println("$k=$v")
            }
        }

        try {
            config.validate(Keys)
            assert(false)
        } catch (e: Misconfiguration) {
            e.printStackTrace()
            assert(true)
        }

    }

    @Test
    fun test_required_keys_valid() {

        val config = ConfigurationProperties(location = Location("location"), properties = Properties().apply {
            setProperty("foo", "abcdefg")
            setProperty("baz.mode", "yes")
            setProperty("baz.uri", "http://example.com")
            setProperty("baz.b", "OhNo")
        })

        config.list().forEach {
            it.second.forEach { (k, v) ->
                println("$k=$v")
            }
        }

        try {
            config.validate(Keys)
            assert(true)
        } catch (e: Misconfiguration) {
            e.printStackTrace()
            assert(false)
        }

    }
}
