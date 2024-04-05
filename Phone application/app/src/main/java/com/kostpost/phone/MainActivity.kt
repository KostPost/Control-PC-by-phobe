package com.kostpost.phone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kostpost.phone.ui.theme.PhoneApplicationTheme
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CommandButtons()
                }
            }
        }
    }
}

// Компонент с кнопками для различных команд
@Composable
fun CommandButtons() {
    val userName = "KostPost";

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = { sendCommand("SHUTDOWN",userName) }) {
            Text("Выключить ПК")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { sendCommand("SLEEP",userName) }) {
            Text("Спящий режим")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { sendCommand("REBOOT",userName) }) {
            Text("Перезагрузка")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { sendCommand("IDEA",userName) }) {
            Text("Запустить IntelliJ IDEA")
        }
    }
}

// Функция для отправки команды
fun sendCommand(command: String, userName: String) {
    Thread {
        connectToPC("192.168.0.103", 12345, command, userName)
    }.start()
}

// Функция для подключения и отправки команды
fun connectToPC(ipAddress: String, port: Int, userName: String, command: String) {
    try {
        val socket = Socket(ipAddress, port)
        val out = PrintWriter(socket.getOutputStream(), true)

        // Отправляем команду и имя пользователя, разделенные табуляцией
        out.println("$userName\t$command")
        out.flush() // Убедитесь, что данные были отправлены

        socket.close()
    } catch (e: Exception) {
        Log.e("connectToPC", "Не удалось подключиться", e)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PhoneApplicationTheme {
        CommandButtons()
    }
}
