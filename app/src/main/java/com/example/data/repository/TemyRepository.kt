package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TemyRepository(private val temyDao: TemyDao) {

    // --- Dynamic Courses ---
    val allCourses: Flow<List<CourseEntity>> = temyDao.getAllCourses()

    suspend fun insertCourse(course: CourseEntity) {
        temyDao.insertCourse(course)
    }

    suspend fun deleteCourse(courseCode: String) {
        temyDao.deleteCourseByCode(courseCode)
        temyDao.deleteQuestionsByCourse(courseCode)
        temyDao.clearSolvedQuestionsForCourse(courseCode)
        temyDao.deleteCourseProgress(courseCode)
    }

    // --- Dynamic Questions ---
    val allQuestions: Flow<List<QuestionEntity>> = temyDao.getAllQuestions()

    fun getQuestionsForCourse(courseCode: String): Flow<List<QuestionEntity>> {
        return temyDao.getQuestionsForCourse(courseCode)
    }

    suspend fun insertQuestion(question: QuestionEntity) {
        temyDao.insertQuestion(question)
    }

    suspend fun deleteQuestion(id: Int) {
        temyDao.deleteQuestionById(id)
    }

    // --- Solved Questions ---
    val allSolvedQuestions: Flow<List<SolvedQuestionEntity>> = temyDao.getAllSolvedQuestions()

    fun getSolvedQuestionsForCourse(courseCode: String): Flow<List<SolvedQuestionEntity>> {
        return temyDao.getSolvedQuestionsForCourse(courseCode)
    }

    suspend fun saveSolvedQuestion(
        courseCode: String,
        questionId: Int,
        selectedOption: Int,
        isCorrect: Boolean,
        totalQuestionsInCourse: Int
    ) {
        // Save solved entry
        temyDao.insertSolvedQuestion(
            SolvedQuestionEntity(
                courseCode = courseCode,
                questionId = questionId,
                selectedOption = selectedOption,
                isCorrect = isCorrect
            )
        )

        // Update overall course progress
        val solvedQuestionsFlow = temyDao.getSolvedQuestionsForCourse(courseCode)
        val solvedList = solvedQuestionsFlow.firstOrNull() ?: emptyList()

        // Distinguish unique question ids solved
        val uniqueSolved = (solvedList.map { it.questionId } + questionId).distinct().size
        val correctList = (solvedList.filter { it.isCorrect }.map { it.questionId } + if (isCorrect) listOf(questionId) else emptyList()).distinct().size

        val updatedProgress = CourseProgressEntity(
            courseCode = courseCode,
            totalQuestions = totalQuestionsInCourse,
            solvedCount = uniqueSolved,
            correctCount = correctList,
            lastAccessed = System.currentTimeMillis()
        )
        temyDao.insertCourseProgress(updatedProgress)
    }

    suspend fun clearHistory() {
        temyDao.clearSolvedQuestions()
    }

    // --- Bookmarks ---
    val allBookmarks: Flow<List<BookmarkedQuestionEntity>> = temyDao.getAllBookmarks()

    fun isBookmarked(questionId: Int): Flow<Boolean> {
        return temyDao.isQuestionBookmarked(questionId)
    }

    suspend fun toggleBookmark(
        questionId: Int,
        courseCode: String,
        courseName: String,
        questionText: String,
        options: List<String>,
        correctOptionIndex: Int,
        explanation: String,
        explanationType: String,
        explanationSteps: List<String>
    ) {
        val currentlyBookmarked = temyDao.isQuestionBookmarked(questionId).firstOrNull() ?: false
        if (currentlyBookmarked) {
            temyDao.deleteBookmarkById(questionId)
        } else {
            temyDao.insertBookmark(
                BookmarkedQuestionEntity(
                    id = questionId,
                    courseCode = courseCode,
                    courseName = courseName,
                    questionText = questionText,
                    optionA = options.getOrElse(0) { "" },
                    optionB = options.getOrElse(1) { "" },
                    optionC = options.getOrElse(2) { "" },
                    optionD = options.getOrElse(3) { "" },
                    correctOptionIndex = correctOptionIndex,
                    explanation = explanation,
                    explanationType = explanationType,
                    explanationSteps = explanationSteps.joinToString("|"),
                    bookmarkedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun removeBookmark(questionId: Int) {
        temyDao.deleteBookmarkById(questionId)
    }

    // --- Course Progress ---
    val allCourseProgress: Flow<List<CourseProgressEntity>> = temyDao.getCourseProgressList()

    // --- Lecture Notes (Library) ---
    val allLectureNotes: Flow<List<LectureNoteEntity>> = temyDao.getAllLectureNotes()

    suspend fun insertLectureNote(note: LectureNoteEntity) {
        temyDao.insertLectureNote(note)
    }

    suspend fun deleteLectureNote(id: Int) {
        temyDao.deleteLectureNoteById(id)
    }

    // --- Seed Demo Data (Courses, Questions, Notes) ---
    suspend fun checkAndSeedData() {
        val existingCourses = temyDao.getAllCourses().firstOrNull() ?: emptyList()
        if (existingCourses.isEmpty()) {
            val defaultCourses = listOf(
                CourseEntity(
                    code = "ACC101",
                    titleAr = "مبادئ المحاسبة والتقارير المالية",
                    titleEn = "Principles of Accounting",
                    level = "الفرقة الأولى",
                    term = "الترم الثاني",
                    totalQuestions = 3,
                    type = "شرح مرئي وحسابي متحرك",
                    isReady = true,
                    colorHex = "#2E7D32"
                ),
                CourseEntity(
                    code = "MIS101",
                    titleAr = "نظم المعلومات الإدارية",
                    titleEn = "Management Information Systems",
                    level = "الفرقة الأولى",
                    term = "الترم الثاني",
                    totalQuestions = 2,
                    type = "شرح مرئي ومخططات هندسية",
                    isReady = true,
                    colorHex = "#1565C0"
                ),
                CourseEntity(
                    code = "OB101",
                    titleAr = "السلوك التنظيمي وإدارة الأفراد",
                    titleEn = "Organizational Behavior",
                    level = "الفرقة الأولى",
                    term = "الترم الثاني",
                    totalQuestions = 1,
                    type = "شرح مرئي وهرمي متفاعل",
                    isReady = true,
                    colorHex = "#D84315"
                ),
                CourseEntity(
                    code = "PA101",
                    titleAr = "الإدارة العامة وتطبيقاتها",
                    titleEn = "Public Administration",
                    level = "الفرقة الأولى",
                    term = "الترم الثاني",
                    totalQuestions = 1,
                    type = "شرح تفاعلي ومصفوفة SWOT",
                    isReady = true,
                    colorHex = "#6A1B9A"
                )
            )
            for (c in defaultCourses) {
                temyDao.insertCourse(c)
            }

            // Seed questions
            val defaultQuestions = listOf(
                QuestionEntity(
                    courseCode = "ACC101",
                    text = "اشترت الشركة معدات بقيمة 50,000 جنيه، سددت منها 20,000 جنيه نقداً والباقي على الحساب. ما هو الأثر على المعادلة المحاسبية الأساسية؟",
                    optionA = "زيادة الأصول بمقدار 50,000 وزيادة الالتزامات بمقدار 50,000",
                    optionB = "زيادة الأصول بمقدار 30,000 وزيادة الالتزامات بمقدار 30,000",
                    optionC = "نقص الأصول بمقدار 20,000 وزيادة الالتزامات بمقدار 30,000",
                    optionD = "لا يطرأ تغيير على إجمالي الأصول أو الالتزامات",
                    correctIndex = 1,
                    explanation = "المعادلة المحاسبية الأساسية هي: الأصول = الالتزامات + حقوق الملكية.\n\nالمعدات (أصل) تزيد بـ +50,000. النقدية (أصل) تقل بـ -20,000. صافي التغير في الأصول = زيادة بـ +30,000.\nالموردون (التزامات) يزيد بـ +30,000. \nبالتالي، يتطابق طرفا المعادلة بزيادة قدرها 30,000 جنيه.",
                    explanationVariant = "MATH",
                    explanationSteps = "التحليل العيني للأصول: شراء معدات (+50,000) ونقص في النقدية (-20,000).|حساب صافي الأصول: +50,000 - 20,000 = +30,000 (زيادة الأصول).|التحليل العيني للالتزامات: شراء على الحساب يعني زيادة الدائنين والخصوم بمبلغ المتبقي: 50,000 - 20,000 = +30,000.|موازنة الطرفين: الأصول المضافة (+30,000) = الخصوم المضافة (+30,000). المعادلة متوازنة بـ 30,000!"
                ),
                QuestionEntity(
                    courseCode = "ACC101",
                    text = "بدأت منشأة أعمالها برأس مال قدره 100,000 جنيه تم إيداعه بالكامل بالبنك. ما هو القيد المحاسبي الصحيح لتسجيل هذه المعاملة؟",
                    optionA = "من حـ/ البنك (مدين) إلى حـ/ رأس المال (دائن)",
                    optionB = "من حـ/ رأس المال (مدين) إلى حـ/ البنك (دائن)",
                    optionC = "من حـ/ الصندوق (مدين) إلى حـ/ البنك (دائن)",
                    optionD = "من حـ/ حاصلي الأرباح (مدين) إلى حـ/ رأس المال (دائن)",
                    correctIndex = 0,
                    explanation = "وفقاً لنظرية القيد المزدوج وقواعد المديونية والدائنية: البنك أصل وطبيعته مدين وبما أنه زاد فيصبح مديناً بـ 100,000 جنيه. رأس المال حق من حقوق الملكية وطبيعته دائن وبما أنه زاد يصبح دليلاً على الدائنية.",
                    explanationVariant = "MATH",
                    explanationSteps = "طبيعة حساب البنك: أصل متداول يزداد في الجانب المدين (من حـ/).|طبيعة حساب رأس المال: حقوق ملكية تزداد في الجانب الدائن (إلى حـ/).|تطبيق العملية: البنك مدين بـ 100,000 ورأس المال دائن بـ 100,000.|المعادلة النهائية: من حـ/ البنك إلى حـ/ رأس المال."
                ),
                QuestionEntity(
                    courseCode = "ACC101",
                    text = "بلغ إجمالي أصول شركة في نهاية العام 180,000 جنيه، وحقوق الملكية 120,000 جنيه. كم يبلغ إجمالي الالتزامات (الخصوم) وفقاً للموازنة؟",
                    optionA = "300,000 جنيه",
                    optionB = "120,000 جنيه",
                    optionC = "60,000 جنيه",
                    optionD = "240,000 جنيه",
                    correctIndex = 2,
                    explanation = "باستخدام معادلة الميزانية: الأصول (180,000) = الالتزامات (س) + حقوق الملكية (120,000).\nس = 180,000 - 120,000 = 60,000 جنيه.",
                    explanationVariant = "MATH",
                    explanationSteps = "اكتب المعادلة المحاسبية الأساسية أولاً: الأصول = الالتزامات + حقوق الملكية.|عوض بالقيم المعلومة: 180,000 = الالتزامات + 120,000.|انقل الأرقام لحساب المجهول: الالتزامات = 180,000 - 120,000.|النتيجة: الالتزامات = 60,000 جنيه وهي الخصوم المستحقة على المنشأة."
                ),
                QuestionEntity(
                    courseCode = "MIS101",
                    text = "أي مما يلي يصف بدقة السمة الرئيسية التي تميز نظم معالجة المعاملات اليومية (TPS) عن نظم دعم القرار (DSS)؟",
                    optionA = "تقوم TPS بدعم متخذي القرار من الإدارة العليا لإعطاء رؤى طويلة المدى للاستثمار والنمو الأكاديمي والمهني.",
                    optionB = "تقوم TPS بمعالجة المعاملات التشغيلية والروتينية المكررة فور حدوثها لتغذية بقية المستويات بالمعلومات الأساسية.",
                    optionC = "تعتمد TPS على نماذج تحليل إحصائية معقدة ومحاكاة تنبؤية للأسواق دون حفظ سجلات المعاملات اليومية.",
                    optionD = "تقوم TPS بتقديم توقعات للمخاطر وتدعم الإدارة الوسطى بالتقارير غير المهيكلة والتخطيط السنوي والتسويقي الفوري.",
                    correctIndex = 1,
                    explanation = "نظم معالجة المعاملات (TPS) تقع في المستوى التشغيلي وتعمل على جمع ومعالجة وتسجيل العمليات اليومية الروتينية كالخدمات والحسومات والمخزون، بينما نظم دعم القرار (DSS) تخدم المستويين الإداري والوسطى لتوفير أدوات نمذجة وتخطيط استراتيجي.",
                    explanationVariant = "SCHEMA",
                    explanationSteps = "موقع TPS: في قاعدة الهرم التشغيلي للجامعة أو الشركة لتسجيل الحركة الفورية.|طبيعة المدخلات والمخرجات: مدخلات روتينية دورية (فواتير، مبيعات، درجات طلاب) لتفرز تقارير تفصيلية فنية.|موقع DSS: في وسط الهرم الإداري بهدف تخطيط أوسع للموارد وتحليل السيناريوهات المحتملة.|التكامل: تغذي نظم TPS قواعد البيانات التي تستخدمها بعد ذلك نظم DSS للتحليل والدعم الاستراتيجي."
                ),
                QuestionEntity(
                    courseCode = "MIS101",
                    text = "ما هو المكون السحابي الرئيسي من نوع SaaS في معمارية الخدمة الثلاثية للأعمال؟",
                    optionA = "توفير آلات بصفائح افتراضية للتحكم بالبنية التحتية الصلبة وعقود تشغيل الخوادم والشبكات.",
                    optionB = "استئجار برمجيات تطبيقية كاملة ومستضافة سحابياً لتشغيل مباشرةً عبر المتصفح (مثل بريد Outlook التعليمي).",
                    optionC = "تقديم بيئات برمجية مخصصة للتحكم بالبنيات البرمجية والأدوات والأطر دون الوصول للتطبيق الفعلي للمستخدم.",
                    optionD = "تهيئة برمجيات إدارة الشبكات المحلية من نوع LAN لتغذية خوادم البيانات الداخلية للجامعات.",
                    correctIndex = 1,
                    explanation = "البرمجيات كخدمة (SaaS) تقدم تطبيقات برمجية كاملة عبر الويب، يتم الوصول إليها بواسطة المتصفحات مباشرةً دون الحاجة لتحميلها أو بنائها محلياً على أجهزة الموظفين والطلاب.",
                    explanationVariant = "SCHEMA",
                    explanationSteps = "نموذج الحوسبة السحابية: ينقسم لـ IaaS (البنية التحتية)، PaaS (المنصات للبرمجة)، و SaaS (البرمجيات للمستخدم).|موقع SaaS: يمثل قمة الهرم السحابي لأنه يمنح الطلاب والمستخدمين أنظمة تسليم جاهزة.|التطبيق العملي: دخول الطالب للمنصة وحلها فوراً عبر السحاب دون تكوين محركات قواعد بيانات.|الفائدة الفورية: تقليل التكاليف التشغيلية للمستشفيات والمنظمات الأكاديمية بنسبة تصل لـ 40%."
                ),
                QuestionEntity(
                    courseCode = "OB101",
                    text = "وفقاً لنظرية ماسلو للاحتياجات الإنسانية (Maslow's Hierarchy)، أي من الخيارات يمثل الاحتياج الذي يحفز عندما يعجز الشخص عن التقدير الاجتماعي ويقع في بداية التدرج لتأمين مستقبله الأكاديمي؟",
                    optionA = "الحاجة لتحقيق الذات والتطوير الفردي التام",
                    optionB = "الاحتياجات الفسيولوجية الأساسية فقط",
                    optionC = "احتياجات الأمان والحماية الوظيفية والبدنية",
                    optionD = "احتياجات الانتماء الاجتماعي وتكوين علاقات",
                    correctIndex = 2,
                    explanation = "عند سد الاحتياجات البدنية الفسيولوجية (الطعام، المأكل)، يطالب الفرد فوراً بـ 'احتياجات الأمان' (كمأمن مالي، أمان وظيفي، تأمين مستقبله وصحته) ليشعر بالاستقرار قبل المطالبة بالانتماء أو التقدير.",
                    explanationVariant = "DIAGRAM",
                    explanationSteps = "قاعدة الهرم: الاحتياجات الفسيولوجية (الطعام، الشراب، النوم) وهي أساس البقاء.|المستوى الثاني: الأمان الوظيفي والجسدي والمستقبلي لضمان تدفق دخل مستقر وحفظ الأرواح.|المستوى الثالث: العلاقات الاجتماعية والأسرية لضمان اندماج مجتمعي وتعليم غني بالأنشطة والمنافسات.|قمة الهرم: تقدير الذات وتطوير الشغف الفردي لتقديم أقصى ابتكارات ممكنة."
                ),
                QuestionEntity(
                    courseCode = "PA101",
                    text = "عند إعداد مصفوفة SWOT للتخطيط الاستراتيجي، تحت أي تصنيف تقع ميزة قيام منافس جديد مهدد بنصف أسعار خدماتنا وإطلاق تقنية تفوق سرعة استجابتنا؟",
                    optionA = "نقاط الضعف (Weaknesses) لأنها تؤثر على الميزانية",
                    optionB = "الفرص التنموية والتقنية (Opportunities) المتاحة محلياً",
                    optionC = "التهديدات الخارجية والبيئية (Threats) التي تواجهنا",
                    optionD = "نقاط القوة التنافسية (Strengths) التي يمكننا تنميتها",
                    correctIndex = 2,
                    explanation = "مصفوفة SWOT تقسم العوامل لداخلية وخارجية. العوامل الخارجية هما: الفرص (إيجابية) والتهديدات (سلبية). دخول منافس يهدد الأسعار ويدخل تقنيات متفوقة يمثل تهديداً واضحاً في البيئة الخارجية للمنظمة.",
                    explanationVariant = "FLOW",
                    explanationSteps = "فهم أبعاد SWOT: القوة والضعف (داخلية)، الفرص والتهديدات (خارجية).|تحليل طبيعة الحدث: المنافس ومسعره الجديد هو حدث بيئي خارجي خارج تماماً عن سيطرتنا المباشرة.|الأثر على الكيان: أثر سلبي قوي يؤدي لتقليل حصتنا في السوق الجامعية للمنتج التعليمي.|القرار: وضع هذا البند في ربع التهديدات (Threats) لبناء جدار استباقي دفاعي."
                )
            )
            for (q in defaultQuestions) {
                temyDao.insertQuestion(q)
            }
        }

        // Notes seeding
        val currentNotes = temyDao.getAllLectureNotes().firstOrNull() ?: emptyList()
        if (currentNotes.isEmpty()) {
            val demoNotes = listOf(
                LectureNoteEntity(
                    title = "ملخص المعادلة المحاسبية الأساسية والتسويات",
                    courseCode = "ACC101",
                    type = "SUMMARY",
                    content = "تركز هذه المادة على جوهر القيد المزدوج: الأصول = الالتزامات + حقوق الملكية. المبادئ الهامة تشمل الدورة الدفترية الكاملة: تحليل المعاملات، تسجيل قيد اليومية، الترحيل للأستاذ وإعداد ميزان المراجعة قبل التسويات الجردية والنهائية.",
                    description = "ملخص من 4 صفحات يغطي أساسيات التسوية الدورية والقيود."
                ),
                LectureNoteEntity(
                    title = "ملزمة المعماريات السحابية ونظم قواعد البيانات",
                    courseCode = "MIS101",
                    type = "HANDOUT",
                    content = "مراجعة شاملة لطبقات تكنولوجيا المعلومات الثلاث: البنية التحتية كمعدات، المنصات كتشغيل، والبرمجيات كتسليم (SaaS). تشتمل على معايير تمييز TPS و DSS والمستويات الإدارية.",
                    description = "ملزمة القسم العلمي - الشرح الأكاديمي الشامل للمصطلحات."
                ),
                LectureNoteEntity(
                    title = "ملاحظات الدوافع الإنسانية ومدرج ماسلو الهرمي",
                    courseCode = "OB101",
                    type = "NOTES",
                    content = "سلوك الفرد يتأثر بالحاجات غير المشبعة. الاحتياجات الفسيولوجية أولاً، تليها احتياجات الأمان الوظيفي وسلامة المسار، ثم الاندماج الاجتماعي والعلاقات، وأخيراً التقدير وتحقيق الرؤية الذاتية.",
                    description = "مذكرة تفصيلية لتسهيل الحفظ السريع مدمجة بالمخططات الهرمية."
                )
            )
            for (note in demoNotes) {
                temyDao.insertLectureNote(note)
            }
        }
    }
}
