package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.network.AntiBotGuard
import com.example.network.CallManager
import com.example.network.CallStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ñeñsi Chat", appName)
  }

  @Test
  fun `antibot guard generates and verifies challenges`() {
    val guard = AntiBotGuard.getInstance()
    val challenge = guard.createChallenge()
    assertNotNull(challenge)
    assertTrue(challenge.options.contains(challenge.correctAnswer))

    val result = guard.verifyAnswer(challenge.id, challenge.correctAnswer)
    assertTrue("La respuesta correcta debió ser aprobada por el escudo Antibot", result)
  }

  @Test
  fun `call manager incoming call accept flow`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val callManager = CallManager.getInstance(context)

    callManager.triggerIncomingCall("Miguel Torres")
    val call = callManager.activeCall.value
    assertNotNull(call)
    assertEquals(CallStatus.INCOMING_RINGING, call?.status)

    callManager.answerCall()
    assertEquals(CallStatus.CONNECTED, callManager.activeCall.value?.status)

    callManager.endCall()
  }
}
