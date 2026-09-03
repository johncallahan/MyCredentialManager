package com.example.mycredman

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CredmanUtilsBase64Test {

    // Lengths 1..8 cover every residue mod 3, i.e. every padding case.
    @Test
    fun roundTripsAtEveryPaddingLength() {
        for (len in 1..8) {
            val data = ByteArray(len) { it.toByte() }
            val encoded = CredmanUtils.b64Encode(data)
            assertFalse("encode must stay unpadded for WebAuthn: $encoded", encoded.contains('='))
            assertArrayEquals("round trip failed at length $len", data, CredmanUtils.b64Decode(encoded))
        }
    }

    // Serialized KeyPair blobs in SharedPreferences are long and unpadded; this is the
    // input shape that crashed MyCredentialDataManager.convertCred.
    @Test
    fun decodesLongUnpaddedBlob() {
        val data = ByteArray(1000) { (it * 7).toByte() }
        assertArrayEquals(data, CredmanUtils.b64Decode(CredmanUtils.b64Encode(data)))
    }

    // RP-supplied JSON values may arrive padded, so decode must accept both forms.
    @Test
    fun decodesPaddedInput() {
        assertArrayEquals(byteArrayOf(1), CredmanUtils.b64Decode("AQ=="))
        assertArrayEquals(byteArrayOf(1), CredmanUtils.b64Decode("AQ"))
    }
}
