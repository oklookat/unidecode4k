import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.oklookat.unidecode


data class TestCase(val input: String, val expect: String)

class UnidecodeTest {

    private fun testUnidecode(input: String, expect: String) {
        val ret = unidecode(input)
        check(ret, expect)
    }

    private fun check(ret: String, expect: String) {
        assertEquals(expect, ret)
    }


    @Test
    fun testUnidecodeASCII() {
        for (n in 0 until '\u007F'.code) {
            val expect = n.toChar().toString()
            testUnidecode(n.toChar().toString(), expect)
        }
    }

    @Test
    fun testUnidecode() {
        val cases = listOf(
            TestCase("", ""),
            TestCase("abc", "abc"),
            TestCase("北京", "Bei Jing "),
            TestCase("abc北京", "abcBei Jing "),
            TestCase("ネオアームストロングサイクロンジェットアームストロング砲", "neoa-musutorongusaikuronzietsutoa-musutoronguPao "),
            TestCase("30 𝗄𝗆/𝗁", "30 /"), // ???
            TestCase("kožušček", "kozuscek"),
            TestCase("ⓐⒶ⑳⒇⒛⓴⓾⓿", "aA20(20)20.20100"),
            TestCase("Hello, World!", "Hello, World!"),
            TestCase("\\n", "\\n"),
            TestCase("北京abc\\n", "Bei Jing abc\\n"),
            TestCase("'\"\\r\\n", "'\"\\r\\n"),
            TestCase("ČŽŠčžš", "CZSczs"),
            TestCase("ア", "a"),
            TestCase("α", "a"),
            TestCase("a", "a"),
            TestCase("ch\u00e2teau", "chateau"),
            TestCase("vi\u00f1edos", "vinedos"),
            TestCase("Efﬁcient", "Efficient"),
            TestCase("příliš žluťoučký kůň pěl ďábelské ódy", "prilis zlutoucky kun pel dabelske ody"),
            TestCase("PŘÍLIŠ ŽLUŤOUČKÝ KŮŇ PĚL ĎÁBELSKÉ ÓDY", "PRILIS ZLUTOUCKY KUN PEL DABELSKE ODY"),
            TestCase("\ua500", ""),
            TestCase("\u1eff", ""),
        )
        cases.forEach { testUnidecode(it.input, it.expect) }
    }
}
