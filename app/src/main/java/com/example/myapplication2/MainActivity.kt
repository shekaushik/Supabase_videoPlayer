package com.example.myapplication2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication2.ui.theme.MyApplication2Theme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import coil.compose.rememberImagePainter

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
                    Scaffold(
                        topBar = { SearchBar(searchText, onSearchTextChanged = { searchText = it }) },
                        content = { paddingValues ->
                            VideoList(searchText, paddingValues)
                        }
                    )

                }
            }
        }
    }
}

@Composable
fun VideoList(searchText: String, paddingValues: PaddingValues) {
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

    LazyColumn(contentPadding = paddingValues) {
        items(filteredVids, key = { vid -> vid.id}) { vid ->
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Image(
                    painter = rememberImagePainter(vid.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Text(vid.title)
                Text("Likes: ${vid.likes}")
                Text("Channel: ${vid.channelName}")
                Text(vid.description)
            }
        }
    }
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
            .padding(16.dp),
        placeholder = { Text("Search videos...") }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplication2Theme {
    }
}