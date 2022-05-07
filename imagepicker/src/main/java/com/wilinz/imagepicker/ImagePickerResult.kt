package com.wilinz.imagepicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import java.util.ArrayList

class ImagePickerResult : ActivityResultContract<PickerOption?, List<Uri>>() {
    override fun createIntent(context: Context, input: PickerOption?): Intent {
        input?.let { ImagePicker.pickerOption = it }
        return ImagePickerActivity.getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val data = intent?.getParcelableArrayExtra(ImagePickerActivity.KEY_RESULT_DATA)?.let { it.map { it as Uri } }
        return data ?: emptyList()
    }

}