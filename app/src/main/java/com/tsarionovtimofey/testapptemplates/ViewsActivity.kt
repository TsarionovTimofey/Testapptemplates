package com.tsarionovtimofey.testapptemplates

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tsarionovtimofey.utils.data.ActivityRequiredDelegate
import com.tsarionovtimofey.utils.data.files.AndroidFilesRepository
import com.tsarionovtimofey.utils.data.handleActivityRequired
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


const val REQUEST_CODE_SIGN_IN = 0

class ViewsActivity : AppCompatActivity() {
    private lateinit var androidFilesRepository: AndroidFilesRepository
    lateinit var auth: FirebaseAuth
    lateinit var btnGoogle: Button
    lateinit var btnPickPhoto: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        androidFilesRepository = AndroidFilesRepository(applicationContext, Dispatchers.IO, Dispatchers.Main)
        handleActivityRequired(setOf(androidFilesRepository))
        auth = FirebaseAuth.getInstance()
        btnGoogle = findViewById(R.id.btn_sign_in_google)
        btnGoogle.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id))
                .requestEmail()
                .requestProfile()
                .build()
            val signInClient = GoogleSignIn.getClient(this, options)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }
        btnPickPhoto = findViewById(R.id.btn_pick_photo)
        btnPickPhoto.setOnClickListener {
            lifecycleScope.launch {
                androidFilesRepository.pickVisualMedia(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.VideoOnly
                    )
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result ?: return
            googleAuthForFirebase(account)
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewsActivity, "success", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewsActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
