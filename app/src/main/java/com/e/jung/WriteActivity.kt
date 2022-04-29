package com.e.jung

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.colorspace.Rgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.e.jung.savememo.SaveMemo
import com.e.jung.ui.theme.JungTheme
import com.e.jung.ui.theme.customGray

class WriteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val m = intent.getIntExtra("num",0)

        setContent {
            JungTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting4(m)
                }
            }
        }
    }
}

@Composable
fun Greeting4(m : Int) {
//    var visibility by remember {
//        mutableStateOf(false)
//    }

    val context = LocalContext.current
    var content by remember {
        mutableStateOf("")
    }
    var pwd by remember {
        mutableStateOf("")
    }
    var check by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(true) {
        if (m != 0) {
            val r = Runnable {
                val memo = saveMemoDB.dao().getMemo(m)
                content = memo.contents
                if (memo.password != "") {
                    check = true
                    pwd = memo.password
                }
            }
            Thread(r).start()
        }
    }
    Scaffold(topBar = {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { (context as Activity).finish() }) {
                Text(text = "취소")
            }
            TextButton(onClick = {
                if(content.isEmpty()){
                    Toast.makeText(
                        context,
                        "공백은 작성할 수 없습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{
                    val r = Runnable {
                        val memo = SaveMemo(content,pwd)
                        if(m==0){
                            saveMemoDB.dao().insert(memo)
                        }
                        else{
                            saveMemoDB.dao().updateMemo(content,pwd,m)
                        }

                    }
                    Thread(r).start()
                    (context as Activity).finish()
                }
            }) {
                Text(text = "저장")
            }

        }
    }) {
        Column() {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    modifier = Modifier
                        .weight(1f),
                    enabled = check,
                    label = { Text(text = "암호") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = if (check) Color.White else customGray
                    ),
                    visualTransformation = VisualTransformation.Companion.None
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { check = !check }
                        .fillMaxHeight()
                ) {
                    Text(text = "암호 설정")
                    Checkbox(checked = check, onCheckedChange = { check = !check; pwd="" })
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White
                ),
                label = { Text(text = content.length.toString())}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    JungTheme {
        Greeting4(0)
    }
}