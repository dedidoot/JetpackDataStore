package com.jetpackdatastore

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.datastore.dataStore
import com.jetpackdatastore.databinding.ActivityMain2Binding

class Main2Activity : AppCompatActivity() {

    private val Context.dataStore by dataStore(
        fileName = "user-settings.json",
        serializer = UserSettingsSerializer(KeyManager())
    )

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener {
            showLoading()
            lifecycleScope.launch {
                save()
                showSnackBar()
                goneLoading()
            }
        }

        binding.decryptButton.setOnClickListener {
            showLoading()
            lifecycleScope.launch {
                val value = dataStore.data.first()
                binding.decryptTextView.text = value.toString()
                goneLoading()
            }
        }
    }

    private fun showLoading() {
        binding.loading.isVisible = true
    }

    private fun goneLoading() {
        binding.loading.isVisible = false
    }

    private suspend fun save() {
        lifecycleScope.launch {
            dataStore.updateData{
                UserModel(
                    username = binding.usernameEditText.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )
            }
        }
    }

    private fun showSnackBar(message: String = "Saved successfully") {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
