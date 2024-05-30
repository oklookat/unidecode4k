package ru.oklookat

import table.tables

/** Unidecode implements transliterate Unicode text into plain 7-bit ASCII.
 *
e.g. Unidecode("kožušček") => "kozuscek" */
fun unidecode(s: String): String {
    val ret = StringBuilder()
    for (r in s) {
        if (r.code < '\u007F'.code) {
            ret.append(r)
            continue
        }
        if (r.code > 0xeffff) {
            continue
        }

        val section = r.code shr 8 // Chop off the last two hex digits
        val position = r.code % 256 // Last two hex digits
        val tb = tables[section.toChar()]
        if (tb != null && tb.size > position) {
            ret.append(tb[position])
        }
    }
    return ret.toString()
}
