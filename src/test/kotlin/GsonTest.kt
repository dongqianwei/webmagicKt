import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Test

class GsonTest  {

    @Test
    fun test01() {
        val list = listOf("aaa", "bbb")
        val gson = Gson()
        val json = gson.toJson(list)
        println(json)
        val listType = object : TypeToken<List<String>>() { }.type
        val arr = gson.fromJson<List<String>>(json, listType)
        println(arr)
    }
}