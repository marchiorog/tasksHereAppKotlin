package com.example.taskshereapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

data class Tarefa(
    val id: String,
    val titulo: String,
    val horario: String,
    val cor: String,
    val icone: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = Firebase.auth.currentUser?.uid
    val db = Firebase.firestore

    var search by remember { mutableStateOf("") }
    var tarefas by remember { mutableStateOf<List<Tarefa>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }


    fun carregarTarefas() {
        if (userId != null) {
            isLoading = true
            db.collection("tarefas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.documents.mapNotNull { doc ->
                        val id = doc.id
                        val titulo = doc.getString("titulo") ?: return@mapNotNull null
                        val horario = doc.getString("horario") ?: ""
                        val cor = doc.getString("cor") ?: ""
                        val icone = doc.getString("icone") ?: ""
                        Tarefa(id = id, titulo = titulo, horario = horario, cor = cor, icone = icone)
                    }
                    tarefas = lista
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } else {
            Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(userId) {
        carregarTarefas()
    }

    val filteredTarefas = tarefas.filter {
        it.titulo.contains(search, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, vertical = 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(45.dp))

                Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Home",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                IconButton(
                    onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sair",
                        tint = Color.Black
                    )
                }

            }


            SearchField(
                titulo = search,
                onTituloChange = { search = it },
                onSearch = { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Minhas tarefas",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                items(filteredTarefas) { tarefa ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .background(Color(android.graphics.Color.parseColor(tarefa.cor)), shape = RoundedCornerShape(10.dp))
                            .padding(6.dp)
                            .clickable { /* Handle click */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = tarefa.icone, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = tarefa.titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = tarefa.horario, fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = {
                                val db = FirebaseFirestore.getInstance()
                                db.collection("tarefas").document(tarefa.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Tarefa concluída!", Toast.LENGTH_SHORT).show()
                                        carregarTarefas()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Erro ao concluir tarefa", Toast.LENGTH_SHORT).show()
                                    }
                                carregarTarefas()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Concluir tarefa",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }

                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("adicionarTarefa") },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar tarefa")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(titulo: String, onTituloChange: (String) -> Unit, onSearch: () -> Unit) {
    TextField(
        value = titulo,
        onValueChange = onTituloChange,
        placeholder = { Text("Buscar") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(6.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),
        colors = textFieldColors(
            containerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        )
    )
}


