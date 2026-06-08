package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.CourseEntity
import com.example.data.database.QuestionEntity
import com.example.ui.viewmodel.TemyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: TemyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val courses by viewModel.allCourses.collectAsState()
    val questions by viewModel.allQuestions.collectAsState()

    var activeTab by remember { mutableStateOf("COURSES") } // "COURSES" or "QUESTIONS"

    // Dialog state for adding a Course
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var courseCode by remember { mutableStateOf("") }
    var courseTitleAr by remember { mutableStateOf("") }
    var courseTitleEn by remember { mutableStateOf("") }
    var courseLevel by remember { mutableStateOf("الفرقة الأولى") }
    var courseTerm by remember { mutableStateOf("الترم الثاني") }
    var courseColorHex by remember { mutableStateOf("#1565C0") }
    var courseType by remember { mutableStateOf("شرح مرئي متحرك") }

    // Dialog state for adding a Question
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var selectedQuestionCourseCode by remember { mutableStateOf("") }
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableStateOf(0) }
    var explanationText by remember { mutableStateOf("") }
    var explanationVariant by remember { mutableStateOf("MATH") } // MATH, SCHEMA, DIAGRAM, FLOW
    var explanationStepsTemp by remember { mutableStateOf("") } // Pipe-separated

    // Filter for questions list
    var filterCourseCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- ADMIN INTRO CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "لوحة تحكم المدير الأكاديمي",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "بصفتك المدير، يمكنك إضافة مواد جديدة، وصياغة بنوك الأسئلة، وتحديد الإجابات والشروح المرئية التفاعلية. ستنعكس تعديلاتك فورياً في رابط منصة الطلاب.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // --- TAB SWITCHER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { activeTab = "QUESTIONS" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "QUESTIONS") MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (activeTab == "QUESTIONS") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدارة الأسئلة (${questions.size})", fontSize = 12.sp)
                }
            }

            Button(
                onClick = { activeTab = "COURSES" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "COURSES") MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (activeTab == "COURSES") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدارة المواد (${courses.size})", fontSize = 12.sp)
                }
            }
        }

        // --- ACTION CONTAINER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (activeTab == "COURSES") {
                Button(
                    onClick = { showAddCourseDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة مادة")
                }
                Text("المواد الجامعية المتوفرة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            } else {
                Button(
                    onClick = {
                        if (courses.isEmpty()) {
                            Toast.makeText(context, "يرجى إضافة مادة أولاً لتتمكن من إضافة أسئلة لها", Toast.LENGTH_SHORT).show()
                        } else {
                            selectedQuestionCourseCode = courses.first().code
                            showAddQuestionDialog = true
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة سؤال")
                }
                Text("أسئلة بنوك المعرفة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }

        // --- FILTER FOR QUESTIONS ---
        if (activeTab == "QUESTIONS" && courses.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("تصفية بحسب المادة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(12.dp))
                ScrollableTabRow(
                    selectedTabIndex = if (filterCourseCode.isEmpty()) 0 else (courses.indexOfFirst { it.code == filterCourseCode } + 1),
                    edgePadding = 0.dp,
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = filterCourseCode.isEmpty(),
                        onClick = { filterCourseCode = "" },
                        text = { Text("الكل", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    courses.forEach { course ->
                        Tab(
                            selected = filterCourseCode == course.code,
                            onClick = { filterCourseCode = course.code },
                            text = { Text(course.code, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }

        // --- LIST VIEW WITH REAL DATA ---
        Box(modifier = Modifier.weight(1f)) {
            if (activeTab == "COURSES") {
                if (courses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("قائمة المواد فارغة حالياً. قم بإضافة أول مادة!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(courses) { course ->
                            AdminCourseCard(course = course, onDelete = {
                                viewModel.deleteCourse(course.code)
                                Toast.makeText(context, "تم حذف مادة ${course.titleAr} بالكامل والأسئلة المرتبطة بها.", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }
            } else {
                val filteredQuestions = if (filterCourseCode.isEmpty()) questions else questions.filter { it.courseCode == filterCourseCode }
                if (filteredQuestions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا يوجد أسئلة تطابق الفلتر الحالي.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredQuestions) { question ->
                            AdminQuestionCard(question = question, onDelete = {
                                viewModel.deleteQuestion(question.id, question.courseCode)
                                Toast.makeText(context, "تم حذف السؤال بنجاح.", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }
            }
        }
    }

    // --- ADD COURSE DIALOG ---
    if (showAddCourseDialog) {
        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("إضافة مادة جامعية جديدة", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("رمز المادة (مثال: ACC101)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseTitleAr,
                        onValueChange = { courseTitleAr = it },
                        label = { Text("اسم المادة باللغة العربية") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseTitleEn,
                        onValueChange = { courseTitleEn = it },
                        label = { Text("اسم المادة باللغة الإنجليزية") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseLevel,
                        onValueChange = { courseLevel = it },
                        label = { Text("المستوى أو الفرقة") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseTerm,
                        onValueChange = { courseTerm = it },
                        label = { Text("الترم الدراسي") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseType,
                        onValueChange = { courseType = it },
                        label = { Text("نوع الشرح (مثال: شرح مرئي هرمي)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = courseColorHex,
                        onValueChange = { courseColorHex = it },
                        label = { Text("كود لون السمة (مثال: #2E7D32)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (courseCode.isNotBlank() && courseTitleAr.isNotBlank()) {
                            viewModel.addCourse(
                                code = courseCode,
                                titleAr = courseTitleAr,
                                titleEn = courseTitleEn,
                                level = courseLevel,
                                term = courseTerm,
                                totalQuestions = 0,
                                type = courseType,
                                colorHex = courseColorHex
                            )
                            showAddCourseDialog = false
                            Toast.makeText(context, "تمت إضافة المادة بنجاح!", Toast.LENGTH_SHORT).show()
                            // Clear inputs
                            courseCode = ""
                            courseTitleAr = ""
                            courseTitleEn = ""
                        } else {
                            Toast.makeText(context, "يرجى ملء كود المادة والاسم بالكامل", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("حفظ المادة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCourseDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // --- ADD QUESTION DIALOG ---
    if (showAddQuestionDialog) {
        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = false },
            title = { Text("إنشاء وإضافة سؤال جديد للطلاب", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    item {
                        Text("اختر المادة المستهدفة:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            courses.forEach { c ->
                                FilterChip(
                                    selected = selectedQuestionCourseCode == c.code,
                                    onClick = { selectedQuestionCourseCode = c.code },
                                    label = { Text(c.code) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            label = { Text("نص السؤال المنهجي") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = optionA,
                            onValueChange = { optionA = it },
                            label = { Text("الخيار الأول (أ)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = optionB,
                            onValueChange = { optionB = it },
                            label = { Text("الخيار الثاني (ب)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = optionC,
                            onValueChange = { optionC = it },
                            label = { Text("الخيار الثالث (ج)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = optionD,
                            onValueChange = { optionD = it },
                            label = { Text("الخيار الرابع (د)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text("الخيار الصحيح المعتمد:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("أ", "ب", "ج", "د").forEachIndexed { index, value ->
                                FilterChip(
                                    selected = correctIndex == index,
                                    onClick = { correctIndex = index },
                                    label = { Text(value) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = explanationText,
                            onValueChange = { explanationText = it },
                            label = { Text("الشرح الأكاديمي التفصيلي للأستاذ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("نوع المخطط المرئي التفاعلي لشرح الإجابة:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("MATH", "SCHEMA", "DIAGRAM", "FLOW").forEach { tag ->
                                FilterChip(
                                    selected = explanationVariant == tag,
                                    onClick = { explanationVariant = tag },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = explanationStepsTemp,
                            onValueChange = { explanationStepsTemp = it },
                            label = { Text("خطوات الشرح المرئي (افصل بين الخطوة والأخرى بـ | )") },
                            placeholder = { Text("مثال: الخطوة 1|الخطوة 2|الخطوة 3") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionText.isNotBlank() && optionA.isNotBlank() && optionB.isNotBlank() && explanationText.isNotBlank()) {
                            viewModel.addQuestion(
                                courseCode = selectedQuestionCourseCode,
                                text = questionText,
                                optionA = optionA,
                                optionB = optionB,
                                optionC = optionC,
                                optionD = optionD,
                                correctIndex = correctIndex,
                                explanation = explanationText,
                                explanationVariant = explanationVariant,
                                explanationSteps = explanationStepsTemp
                            )
                            showAddQuestionDialog = false
                            Toast.makeText(context, "تم إنشاء وإضافة السؤال بنجاح للطلاب!", Toast.LENGTH_SHORT).show()
                            // Clear inputs
                            questionText = ""
                            optionA = ""
                            optionB = ""
                            optionC = ""
                            optionD = ""
                            explanationText = ""
                            explanationStepsTemp = ""
                        } else {
                            Toast.makeText(context, "يرجى ملء نص السؤال، والخيار (أ)، (ب) والشرح الأكاديمي", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("حفظ ونشر السؤال")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQuestionDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun AdminCourseCard(
    course: CourseEntity,
    onDelete: () -> Unit
) {
    val themeColor = try {
        Color(android.graphics.Color.parseColor(course.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف المادة",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = course.titleAr,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "${course.code} • ${course.titleEn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${course.level} | ${course.term}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Box(
                    modifier = Modifier
                        .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "سمة اللون: ${course.colorHex}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = themeColor
                    )
                }
            }
        }
    }
}

@Composable
fun AdminQuestionCard(
    question: QuestionEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف السؤال",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "رمز المادة: ${question.courseCode}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.text,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "💡 الشرح الأكاديمي المكتوب:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مخطط التفاعل: ${question.explanationVariant}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "رقم الإجابة الصحيحة: ${when(question.correctIndex) { 0 -> "أ"; 1 -> "ب"; 2 -> "ج"; else -> "د" }}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
