package com.e.jung

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.RoundedCorner
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Greeting() {
    val context = LocalContext.current

    var visibility by remember {
        mutableStateOf(false)
    }
    var deleteVisibility by remember {
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
                visibility = !visibility

            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Menu")
            }
            Text(text = "빡코딩")
            IconButton(onClick = {
                deleteVisibility = true
//                val r = Runnable {
//                    list.removeAll(list)
//                    saveMemoDB.dao().deleteAll()
//                }
//                Thread(r).start()
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
            Text(text = "메모 작성")
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
            Box() {
                androidx.compose.animation.AnimatedVisibility(visible = deleteVisibility) {
                    AlertDialog(onDismissRequest = { deleteVisibility = false },
                        title = { Text(text = "모두 삭제") },
                        text = { Text(text = "모든 메모가 삭제 됩니다.") },
                        dismissButton = {
                            TextButton(onClick = { deleteVisibility = false }) {
                                Text(text = "취소")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val r = Runnable {
                                    list.removeAll(list)
                                    saveMemoDB.dao().deleteAll()
                                }
                                Thread(r).start()
                                deleteVisibility = false
                            }) {
                                Text(text = "확인")
                            }
                        })
                }
            }
            LazyColumn() {
                itemsIndexed(items = list, key = { index, memo ->
                    memo.hashCode()
                }) { index, memo ->
                    val state = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                val r = Runnable {
                                    list.removeAll(list)
                                    saveMemoDB.dao().delete(memo.num)
                                }
                                Thread(r).start()
                            }
                            true
                        }
                    )
                    SwipeToDismiss(
                        state = state,
                        background = {
                            val color = when (state.dismissDirection) {
                                DismissDirection.StartToEnd -> Color.Transparent
                                DismissDirection.EndToStart -> Color.Red
                                null -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(74.dp)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "",
                                    tint = Color.White,
                                    modifier = Modifier.align(
                                        Alignment.CenterEnd
                                    )
                                )
                            }
                        },
                        dismissContent = {
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
                        },
                        directions = setOf(DismissDirection.EndToStart)
                    )

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
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = if (memo.password == "") memo.contents else "비밀메모 입니다.",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .height(20.dp)
                            .align(Alignment.CenterHorizontally),
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