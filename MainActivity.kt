package com.vidora.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Bg = Color(0xFF09070F)
private val Card = Color(0xFF171321)
private val Purple = Color(0xFF9B5CFF)
private val Pink = Color(0xFFFF4FD8)

data class Video(val title: String, val style: String, val date: String)

enum class Screen { HOME, CREATE, LIBRARY, PROFILE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VidoraApp() }
    }
}

@Composable
fun VidoraApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var videos by remember { mutableStateOf(listOf(Video("Путешествие улитки Джакуй", "Cartoon", "Сегодня"))) }
    var prompt by remember { mutableStateOf("") }
    var style by remember { mutableStateOf("Cinematic") }
    var duration by remember { mutableStateOf("30 сек") }
    var generating by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Card, primary = Purple)) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onCreate = { screen = Screen.CREATE },
                    onLibrary = { screen = Screen.LIBRARY },
                    onProfile = { screen = Screen.PROFILE }
                )
                Screen.CREATE -> CreateScreen(prompt, { prompt = it }, style, { style = it }, duration, { duration = it }, generating, progress,
                    onGenerate = { if (prompt.isNotBlank()) { generating = true; progress = 0f } },
                    onBack = { screen = Screen.HOME })
                Screen.LIBRARY -> LibraryScreen(videos, { screen = Screen.HOME })
                Screen.PROFILE -> ProfileScreen({ screen = Screen.HOME })
            }
            if (generating) {
                LaunchedEffect(Unit) {
                    for (i in 1..30) { delay(100); progress = i / 30f }
                    videos = listOf(Video(prompt.take(45), style, "Только что")) + videos
                    generating = false; prompt = ""; screen = Screen.LIBRARY
                }
            }
        }
    }
}

@Composable fun Header(title: String, onBack: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) { Text("‹", fontSize = 36.sp, color = Color.White, modifier = Modifier.clickable { onBack() }); Spacer(Modifier.width(12.dp)) }
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable fun HomeScreen(onCreate: () -> Unit, onLibrary: () -> Unit, onProfile: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("VIDORA", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Color.White); Text("AI VIDEO STUDIO", color = Purple, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            Text("⚙", fontSize = 25.sp, color = Color.White, modifier = Modifier.clickable { onProfile() })
        }
        Spacer(Modifier.height(30.dp))
        Box(Modifier.fillMaxWidth().height(220.dp).background(Brush.linearGradient(listOf(Purple, Pink)), RoundedCornerShape(28.dp)).clickable { onCreate() }) {
            Column(Modifier.fillMaxSize().padding(26.dp), verticalArrangement = Arrangement.Center) {
                Text("Создай видео", fontSize = 31.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Одна идея → готовый ролик", color = Color.White.copy(.88f), fontSize = 16.sp)
                Spacer(Modifier.height(22.dp)); Button(onClick = onCreate, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) { Text("+ Создать") }
            }
        }
        Spacer(Modifier.height(24.dp)); Text("Быстрые шаблоны", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp)); Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { listOf("TikTok", "Reels", "Cartoon").forEach { Template(it, onCreate) } }
        Spacer(Modifier.weight(1f)); OutlinedButton(onClick = onLibrary, modifier = Modifier.fillMaxWidth()) { Text("Мои видео") }
    }
}

@Composable fun Template(name: String, click: () -> Unit) { Card(Modifier.width(105.dp).height(70.dp).clickable { click() }) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(name, color = Color.White) } } }

@Composable fun CreateScreen(prompt: String, setPrompt: (String) -> Unit, style: String, setStyle: (String) -> Unit, duration: String, setDuration: (String) -> Unit, generating: Boolean, progress: Float, onGenerate: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Header("Новое видео", onBack)
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item {
                Text("Опиши видео", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = prompt, onValueChange = setPrompt, modifier = Modifier.fillMaxWidth().height(150.dp), placeholder = { Text("Например: улитка Джакуй путешествует по Парижу") }, maxLines = 6)
                Spacer(Modifier.height(20.dp)); Text("Стиль", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp); Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Cinematic", "Cartoon", "Anime").forEach { s -> FilterChip(selected = style == s, onClick = { setStyle(s) }, label = { Text(s) }) } }
                Spacer(Modifier.height(18.dp)); Text("Длительность", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp); Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("15 сек", "30 сек", "60 сек").forEach { d -> FilterChip(selected = duration == d, onClick = { setDuration(d) }, label = { Text(d) }) } }
                Spacer(Modifier.height(18.dp)); Text("Формат: 9:16 • вертикальное видео", color = Color.Gray)
                Spacer(Modifier.height(20.dp)); Button(onClick = onGenerate, enabled = prompt.isNotBlank() && !generating, modifier = Modifier.fillMaxWidth().height(54.dp)) { Text(if (generating) "Генерируем… ${(progress * 100).toInt()}%" else "✨ Сгенерировать видео") }
                if (generating) { Spacer(Modifier.height(18.dp)); LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth()) }
                Spacer(Modifier.height(30.dp)); Text("Сейчас включён DEMO-режим. Реальный AI подключается через защищённый backend, чтобы ключи провайдеров не хранились в APK.", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable fun LibraryScreen(videos: List<Video>, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) { Header("Мои видео", onBack); LazyColumn(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { items(videos) { v -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(v.title, color = Color.White, fontWeight = FontWeight.Bold); Spacer(Modifier.height(5.dp)); Text("${v.style} • ${v.date} • 9:16", color = Color.Gray); Spacer(Modifier.height(12.dp)); Text("▶  Готово", color = Purple) } } } } }
}

@Composable fun ProfileScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) { Header("Профиль", onBack); Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp)) { Text("VIDORA FREE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp); Spacer(Modifier.height(8.dp)); Text("2 бесплатных генерации в месяц", color = Color.Gray) } }
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("⭐ Перейти на Pro — €19.99/мес") }
        Text("Оплата и подписки будут подключены на следующем этапе.", color = Color.Gray, fontSize = 13.sp)
    } }
}
