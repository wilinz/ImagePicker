package com.wilinz.imagepicker.ui.page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.*
import com.wilinz.imagepicker.util.Album
import com.wilinz.imagepicker.util.AlbumUtil
import com.wilinz.imagepicker.ImagePickerActivity
import com.wilinz.imagepicker.PickerOption
import com.wilinz.imagepicker.ImagePickerStringResources
import com.wilinz.imagepicker.ImagePreviewActivity
import com.wilinz.imagepicker.ui.widget.NumberRadioButton
import com.wilinz.imagepicker.ui.widget.NumberRadioButtonLarge
import com.wilinz.imagepicker.ui.widget.TextButtonOnPrimary
import com.wilinz.imagepicker.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ImageItem(val uri: Uri, val selected: Boolean = false)

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PickerPage(option: PickerOption) {
    val lazyGridState = rememberLazyGridState()
    var bucketId by remember {
        mutableStateOf<String>("-1")
    }
    val images = remember {
        mutableStateListOf<ImageItem>()
    }
    val selectImagesIndex = remember {
        mutableStateListOf<Int>()
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val requestGetImages =
        rememberPermissionState(
            permission = Manifest.permission.READ_EXTERNAL_STORAGE,
            onPermissionResult = {
                if (it) {
                    scope.launch {
                        val albums = AlbumUtil.getImages(context.contentResolver, bucketId, listOf("video/*"))
                        images.apply {
                            clear()
                            addAll(albums.map { ImageItem(it) })
                        }
                    }
                } else {
                    toast(context, option.stringResources.permissionDenied)
                }
            },
        )

    Scaffold(
        topBar = {
            MyTopAppBar(
                option,
                onAlbumSelected = {
                    bucketId = it.id
                    requestGetImages.launchPermissionRequest()
                    scope.launch { lazyGridState.scrollToItem(0) }
                },
                onBack = { if (context is Activity) context.finish() }
            )
        },
        bottomBar = {
            MyBottomAppBar(
                option,
                onPreview = {
                    ImagePreviewActivity.start(context, selectImagesIndex.map { images[it].uri })
                },
                onEdit = {},
                onConfirm = {
                    if (context is Activity) {
                        val selectedImages =
                            selectImagesIndex.map { images[it].uri }.toTypedArray()
                        context.setResult(
                            Activity.RESULT_OK,
                            Intent().putExtra(ImagePickerActivity.KEY_RESULT_DATA, selectedImages)
                        )
                        context.finish()
                    }
                },
                selectImagesIndex
            )
        }
    ) {
        Content(
            option = option,
            permission = requestGetImages,
            lazyGridState = lazyGridState,
            it = it,
            images = images,
            selectedList = selectImagesIndex,
            onPreview = { uri ->
                ImagePreviewActivity.start(context, listOf(uri))
            })
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
private fun Content(
    option: PickerOption,
    permission: PermissionState,
    lazyGridState: LazyGridState,
    it: PaddingValues,
    images: SnapshotStateList<ImageItem>,
    selectedList: SnapshotStateList<Int>,
    onPreview: (Uri) -> Unit,
) {
    LaunchedEffect(key1 = Unit, block = {
        permission.launchPermissionRequest()
    })
    var size by remember { mutableStateOf(DpSize.Zero) }
    val localDensity = LocalDensity.current
    val context = LocalContext.current

    LazyVerticalGrid(
        cells = GridCells.Adaptive(option.gridExpectedSize),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        state = lazyGridState,
        modifier = Modifier.padding(it)
    ) {
        itemsIndexed(images) { index, imageItem ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        if (size.width == 0.dp && size.height == 0.dp) {
                            with(localDensity) {
                                size = DpSize(it.width.toDp(), it.width.toDp())
                            }
                        }
                    }
                    .height(size.height)
            ) {

                AsyncImage(
                    model = imageItem.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(
                            width = size.width,
                            height = size.height,
                        )
                        .clickable { onPreview(imageItem.uri) },
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                )

                NumberRadioButtonLarge(
                    selected = imageItem.selected,
                    onClick = {
                        if (!imageItem.selected) {
                            if (selectedList.size < option.maxSelectable) {
                                selectedList.add(index)
                                images[index] =
                                    imageItem.copy(selected = !imageItem.selected)
                            } else {
                                toast(
                                    context,
                                    String.format(
                                        option.stringResources.maxSelectableDesc,
                                        option.maxSelectable
                                    )
                                )
                            }
                        } else {
                            selectedList.remove(index)
                            images[index] =
                                imageItem.copy(selected = !imageItem.selected)
                        }

                    },
                    serialNumber = {
                        val i = selectedList.indexOf(index)
                        if (i >= 0) {
                            Text(
                                text = "${i + 1}",
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colors.primary,
                        unselectedColor = MaterialTheme.colors.primary
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                )

            }


        }
    }
}

@Composable
private fun MyBottomAppBar(
    option: PickerOption,
    onPreview: () -> Unit = {},
    onEdit: () -> Unit = {},
    onConfirm: () -> Unit = {},
    selectImagesIndex: SnapshotStateList<Int>
) {
    BottomAppBar {
        TextButtonOnPrimary(
            onClick = onPreview,
            enabled = selectImagesIndex.isNotEmpty()
        ) {
            Text(text = option.stringResources.preview)
        }
        TextButtonOnPrimary(
            onClick = onEdit,
            enabled = selectImagesIndex.isNotEmpty()
        ) {
            Text(text = option.stringResources.edit)
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButtonOnPrimary(
            onClick = onConfirm,
            enabled = selectImagesIndex.isNotEmpty()
        ) {
            Text(text = option.stringResources.confirm + if (selectImagesIndex.isNotEmpty()) "(${selectImagesIndex.size})" else "")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MyTopAppBar(
    option: PickerOption,
    onAlbumSelected: (Album) -> Unit = {},
    onBack: () -> Unit = {}
) {
    TopAppBar {
        IconButton(onClick = { onBack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = option.stringResources.back,
                tint = MaterialTheme.colors.onPrimary
            )
        }
        Column {

            val context = LocalContext.current
            var expanded by remember {
                mutableStateOf(false)
            }
            val list = remember {
                mutableStateListOf<Album>()
            }
            var selectedAlbum by remember {
                mutableStateOf(Album(displayName = option.stringResources.allImage))
            }
            val scope = rememberCoroutineScope()
            val permission =
                rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    onPermissionsResult = {
                        scope.launch(Dispatchers.IO) {
                            val albums =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    AlbumUtil.getAlbums(context.contentResolver)
                                } else {
                                    TODO("VERSION.SDK_INT < O")
                                }
                            withContext(Dispatchers.Main) {
                                albums.let { it1 -> list.addAll(it1) }
                                if (albums.isNotEmpty()) selectedAlbum = list.first()
                            }
                        }
                    },
                )

            LaunchedEffect(key1 = Unit, block = {
                permission.launchMultiplePermissionRequest()
            })

            TextButtonOnPrimary(
                onClick = { expanded = true }
            ) {
                Text(text = "${selectedAlbum.displayName.toLocalizedAlbumName(option.stringResources)}(${selectedAlbum.count})")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = option.stringResources.moreAlbum,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEachIndexed { index, album ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        if (selectedAlbum != album) {
                            selectedAlbum = album
                            onAlbumSelected(album)
                        }
                    }) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = album.uri,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = album.displayName.toLocalizedAlbumName(option.stringResources))
                                Text(text = album.count.toString())
                            }
                        }
                    }
                }
            }
        }

    }
}

fun String.toLocalizedAlbumName(stringResources: ImagePickerStringResources): String {
    return when (this.toLowerCase()) {
        "all" -> stringResources.allImage
        "camera" -> stringResources.camera
        "screenshots" -> stringResources.screenshots
        "download" -> stringResources.download
        else -> this
    }
}