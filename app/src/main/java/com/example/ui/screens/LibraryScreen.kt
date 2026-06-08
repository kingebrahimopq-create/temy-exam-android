package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.database.LectureNoteEntity
import com.example.ui.viewmodel.TemyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: TemyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notes by viewModel.allLectureNotes.collectAsState()
    val courses by viewModel.allCourses.collectAsState()

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteCourseCode by remember { mutableStateOf("") }
    var noteType by remember { mutableStateOf("SUMMARY") } // SUMMARY, HANDOUT, NOTES
    var noteContent by remember { mutableStateOf("") }
    var noteDesc by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- LIBRARY BRAND BANNER ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (courses.isEmpty()) {
                                Toast.makeText(context, "الرجاء إضافة مادة أولاً لتنزيل ملخصاتها", Toast.LENGTH_SHORT).show()
                            } else {
                                noteCourseCode = courses.first().code
                                showAddNoteDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة ملخص", fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "مكتبة الملخصات والملازم 📖",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Right
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "بصفتك مديراً للمنصة، يمكنك توفير ملازم دراسية وملخصات هامة للعام الدراسي، مما يتيح للطلاب تنزيل وقراءة المحتوى الأكاديمي مباشرةً وبسهولة تامة.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // --- NOTES LIST ---
        if (notes.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "لا توجد ملفات أو ملخصات منشورة بالمكتبة حتى الآن.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(notes) { note ->
                    LibraryNoteRow(note = note, onDelete = {
                        viewModel.removeNote(note.id)
                        Toast.makeText(context, "تم إزالة الملف من المكتبة بنجاح.", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }

    // --- ADD NOTE DIALOG ---
    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("إدراج مادة تعليمية جديدة", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("عنوان الملخص / الملف") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("اختر المادة المرتبطة:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        courses.forEach { c ->
                            FilterChip(
                                selected = noteCourseCode == c.code,
                                onClick = { noteCourseCode = c.code },
                                label = { Text(c.code) }
                            )
                        }
                    }
                    Text("نوع المستند الأكاديمي:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("SUMMARY" to "ملخص مكثف", "HANDOUT" to "ملزمة القسم", "NOTES" to "ملاحظات").forEach { (type, label) ->
                            FilterChip(
                                selected = noteType == type,
                                onClick = { noteType = type },
                                label = { Text(label) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = noteDesc,
                        onValueChange = { noteDesc = it },
                        label = { Text("وصف مختصر للمستند (مثال: يغطي الفصل الاول)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("المحتوى الدراسي والنص الأكاديمي الكامل") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                            viewModel.addCustomNote(
                                title = noteTitle,
                                courseCode = noteCourseCode,
                                type = noteType,
                                content = noteContent,
                                description = noteDesc
                            )
                            showAddNoteDialog = false
                            Toast.makeText(context, "تم رفع وتعميم المذكرة الأكاديمية بنجاح!", Toast.LENGTH_SHORT).show()
                            // Reset inputs
                            noteTitle = ""
                            noteContent = ""
                            noteDesc = ""
                        } else {
                            Toast.makeText(context, "يرجى كتابة عنوان للمستند والمحتوى الأساسي", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("رفع الملخص")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun LibraryNoteRow(
    note: LectureNoteEntity,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "حذف ملف",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Right
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حجم الملف: ${note.fileSize} • ${note.courseCode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = when (note.type) {
                                    "SUMMARY" -> Color(0xFFE8F5E9)
                                    "HANDOUT" -> Color(0xFFEFF6FF)
                                    else -> Color(0xFFFFF4E5)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (note.type) {
                                "SUMMARY" -> Icons.Default.Article
                                "HANDOUT" -> Icons.Default.MenuBook
                                else -> Icons.Default.Assignment
                            },
                            contentDescription = null,
                            tint = when (note.type) {
                                "SUMMARY" -> Color(0xFF2E7D32)
                                "HANDOUT" -> Color(0xFF1D4ED8)
                                else -> Color(0xFFD84315)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = note.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isExpanded) "إغلاق الملف" else "دراسة وقراءة المحتوى التعليمي", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}
