package com.example.kmpprojectdemo.data

import kotlin.random.Random

object PasswordUtils {
    fun generateSalt(): String {
        val bytes = ByteArray(16)
        Random.nextBytes(bytes)
        return bytes.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
    }

    fun hashPassword(password: String, salt: String): String {
        return sha256(salt + password)
    }

    private fun sha256(input: String): String {
        val bytes = input.encodeToByteArray()
        return sha256Bytes(bytes).joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
    }

    private fun sha256Bytes(message: ByteArray): ByteArray {
        val k = intArrayOf(
            0x428a2f98.toInt(), 0x71374491.toInt(), 0xb5c0fbcf.toInt(), 0xe9b5dba5.toInt(),
            0x3956c25b.toInt(), 0x59f111f1.toInt(), 0x923f82a4.toInt(), 0xab1c5ed5.toInt(),
            0xd807aa98.toInt(), 0x12835b01.toInt(), 0x243185be.toInt(), 0x550c7dc3.toInt(),
            0x72be5d74.toInt(), 0x80deb1fe.toInt(), 0x9bdc06a7.toInt(), 0xc19bf174.toInt(),
            0xe49b69c1.toInt(), 0xefbe4786.toInt(), 0x0fc19dc6.toInt(), 0x240ca1cc.toInt(),
            0x2de92c6f.toInt(), 0x4a7484aa.toInt(), 0x5cb0a9dc.toInt(), 0x76f988da.toInt(),
            0x983e5152.toInt(), 0xa831c66d.toInt(), 0xb00327c8.toInt(), 0xbf597fc7.toInt(),
            0xc6e00bf3.toInt(), 0xd5a79147.toInt(), 0x06ca6351.toInt(), 0x14292967.toInt(),
            0x27b70a85.toInt(), 0x2e1b2138.toInt(), 0x4d2c6dfc.toInt(), 0x53380d13.toInt(),
            0x650a7354.toInt(), 0x766a0abb.toInt(), 0x81c2c92e.toInt(), 0x92722c85.toInt(),
            0xa2bfe8a1.toInt(), 0xa81a664b.toInt(), 0xc24b8b70.toInt(), 0xc76c51a3.toInt(),
            0xd192e819.toInt(), 0xd6990624.toInt(), 0xf40e3585.toInt(), 0x106aa070.toInt(),
            0x19a4c116.toInt(), 0x1e376c08.toInt(), 0x2748774c.toInt(), 0x34b0bcb5.toInt(),
            0x391c0cb3.toInt(), 0x4ed8aa4a.toInt(), 0x5b9cca4f.toInt(), 0x682e6ff3.toInt(),
            0x748f82ee.toInt(), 0x78a5636f.toInt(), 0x84c87814.toInt(), 0x8cc70208.toInt(),
            0x90befffa.toInt(), 0xa4506ceb.toInt(), 0xbef9a3f7.toInt(), 0xc67178f2.toInt()
        )

        var h0 = 0x6a09e667.toInt()
        var h1 = 0xbb67ae85.toInt()
        var h2 = 0x3c6ef372.toInt()
        var h3 = 0xa54ff53a.toInt()
        var h4 = 0x510e527f.toInt()
        var h5 = 0x9b05688c.toInt()
        var h6 = 0x1f83d9ab.toInt()
        var h7 = 0x5be0cd19.toInt()

        val msgLen = message.size
        val bitLen = msgLen.toLong() shl 3
        
        val totalLen = (((msgLen + 9 + 63) / 64) * 64)
        val padded = ByteArray(totalLen)
        message.copyInto(padded, 0, 0, msgLen)
        padded[msgLen] = 0x80.toByte()

        for (i in 0..7) {
            padded[totalLen - 1 - i] = (bitLen ushr (i * 8)).toByte()
        }

        val w = IntArray(64)
        val chunk = ByteArray(64)

        for (i in 0 until totalLen step 64) {
            padded.copyInto(chunk, 0, i, i + 64)
            for (j in 0..15) {
                w[j] = ((chunk[j * 4].toInt() and 0xff) shl 24) or
                        ((chunk[j * 4 + 1].toInt() and 0xff) shl 16) or
                        ((chunk[j * 4 + 2].toInt() and 0xff) shl 8) or
                        (chunk[j * 4 + 3].toInt() and 0xff)
            }
            for (j in 16..63) {
                val s0 = rotateRight(w[j - 15], 7) xor rotateRight(w[j - 15], 18) xor (w[j - 15] ushr 3)
                val s1 = rotateRight(w[j - 2], 17) xor rotateRight(w[j - 2], 19) xor (w[j - 2] ushr 10)
                w[j] = w[j - 16] + s0 + w[j - 7] + s1
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4
            var f = h5
            var g = h6
            var h = h7

            for (j in 0..63) {
                val s1 = rotateRight(e, 6) xor rotateRight(e, 11) xor rotateRight(e, 25)
                val ch = (e and f) xor (e.inv() and g)
                val temp1 = h + s1 + ch + k[j] + w[j]
                val s0 = rotateRight(a, 2) xor rotateRight(a, 13) xor rotateRight(a, 22)
                val maj = (a and b) xor (a and c) xor (b and c)
                val temp2 = s0 + maj

                h = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            h5 += f
            h6 += g
            h7 += h
        }

        val result = ByteArray(32)
        intToBytes(h0, result, 0)
        intToBytes(h1, result, 4)
        intToBytes(h2, result, 8)
        intToBytes(h3, result, 12)
        intToBytes(h4, result, 16)
        intToBytes(h5, result, 20)
        intToBytes(h6, result, 24)
        intToBytes(h7, result, 28)
        return result
    }

    private fun rotateRight(value: Int, count: Int): Int {
        return (value ushr count) or (value shl (32 - count))
    }

    private fun intToBytes(value: Int, array: ByteArray, offset: Int) {
        array[offset] = (value ushr 24).toByte()
        array[offset + 1] = (value ushr 16).toByte()
        array[offset + 2] = (value ushr 8).toByte()
        array[offset + 3] = value.toByte()
    }
}
