package com.ygaberman.babypoo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ygaberman.babypoo.db.Activity
import com.ygaberman.babypoo.db.ActivityViewModel
import com.ygaberman.babypoo.db.ActivityViewModelFactory
import com.ygaberman.babypoo.ui.theme.BabyPooTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss")

class MainActivity : ComponentActivity() {
    private val activityViewModel: ActivityViewModel by viewModels {
        ActivityViewModelFactory((application as ActivityApplication).repository)
    }
    private var babyActivities: List<Activity> = mutableListOf()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityViewModel.allActivities.observe(this) { activities ->
            activities.let { babyActivities = it }
        }

        setContent {
            val navController = rememberNavController()

            BabyPooTheme {
                Scaffold(topBar = {
                    TopAppBar(title = {
                        Text(stringResource(R.string.app_name))
                    })
                },
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
                        composable("activity-create") { CreateActivity(navController, activityViewModel) }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityList(activities: List<Activity>) {
    if (activities.isEmpty()) {
        return Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("No activities recorded yet")
        }
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Activity Feed")
        }
        LazyColumn {
            for (activity in activities) {
                item {
                    ActivityRow(activity = activity)
                }
            }
        }
    }
}

@Composable
fun ActivityRow(activity: Activity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            Text(text = formatter.format(activity.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()))
        }
        Column {
            Row {
                Text(text = activity.type)
            }
            Row {
                Text(text = activity.notes)
            }
        }
    }
}

@Composable
fun CreateActivity(navController: NavController, activityViewModel: ActivityViewModel) {
    var typeModified by remember { mutableStateOf(false) }
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
                type = it
            },
            label = { Text("Type") },
            placeholder = { Text("ex: pee, poop, feed") },
            singleLine = true,
            isError = type.none { !it.isWhitespace() } && typeModified,
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            placeholder = { Text("ex: fed 50ml of formula") },
        )
        Button(
            onClick = {
                activityViewModel.insert(Activity(type = type, notes = notes))
                navController.navigate("activity-list") {
                    popUpTo("activity-list") { inclusive = true }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Add activity", Modifier.padding(start = 10.dp))
        }
    }
}
