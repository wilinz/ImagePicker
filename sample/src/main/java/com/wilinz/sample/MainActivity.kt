package com.wilinz.sample

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wilinz.imagepicker.ImagePicker
import com.wilinz.imagepicker.ImagePickerResult
import com.wilinz.imagepicker.PickerOption
import com.wilinz.imagepicker.ImagePickerStringResources
import com.wilinz.sample.ui.theme.DarkColorPalette
import com.wilinz.sample.ui.theme.ImagePickerTheme
import com.wilinz.sample.ui.theme.LightColorPalette

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.wilinz.imagepicker.ui.theme.ImagePickerTheme.let {
            it.DarkColorPalette = DarkColorPalette
            it.LightColorPalette = LightColorPalette
        }
        ImagePicker.pickerOption = PickerOption(
            maxSelectable = 9,
            stringResources = ImagePickerStringResources(
                preview = getString(R.string.preview),
                edit = getString(R.string.edit),
                confirm = getString(R.string.confirm),
                allImage = getString(R.string.all),
                camera = getString(R.string.camera),
                screenshots = getString(R.string.screenshots),
                download = getString(R.string.download),
                back = getString(R.string.back),
                moreAlbum = getString(R.string.more_album),
                maxSelectableDesc = getString(R.string.max_selectable_desc),
                permissionDenied = getString(R.string.permission_denied)
            )
        )

        setContent {
            ImagePickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val images = remember {
                        mutableStateListOf<Uri>()
                    }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ImagePickerResult(),
                        onResult = {
                            images.apply {
                                clear()
                                addAll(it)
                            }
                        }
                    )
                    Column {
                        Button(onClick = { launcher.launch(null) }) {
                            Text(text = "select image")
                        }
                        var dpSize by remember {
                            mutableStateOf(DpSize.Zero)
                        }
                        val density = LocalDensity.current

                        LazyVerticalGrid(
                            cells = GridCells.Adaptive(100.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            itemsIndexed(images) { index, uri ->
                                AsyncImage(
                                    model = uri,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onSizeChanged {
                                            if (dpSize.width == 0.dp) {
                                                with(density) {
                                                    dpSize =
                                                        DpSize(it.width.toDp(), it.width.toDp())
                                                }
                                            }
                                        }
                                        .height(dpSize.height),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ImagePickerTheme {
        Greeting("Android")
    }
}