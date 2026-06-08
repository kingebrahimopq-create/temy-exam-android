package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.CourseEntity
import com.example.data.database.QuestionEntity
import com.example.ui.components.VisualExplanationContainer
import com.example.ui.viewmodel.TemyViewModel

val CourseEntity.composeColor: Color
    get() = try {
        Color(android.graphics.Color.parseColor(this.colorHex))
    } catch (e: Exception) {
        Color(0xFF2E7D32)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: TemyViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCourse by viewModel.activeCourse.collectAsState()
    val questionsList by viewModel.activeQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedIndex by viewModel.selectedOptionIndex.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val isCorrectResult by viewModel.isCurrentCorrect.collectAsState()
    val isBookmarked by viewModel.bookmarkedStatus.collectAsState()

    val scrollState = rememberScrollState()

    if (activeCourse == null || questionsList.isEmpty() || currentIndex !in questionsList.indices) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "جاري تحميل الأسئلة الأكاديمية...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        return
    }

    val currentCourse = activeCourse!!
    val currentQuestion = questionsList[currentIndex]
    val optionsList = listOf(
        currentQuestion.optionA,
        currentQuestion.optionB,
        currentQuestion.optionC,
        currentQuestion.optionD
    ).filter { it.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = currentCourse.titleAr,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "سؤال ${currentIndex + 1} من ${questionsList.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Right
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmarkActive() }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "حفظ السؤال",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- QUIZ SYSTEM SCORE PROGRESS HUD ---
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questionsList.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = currentCourse.composeColor,
                trackColor = currentCourse.composeColor.copy(alpha = 0.15f)
            )

            // --- QUESTION CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "السؤال المنهجي المعتمد:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = currentCourse.composeColor
                        ),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // --- OPTION CHOICES LIST ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                optionsList.forEachIndexed { i, option ->
                    OptionChoiceItem(
                        optionText = option,
                        index = i,
                        isSelected = selectedIndex == i,
                        isSubmitted = isSubmitted,
                        isCorrectIndex = currentQuestion.correctIndex == i,
                        accentColor = currentCourse.composeColor,
                        onClicked = { viewModel.selectOption(i) }
                    )
                }
            }

            // --- SUBMISSION ACTIONS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Back/Next Question
                Button(
                    onClick = { viewModel.nextQuestion() },
                    enabled = isSubmitted && currentIndex < questionsList.size - 1,
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentCourse.composeColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("السؤال التالي")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = null)
                    }
                }

                // Check Response
                Button(
                    onClick = { viewModel.submitAnswer() },
                    enabled = selectedIndex != null && !isSubmitted,
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("تحقق من الإجابة")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
            }

            // --- INTERACTIVE VISUAL AND TEXTUAL EXPLANATIONS ---
            AnimatedVisibility(
                visible = isSubmitted,
                enter = fadeIn() + expandVertically(animationSpec = spring())
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Result feedback Banner
                    ResultBannerBox(isCorrect = isCorrectResult)

                    // Text Explanation explanation
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "💡 الشرح الأكاديمي التفصيلي:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentQuestion.explanation,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Special Interactive Visual Animation component based on variant!
                    val stepsList = currentQuestion.explanationSteps
                        .split("|")
                        .filter { it.isNotBlank() }

                    VisualExplanationContainer(
                        variant = if (currentQuestion.explanationVariant == "VISUAL") "MATH" else currentQuestion.explanationVariant,
                        steps = stepsList
                    )
                }
            }
        }
    }
}

@Composable
fun OptionChoiceItem(
    optionText: String,
    index: Int,
    isSelected: Boolean,
    isSubmitted: Boolean,
    isCorrectIndex: Boolean,
    accentColor: Color,
    onClicked: () -> Unit
) {
    // Dynamic styling based on submissions and selection
    val baseColor = when {
        isSubmitted && isCorrectIndex -> Color(0xFFE8F5E9)         // Right choice green
        isSubmitted && isSelected && !isCorrectIndex -> Color(0xFFFFEBEE) // Selected wrong choice red
        isSelected -> accentColor.copy(alpha = 0.12f)              // Just highlighted
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isSubmitted && isCorrectIndex -> Color(0xFF2E7D32)
        isSubmitted && isSelected && !isCorrectIndex -> Color(0xFFC62828)
        isSelected -> accentColor
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    }

    val contentColor = when {
        isSubmitted && isCorrectIndex -> Color(0xFF1B5E20)
        isSubmitted && isSelected && !isCorrectIndex -> Color(0xFFB71C1C)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSubmitted, onClick = onClicked),
        colors = CardDefaults.cardColors(containerColor = baseColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .heightIn(min = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // Text choice
            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected || (isSubmitted && isCorrectIndex)) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor
                ),
                textAlign = TextAlign.Right,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // State visual marker
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected || (isSubmitted && isCorrectIndex)) borderColor else Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = if (isSelected || (isSubmitted && isCorrectIndex)) borderColor else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSubmitted && isCorrectIndex) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "صح",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else if (isSubmitted && isSelected && !isCorrectIndex) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "خطأ",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = when (index) {
                            0 -> "أ"
                            1 -> "ب"
                            2 -> "ج"
                            else -> "د"
                        },
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ResultBannerBox(isCorrect: Boolean) {
    val containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val colorAccent = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
    val text = if (isCorrect) "إجابة صحيحة وممتازة! أحسنت 🌟" else "إجابة خاطئة. راجع الشق التفصيلي أدناه."

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(14.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = colorAccent,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Right,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = colorAccent,
            modifier = Modifier.size(24.dp)
        )
    }
}
