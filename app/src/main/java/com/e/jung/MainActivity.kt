package com.e.jung

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.e.jung.savememo.SaveMemo
import com.e.jung.savememo.SaveMemoDB
import com.e.jung.ui.theme.JungTheme

lateinit var saveMemoDB: SaveMemoDB

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveMemoDB =
            Room.databaseBuilder(applicationContext, SaveMemoDB::class.java, "saveMemoDB").build()
        setContent {
            JungTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Greeting() {
    val context = LocalContext.current

    var visibility by remember {
        mutableStateOf(false)
    }
    var word by rememberSaveable {
        mutableStateOf("")
    }
    val list: SnapshotStateList<SaveMemo> =
        saveMemoDB.dao().getAllasFlow()
            .collectAsState(initial = emptyList()).value.toMutableStateList()
    Scaffold(topBar = {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {

//                val intent = Intent(context, SearchActivity::class.java)
//                context.startActivity(intent)
                visibility = !visibility

            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Menu")
            }
            Text(text = "빡코딩")
            IconButton(onClick = {
                val r = Runnable {
                    list.removeAll(list)
                    saveMemoDB.dao().deleteAll()
                }
                Thread(r).start()
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            val intent = Intent(context, WriteActivity::class.java)
            intent.putExtra("num", 0)
            context.startActivity(intent)
        }) {
            Text(text = "글쓰기")
        }
    }) {
        BackHandler() {
            if (visibility) {
                word = ""
                visibility = false
            }
        }
        Column() {
            AnimatedVisibility(visible = visibility) {
                Row() {
                    OutlinedTextField(
                        value = word,
                        onValueChange = { word = it },
                        Modifier.fillMaxWidth()
                    )
                }


            }
            LazyColumn() {
                items(list) { memo ->
                    if (visibility) {
                        if (word == "") {
                            MemoCard(memo = memo)
                        } else {
                            if (memo.contents.contains(word) && memo.password == "") {
                                MemoCard(memo = memo)
                            }
                        }
                    } else {
                        MemoCard(memo = memo)
                    }
                }
            }
        }
    }
}


@Composable
fun MemoCard(memo: SaveMemo) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                val intent = Intent(context, MemoActivity::class.java)
                intent.putExtra("num", memo.num)
                intent.putExtra("pwd", memo.password)
                context.startActivity(intent)
            },
        elevation = 8.dp
    ) {
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f, fill = false)) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = if (memo.password == "") memo.contents else "비밀메모 입니다.",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JungTheme {
        Greeting()
    }
}