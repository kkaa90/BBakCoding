package com.e.jung

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Visibility
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.e.jung.dataclass.Memo
import com.e.jung.savememo.SaveMemo
import com.e.jung.ui.theme.JungTheme
import kotlinx.coroutines.flow.filterNotNull

class MemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val num = intent.getIntExtra("num", 0)
        val temp = intent.getStringExtra("pwd")
        setContent {
            JungTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting2(num, temp!!)
                }
            }
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun Greeting2(num: Int, temp : String) {
    var memo = saveMemoDB.dao().getMemoAsFlow(num).filterNotNull()
        .collectAsState(initial = SaveMemo("","")).value
    val context = LocalContext.current
    var pwd by remember {
        mutableStateOf("")
    }
    var check = remember {
        mutableStateOf(0)
    }
    val openDialog = remember {
        mutableStateOf(true)
    }
    if (temp == pwd && check.value == 0) {
        openDialog.value = false
    } else {
        check.value = 3
        Dialog(onDismissRequest = { (context as Activity).finish() }) {
            Surface(
                modifier = Modifier
                    .width(200.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Column() {
                    Text(text = "비밀번호 확인")
                    Spacer(modifier = Modifier.height(18.dp))
                    OutlinedTextField(value = pwd, onValueChange = { pwd = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation())
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        OutlinedButton(onClick = { (context as Activity).finish() }) {
                            Text(text = "취소")
                        }
                        OutlinedButton(onClick = {
                            if (pwd == temp) {
                                openDialog.value = false
                                check.value = 0
                            } else {
                                pwd = ""
                                Toast.makeText(
                                    context,
                                    "비밀번호가 다릅니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }) {
                            Text(text = "확인")
                        }
                    }
                }
            }

        }
    }
    var visibility by remember {
        mutableStateOf(false)
    }
    var adv = remember {
        mutableStateOf(false)
    }
    Scaffold(topBar = {
        AnimatedVisibility(visible = visibility) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { (context as Activity).finish() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
                Row() {
                    IconButton(onClick = {
                        val intent = Intent(context, WriteActivity::class.java)
                        intent.putExtra("num",memo.num)
                        context.startActivity(intent)
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    }
                    IconButton(onClick = { adv.value = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    }
                }
            }
        }
    }) {
        Box() {
            AnimatedVisibility(visible = adv.value) {
                ADialog(num = num, adv)
            }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .clickable { visibility = !visibility }) {
            if (!openDialog.value) Text(text =memo.contents)

            else {

            }
        }
    }
}

@Composable
fun ADialog(num: Int, visibility: MutableState<Boolean>) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text("메모 삭제") },
        text = { Text(text = "메모가 삭제됩니다.") },
        dismissButton = {
            TextButton(onClick = { }) {
                Text(text = "취소")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                visibility.value = false
                deleteMemo(num)
                (context as Activity).finish()
            }) {
                Text("확인")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    JungTheme {
    }
}

fun deleteMemo(num: Int) {
    val r = Runnable {
        saveMemoDB.dao().delete(num)
    }
    Thread(r).start()
}