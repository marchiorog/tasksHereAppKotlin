package com.example.taskshereapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taskshereapp.ui.theme.TasksHereAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdicionarTarefaScreen(navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var icone by remember { mutableStateOf("") }
    var corSelecionada by remember { mutableStateOf("#ffffff") }
    var horario by remember { mutableStateOf("") }

    val db = Firebase.firestore

    val userId = Firebase.auth.currentUser?.uid


    val predefinedColors = listOf(
        "#FFF4E3", "#E3FFE3", "#F9E6FF",
        "#E3F9FF", "#FFFCE3", "#E3FFF4"
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Adicionar tarefa",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(48.dp))
        }

        Text(
            text = "Nome",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textAlign = TextAlign.Start
        )

        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = { Text("Digite um nome") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(6.dp),
            colors = textFieldColors(
                containerColor = Color(0xFFF8F8F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ícone",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textAlign = TextAlign.Start
        )

        TextField(
            value = icone,
            onValueChange = { icone = it },
            placeholder = { Text("Escolha um emoji") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(6.dp),
            colors = textFieldColors(
                containerColor = Color(0xFFF8F8F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Horário",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textAlign = TextAlign.Start
        )

        TextField(
            value = horario,
            onValueChange = { horario = it },
            placeholder = { Text("Digite um horário") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(6.dp),
            colors = textFieldColors(
                containerColor = Color(0xFFF8F8F8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cor:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textAlign = TextAlign.Start
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            predefinedColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            color = Color(android.graphics.Color.parseColor(color)),
                            shape = CircleShape
                        )
                        .border(
                            width = if (corSelecionada == color) 2.dp else 0.dp,
                            color = if (corSelecionada == color) Color.Black else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { corSelecionada = color }
                )

            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (titulo.isNotBlank() && horario.isNotBlank()) {
                    val tarefa = hashMapOf(
                        "titulo" to titulo,
                        "icone" to icone,
                        "cor" to corSelecionada,
                        "horario" to horario,
                        "userId" to userId
                    )

                    db.collection("tarefas")
                        .add(tarefa)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Tarefa adicionada!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao adicionar tarefa", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF68BAE8))
        ) {
            Text(
                text = "Salvar",
                color = Color.White,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(7.dp)
            )
        }
    }
}
