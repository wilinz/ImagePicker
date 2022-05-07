package com.wilinz.imagepicker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wilinz.imagepicker.ui.page.PickerPage
import com.wilinz.imagepicker.ui.theme.ImagePickerTheme

class ImagePickerActivity : ComponentActivity() {

    companion object {
        const val KEY_RESULT_DATA = "data"

        fun getIntent(context: Context): Intent {
            return Intent(context, ImagePickerActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val option = ImagePicker.pickerOption
        setContent {
            ImagePickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PickerPage(option)
                }
            }
        }
    }

}

