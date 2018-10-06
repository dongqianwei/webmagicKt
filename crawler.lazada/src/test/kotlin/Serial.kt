import com.google.gson.Gson
import org.junit.Test

val gson = Gson()

class d1(val value: List<String>)

class T1 {
    @Test
    fun test01() {
        val json = gson.toJson(d1(listOf("hello", "world")))
        println(json)
    }
}
