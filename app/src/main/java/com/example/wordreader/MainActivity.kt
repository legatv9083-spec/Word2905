package com.example.wordreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WordReaderScreen()
                }
            }
        }
    }
}

@Composable
fun WordReaderScreen() {
    // Состояние экрана: показываем текст или читаем слово
    var words by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }

    val isReading = words.isNotEmpty()

    if (!isReading) {
        InputScreen(
            onSubmit = { text ->
                val parsed = text
                    .split(Regex("\\s+"))
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                if (parsed.isNotEmpty()) {
                    words = parsed
                    currentIndex = 0
                }
            }
        )
    } else {
        ReadingScreen(
            word = words[currentIndex],
            onNext = {
                if (currentIndex + 1 >= words.size) {
                    // Конец — возвращаемся к экрану ввода
                    words = emptyList()
                    currentIndex = 0
                } else {
                    currentIndex++
                }
            }
        )
    }
}

@Composable
fun InputScreen(onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вставьте текст для чтения",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = "Введите или вставьте текст...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSubmit(text) },
            enabled = text.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Начать", fontSize = 18.sp)
        }
    }
}

@Composable
fun ReadingScreen(word: String, onNext: () -> Unit) {
    // Кликабельная область на весь экран
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickableWithoutRipple { onNext() }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Подсказка внизу
        Text(
            text = "Нажмите для продолжения",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

/**
 * Модификатор клика без ripple-эффекта (чтобы не отвлекать при чтении)
 */
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    )
