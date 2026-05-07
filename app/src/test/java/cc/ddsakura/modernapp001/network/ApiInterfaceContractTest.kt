package cc.ddsakura.modernapp001.network

import kotlin.coroutines.Continuation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import retrofit2.http.GET

class ApiInterfaceContractTest {
    @Test
    fun get200_usesExpectedTimeoutEndpoint() {
        val annotation = requireNotNull(get200Method().getAnnotation(GET::class.java))

        assertEquals("/200?sleep=5000", annotation.value)
    }

    @Test
    fun get200_isSuspendFunctionReturningRetrofitResponse() {
        val method = get200Method()
        // Retrofit contract tests use reflection here so they do not create a client or make network calls.
        val continuationType = method.genericParameterTypes.single().typeName

        assertTrue(continuationType.startsWith(Continuation::class.java.name))
        assertTrue(continuationType.contains(Response::class.java.name))
    }

    private fun get200Method() = APIInterface::class.java.methods.single { it.name == "get200" }
}
