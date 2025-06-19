package com.example.taskshereapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskshereapp.ui.theme.TasksHereAppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            TasksHereAppTheme {
                val navigationController = rememberNavController()

                NavHost(navController = navigationController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navigationController)
                    }
                    composable("cadastro") {
                        CadastroScreen(navigationController)
                    }
                    composable("home") {
                        HomeScreen(navigationController)
                    }
                    composable("adicionarTarefa") {
                        AdicionarTarefaScreen(navigationController)
                    }
                }
            }
        }
    }
}
