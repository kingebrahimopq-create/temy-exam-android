package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun VisualExplanationContainer(
    variant: String,
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊 الشرح المرئي التفاعلي المتحرك",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 12.dp),
                textAlign = TextAlign.Right
            )

            // Render custom animations depending on key categorization
            when (variant) {
                "MATH" -> MathExplanationVisualizer()
                "SCHEMA" -> SchemaExplanationVisualizer()
                "DIAGRAM" -> DiagramExplanationVisualizer()
                "FLOW" -> FlowExplanationVisualizer()
                else -> {
                    // Fallback simpler layout
                    Column(modifier = Modifier.fillMaxWidth()) {
                        steps.forEachIndexed { i, step ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(text = "${i + 1}. ", fontWeight = FontWeight.Bold)
                                Text(text = step)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated step-by-step checklist
            InteractiveStepList(steps = steps)
        }
    }
}

@Composable
fun InteractiveStepList(steps: List<String>) {
    var visibleStepsCount by remember { mutableStateOf(0) }

    LaunchedEffect(steps) {
        visibleStepsCount = 0
        for (i in 1..steps.size) {
            delay(400)
            visibleStepsCount = i
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        steps.forEachIndexed { index, step ->
            AnimatedVisibility(
                visible = index < visibleStepsCount,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ------ SPECIAL ANIMATION VISUALIZERS ------

@Composable
fun MathExplanationVisualizer() {
    // ACCOUNTING EQUATION: Assets = Liabilities + Owners' Equity
    var showCalculations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showCalculations = true
    }

    val animatedBalanceOffset by animateFloatAsState(
        targetValue = if (showCalculations) 0f else -60f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "math_eq"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Assets Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .background(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        Color(0xFF2E7D32).copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "الأصول (Assets)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (showCalculations) "+30,000 ج.م" else "-- ج.م",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "معدات (+50k)\nنقدية (-20k)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "=",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Liabilities Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        Color(0xFFC62828).copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "الالتزامات",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (showCalculations) "+30,000 ج.م" else "-- ج.م",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = "دائنون (على الحساب)\n(+30k)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "+",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Equity Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .background(
                        color = Color(0xFFECEFF1),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "حقوق الملكية",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (showCalculations) "0.00 ج.م" else "-- ج.م",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(
                    text = "رأس المال الحالي\n(لم يتغير)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "⚖️ ميزان المعاملة متطابق ومتزن بالكامل!",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (showCalculations) Color(0xFF2E7D32) else Color.Transparent
            )
        )
    }
}

@Composable
fun SchemaExplanationVisualizer() {
    // MIS ARCHITECTURE: Client -> Cloud App (SaaS) -> SQL Database
    var activeNode by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            activeNode = (activeNode + 1) % 3
            delay(1200)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Node 1: Client device
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = if (activeNode == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (activeNode == 0) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = "جهاز الطالب",
                tint = if (activeNode == 0) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "جهاز العميل\n(Client User)",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        // Arrow flow 1
        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "تدفق",
                tint = if (activeNode == 0) MaterialTheme.colorScheme.primary else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }

        // Node 2: Web Server SaaS
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = if (activeNode == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (activeNode == 1) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudQueue,
                contentDescription = "السحابة",
                tint = if (activeNode == 1) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "سحابة SaaS\n(Temy Cloud)",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        // Arrow flow 2
        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "تدفق",
                tint = if (activeNode == 1) MaterialTheme.colorScheme.primary else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }

        // Node 3: SQL Database
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = if (activeNode == 2) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (activeNode == 2) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "قاعدة البيانات",
                tint = if (activeNode == 2) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "قاعدة البيانات\n(db/SQL Server)",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DiagramExplanationVisualizer() {
    // OB DICTIONARY: MASLOW NEEDS PYRAMID
    var activeTier by remember { mutableStateOf(2) } // Tier 2: Safety

    LaunchedEffect(Unit) {
        // Just let it oscillate slightly to show interactivity
        while (true) {
            delay(1500)
            activeTier = if (activeTier == 2) 3 else 2
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Draw custom triangle layers using Compose canvas to provide beautiful visual animations
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .weight(1f)
        ) {
            val width = size.width
            val height = size.height

            // We have 4 tiers in our simplified hierarchy:
            // Tier 4 (Top): Self-Actualization (تحقيق الذات)
            // Tier 3: Belonging (الاحتياجات الاجتماعية)
            // Tier 2: Safety (احتياجات الأمان) [Active Target]
            // Tier 1 (Base): Physiological (الاحتياجات الفسيولوجية)

            // Let's draw 4 trapezoids/triangles stacking up
            val tierGap = height / 4f

            // Tier 1 Base:
            val p1Y = height
            val p1Yt = height - tierGap
            val baseLeftX = 0f
            val baseRightX = width
            val top1LeftX = width * 0.125f
            val top1RightX = width * 0.875f

            val path1 = Path().apply {
                moveTo(baseLeftX, p1Y)
                lineTo(baseRightX, p1Y)
                lineTo(top1RightX, p1Yt)
                lineTo(top1LeftX, p1Yt)
                close()
            }
            drawPath(
                path = path1,
                color = if (activeTier == 0) Color(0xFFDD2C00) else Color(0xFFFF8A65).copy(alpha = 0.5f)
            )

            // Tier 2 Safety (Target):
            val p2Y = p1Yt
            val p2Yt = height - (tierGap * 2f)
            val top2LeftX = width * 0.25f
            val top2RightX = width * 0.75f

            val path2 = Path().apply {
                moveTo(top1LeftX, p2Y)
                lineTo(top1RightX, p2Y)
                lineTo(top2RightX, p2Yt)
                lineTo(top2LeftX, p2Yt)
                close()
            }
            drawPath(
                path = path2,
                color = if (activeTier == 2) Color(0xFFD84315) else Color(0xFFFFAB91)
            )

            // Tier 3 Belonging:
            val p3Y = p2Yt
            val p3Yt = height - (tierGap * 3f)
            val top3LeftX = width * 0.375f
            val top3RightX = width * 0.625f

            val path3 = Path().apply {
                moveTo(top2LeftX, p3Y)
                lineTo(top2RightX, p3Y)
                lineTo(top3RightX, p3Yt)
                lineTo(top3LeftX, p3Yt)
                close()
            }
            drawPath(
                path = path3,
                color = if (activeTier == 3) Color(0xFFFFB74D) else Color(0xFFFFE0B2).copy(alpha = 0.7f)
            )

            // Tier 4 Achievement Top:
            val p4Y = p3Yt
            val p4Yt = 0f
            val top4X = width * 0.5f

            val path4 = Path().apply {
                moveTo(top3LeftX, p4Y)
                lineTo(top3RightX, p4Y)
                lineTo(top4X, p4Yt)
                close()
            }
            drawPath(
                path = path4,
                color = Color(0xFFFFF176).copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFD84315), RoundedCornerShape(5.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "🔥 المستوى النشط: احتياجات الأمان والوقاية الوظيفية",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD84315)
            )
        }
    }
}

@Composable
fun FlowExplanationVisualizer() {
    // MGT SWOT MATRIX: 2x2 grid representing Strengths, Weaknesses, Opportunities, Threats
    var showSWOTPulse by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        showSWOTPulse = true
    }

    val pulseScale by animateFloatAsState(
        targetValue = if (showSWOTPulse) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            // S (Strengths)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(3.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF81C784), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("S", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 16.sp)
                    Text("قوة داخلية", fontSize = 9.sp, color = Color.DarkGray)
                }
            }

            // W (Weaknesses)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(3.dp)
                    .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("W", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17), fontSize = 16.sp)
                    Text("ضعف داخلي", fontSize = 9.sp, color = Color.DarkGray)
                }
            }
        }

        Row(modifier = Modifier.weight(1f)) {
            // O (Opportunities)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(3.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("O", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0), fontSize = 16.sp)
                    Text("فرص خارجية", fontSize = 9.sp, color = Color.DarkGray)
                }
            }

            // T (Threats) - Pulse this because the question relates to Threat from competitor!
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(3.dp)
                    .shadow(elevation = if (showSWOTPulse) 4.dp else 1.dp, shape = RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFFD32F2F), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("T", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 18.sp)
                    Text("تهديد خارجي ⚠️", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                }
            }
        }
    }
}

class BorderStroke(val width: androidx.compose.ui.unit.Dp, val color: Color) {
    // Simple custom fallback
}

@Composable
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color, shape: RoundedCornerShape): Modifier {
    return Modifier.border(width, color, shape)
}
