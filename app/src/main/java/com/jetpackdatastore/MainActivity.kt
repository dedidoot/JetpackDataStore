package com.jetpackdatastore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.jetpackdatastore.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class MainActivity : AppCompatActivity() {

    private var dataStore: DataStore<Preferences>? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStore = createDataStore(name = "settings")

        binding.saveButton.setOnClickListener {
            showLoading()
            lifecycleScope.launch {
                save(getKeyEditText, getValueEditText)
                showSnackBar()
                goneLoading()
            }
        }

        binding.readButton.setOnClickListener {
            showLoading()
            lifecycleScope.launch {
                val value = read(getKeyReadEditText) ?: "Data not found"
                binding.readTextView.text = value
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

    private suspend fun save(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore?.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore?.data?.first()
        return preferences?.get(dataStoreKey)
    }

    private fun showSnackBar(message: String = "Saved successfully") {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    val getKeyEditText: String get() = binding.keyEditText.text.toString()
    val getKeyReadEditText: String get() = binding.keyReadEditText.text.toString()
    val getValueEditText: String get() = binding.valueEditText.text.toString()
}
