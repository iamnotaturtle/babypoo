package com.ygaberman.babypoo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ygaberman.babypoo.db.Activity
import com.ygaberman.babypoo.db.ActivityViewModel
import com.ygaberman.babypoo.db.ActivityViewModelFactory
import com.ygaberman.babypoo.ui.theme.BabyPooTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss")

class MainActivity : ComponentActivity() {
    private val activityViewModel: ActivityViewModel by viewModels {
        ActivityViewModelFactory((application as ActivityApplication).repository)
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()

            BabyPooTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
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
                    },
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "activity-list"
                    ) {
                        composable("activity-list") { ActivityList(activityViewModel) }
                        composable("activity-create") {
                            CreateActivity(
                                scope,
                                scaffoldState,
                                navController,
                                activityViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityList(activityViewModel: ActivityViewModel) {
    val activities: List<Activity> by activityViewModel.allActivities.observeAsState(listOf())

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Timeline")
        }

        if (activities.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("No activities recorded yet")
            }
        } else {
            LazyColumn {
                for (activity in activities) {
                    item {
                        ActivityRow(activity, activityViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityRow(activity: Activity, activityViewModel: ActivityViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { activityViewModel.delete(activity) })
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = formatter.format(
                activity.createdAt.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Card(
            elevation = 10.dp,

            ) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
            ) {
                Row(Modifier.padding(bottom = 10.dp)) {
                    Text("Type: ", fontWeight = FontWeight.Bold)
                    Text(text = activity.type)
                }
                Row {
                    Text("Notes: ", fontWeight = FontWeight.Bold)
                    Text(text = activity.notes)
                }
            }
        }
    }
}

enum class ActivityType(val type: String) {
    Feed("Feed 🍼"),
    Pee("Pee 💦"),
    Poop("Poop 💩"),
}

@Composable
fun CreateActivity(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    activityViewModel: ActivityViewModel
) {
    val initialType = "Type"
    var type by remember { mutableStateOf(initialType) }
    var notes by remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }

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
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Button(
                    onClick = {
                        expanded.value = true
                    }) {
                    Text(type)
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }) {
                        DropdownMenuItem(onClick = {
                            type = ActivityType.Feed.type
                            expanded.value = false
                        }) {
                            Text(ActivityType.Feed.type)
                        }
                        DropdownMenuItem(onClick = {
                            type = ActivityType.Pee.type
                            expanded.value = false
                        }) {
                            Text(ActivityType.Pee.type)
                        }
                        DropdownMenuItem(onClick = {
                            type = ActivityType.Poop.type
                            expanded.value = false
                        }) {
                            Text(ActivityType.Poop.type)
                        }
                    }
                }
            }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("ex: fed 50ml of formula") },
            )
        }
        Button(
            onClick = {
                if (type == initialType) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Please select an activity type")
                    }
                } else {
                    activityViewModel.insert(Activity(type = type, notes = notes))
                    navController.navigate("activity-list") {
                        popUpTo("activity-list") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Add activity", Modifier.padding(start = 10.dp))
        }
    }
}
