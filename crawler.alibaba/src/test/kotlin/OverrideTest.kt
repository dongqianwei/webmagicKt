import org.junit.Test

interface A {
    val a: Boolean
        get() = true
}

class AI(override val a: Boolean) : A {

}

class OverrideTest {
    @Test
    fun test001() {
        val a = AI(true)
    }
}