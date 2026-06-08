package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.StudentPortalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TemyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TemyViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Determine whether user is viewing a specialized full-screen, like active Exam Quiz
                var currentScreen by remember { mutableStateOf("TABS_CONTAINER") } // "TABS_CONTAINER" or "QUIZ"
                
                // Active sub-tab under container
                var selectedTab by remember { mutableStateOf("STUDENTS") } // "MANAGER", "STUDENTS", "LIBRARY", "REPORTS"

                val bookmarkList by viewModel.allBookmarks.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentScreen == "QUIZ") {
                        QuizScreen(
                            viewModel = viewModel,
                            onBackClicked = {
                                currentScreen = "TABS_CONTAINER"
                            }
                        )
                    } else {
                        Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Text(
                                            text = when (selectedTab) {
                                                "MANAGER" -> "لوحة التحكم · المدير"
                                                "STUDENTS" -> "تيمي إكزام · الطلاب"
                                                "LIBRARY" -> "مكتبة الملخصات والمذكرات"
                                                else -> "تقارير الفهم والتقدم"
                                            },
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            },
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 4.dp
                                ) {
                                    // tab 1: Admin / Manager (المدير)
                                    NavigationBarItem(
                                        selected = selectedTab == "MANAGER",
                                        onClick = { selectedTab = "MANAGER" },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = "المدير",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = { Text("المدير", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )

                                    // tab 2: Portal link simulation (الطلاب)
                                    NavigationBarItem(
                                        selected = selectedTab == "STUDENTS",
                                        onClick = { selectedTab = "STUDENTS" },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = "الطلاب",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = { Text("رابط الطلاب", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )

                                    // tab 3: Lectures files library
                                    NavigationBarItem(
                                        selected = selectedTab == "LIBRARY",
                                        onClick = { selectedTab = "LIBRARY" },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.LibraryBooks,
                                                contentDescription = "المكتبة",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = { Text("المكتبة", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )

                                    // tab 4: Solved Stats & Saved Bookmarks
                                    NavigationBarItem(
                                        selected = selectedTab == "REPORTS",
                                        onClick = { selectedTab = "REPORTS" },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (bookmarkList.isNotEmpty()) {
                                                        Badge {
                                                            Text("${bookmarkList.size}")
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Insights,
                                                    contentDescription = "التقارير",
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        },
                                        label = { Text("التقرير", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            when (selectedTab) {
                                "MANAGER" -> {
                                    AdminScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                                "STUDENTS" -> {
                                    StudentPortalScreen(
                                        viewModel = viewModel,
                                        onCourseSelected = { course ->
                                            viewModel.startQuiz(course)
                                            currentScreen = "QUIZ"
                                        },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                                "LIBRARY" -> {
                                    LibraryScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                                "REPORTS" -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
