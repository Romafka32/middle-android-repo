package com.example.androidpracticumcustomview.ui.theme

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */
@Composable
fun CustomContainerCompose(
    firstChild: @Composable (() -> Unit)?,
    secondChild: @Composable (() -> Unit)?,
    alphaAnimationDuration: Int = 2000,
    translationAnimationDuration: Int = 5000
) {
    val screenHight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current
    val offsetPx = with(density) { screenHight.toPx() / 2}

    val topOffset = remember { Animatable(offsetPx) }
    val bottomOffset = remember { Animatable(-offsetPx) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(alphaAnimationDuration)
            )
        }
        launch {
            topOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = translationAnimationDuration)
            )
        }
        launch {
            bottomOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = translationAnimationDuration)
            )
        }
    }
    runCatching {
        Box(modifier = Modifier.fillMaxSize()) {

            firstChild?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset{IntOffset(x = 0, y = topOffset.value.toInt())}
                        .graphicsLayer{this.alpha = alpha.value}
                ) {
                    it()
                }
            }

            secondChild?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset{IntOffset(x = 0, y = bottomOffset.value.toInt())}
                        .graphicsLayer{this.alpha = alpha.value}
                ){
                    it()
                }
            }
        }
    }.onFailure {
        Log.e("CustomContainerCompose", "Ошибка рендера дочерних компонентов", it)
    }

}