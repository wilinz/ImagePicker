package com.wilinz.imagepicker

import android.os.Parcelable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickerOption(
    var maxSelectable: Int = 1,
    var mimeTypes: List<String> = listOf("image/*"),
    var gridExpectedSize: Dp = 100.dp,
    var stringResources: ImagePickerStringResources = ImagePickerStringResources()
) : Parcelable

@Parcelize
data class ImagePickerStringResources(
    var preview: String = "Preview",
    var edit: String = "Edit",
    var confirm: String = "Confirm",
    var allImage: String = "All",
    var camera: String = "Camera",
    var screenshots: String = "Screenshots",
    var download: String = "Download",
    var back: String = "Back",
    var moreAlbum: String = "More album",
    var maxSelectableDesc: String = "At most %d images can be selected",
    var permissionDenied: String = "Permission denied to read image"
) : Parcelable