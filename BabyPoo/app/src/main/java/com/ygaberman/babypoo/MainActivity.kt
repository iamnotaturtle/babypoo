package com.ygaberman.babypoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ygaberman.babypoo.ui.theme.BabyPooTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            BabyPooTheme {
                Scaffold(topBar = {},
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("activity-create") {
                                    launchSingleTop = true
                                }
                                      },
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add an activity")
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "activity-list"
                    ) {
                        composable("activity-list") { ActivityList(babyActivities) }
                        composable("activity-create") { CreateActivity(navController) }
                    }
                }
            }
        }
    }
}

data class Activity(val type: String, val notes: String, val created_at: LocalDateTime = LocalDateTime.now())
// TODO: state/storage
val babyActivities = mutableListOf<Activity>()
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")

@Composable
fun ActivityList(activities: MutableList<Activity>) {
    if (activities.size == 0 ){
        return Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("No activities recorded yet")
        }
    }
    LazyColumn {
        for (activity in activities) {
            item {
                ActivityRow(activity = activity)
            }
        }
    }
}

@Composable
fun ActivityRow(activity: Activity) {
    Row {
        Text(text=activity.created_at.format(formatter))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = activity.type)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = activity.notes)
    }
}

@Composable
fun CreateActivity(navController: NavController) {
    var typeModified by remember { mutableStateOf(false)}
    var type by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Record an activity", modifier = Modifier.padding(16.dp))
        }
        OutlinedTextField(
            value = type,
            onValueChange = {
                typeModified = true
                type = it },
            label = { Text("Type")},
            placeholder = { Text("ex: pee, poop, feed") },
            singleLine = true,
            isError = type.none { !it.isWhitespace() } && typeModified,
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes")},
            placeholder = { Text("ex: fed 50ml of formula") },
        )
        Button(onClick = {
            babyActivities.add(Activity(type, notes))
            navController.navigate("activity-list") {}
        },
        modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Add activity",Modifier.padding(start = 10.dp))
        }
    }
}
