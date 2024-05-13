package com.example.myapplication2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.myapplication2.ui.theme.MyApplication2Theme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

val supabase = createSupabaseClient(
    supabaseUrl = "https://eobckgfvzhrvreqsjivr.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVvYmNrZ2Z2emhydnJlcXNqaXZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUzNDAyNDAsImV4cCI6MjAzMDkxNjI0MH0.Kz80M_x1osGvYhN-9OVzp1OKZD0JZG3e8mCwHs-T0zk"
) {
    install(Postgrest)
}

@Serializable
data class Vid(
    val id : Int,
    val title: String,
    val description: String,
    val likes: Int,
    val channelName: String,
    val videoUrl: String,
    val imageUrl: String
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    var searchText by remember { mutableStateOf("") }
//                    Scaffold(
//                        topBar = { SearchBar(searchText, onSearchTextChanged = { searchText = it })
//                            Spacer(modifier = Modifier.height(4.dp))
//                                 },
//                        content = { paddingValues ->
//                            VideoList(searchText, paddingValues)
//                        }
//                    )
                    Scaffold(
                        topBar = { SearchBar(searchText, onSearchTextChanged = { searchText = it}) }
                    ) { padding ->
                        Box(
                            modifier = Modifier.padding(padding))
                        {
                            VideoList(searchText )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoList(searchText: String) {
    var vids by remember { mutableStateOf<List<Vid>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            vids = supabase.from("videos").select().decodeList<Vid>()
        }
    }

    // Filter videos based on search text
    val filteredVids = if (searchText.isBlank()) {
        vids
    } else {
        vids.filter { it.title.contains(searchText, ignoreCase = true) }
    }

    val context = LocalContext.current

    LazyColumn() {
        items(filteredVids, key = { vid -> vid.id}) { vid ->
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                Image(
                    painter = rememberImagePainter(vid.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(16f/9f)
                        .clickable {
                            // Open video player on image click
                            openVideoPlayer(context, vid)
                        }
                )
                Text(vid.title, fontSize = 18.sp)
                Text("Likes: ${vid.likes}", fontSize = 12.sp)
                Text("Channel: ${vid.channelName}", fontSize = 15.sp)
            }
        }
    }
}
fun openVideoPlayer(context: Context, vid : Vid) {
    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
        putExtra("videoUrl", vid.videoUrl)
        putExtra("title", vid.title)
        putExtra("channelName", vid.channelName)
        putExtra("likes", vid.likes)
        putExtra("description", vid.description)
    }
    context.startActivity(intent)
}


@Composable
fun SearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    TextField(
        value = searchText,
        onValueChange = {
            onSearchTextChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp)
            .background(color = Color.Black),
        placeholder = { Text("Search videos...") }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplication2Theme {
    }
}