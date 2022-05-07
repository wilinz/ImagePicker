package com.wilinz.imagepicker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

object ImagePickerTheme {
    var ImagePreviewDarkColorPalette = darkColors(
        primary = Color.Black,
        primaryVariant = Color.Gray,
        secondary = Teal200
    )

    var ImagePreviewLightColorPalette = lightColors(
        primary = Color.Black,
        primaryVariant = Color.Gray,
        secondary = Teal200
    )

    var DarkColorPalette = darkColors(
        primary = Purple200,
        primaryVariant = Purple700,
        secondary = Teal200
    )

    var LightColorPalette = lightColors(
        primary = Purple500,
        primaryVariant = Purple700,
        secondary = Teal200
    )

    var Typography = com.wilinz.imagepicker.ui.theme.Typography

    var Shapes = com.wilinz.imagepicker.ui.theme.Shapes
}


/* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/

@Composable
fun ImagePickerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        ImagePickerTheme.DarkColorPalette
    } else {
        ImagePickerTheme.LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = ImagePickerTheme.Typography,
        shapes = ImagePickerTheme.Shapes,
        content = {
            SetSystemUi(color = MaterialTheme.colors.primary)
            content()
        }
    )
}

@Composable
fun ImagePreviewTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        ImagePickerTheme.ImagePreviewDarkColorPalette
    } else {
        ImagePickerTheme.ImagePreviewLightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = ImagePickerTheme.Typography,
        shapes = ImagePickerTheme.Shapes,
        content = {
            SetSystemUi(color = Color.Transparent)
            content()
        }
    )
}

@Composable
fun SetSystemUi(color: Color) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = useDarkIcons
        )
    }
}