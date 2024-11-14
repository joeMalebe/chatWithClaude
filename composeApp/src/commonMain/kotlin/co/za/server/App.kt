package co.za.server

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import network.models.ApiResponseDto
import org.jetbrains.compose.ui.tooling.preview.Preview

const val numberOfDots = 4
val dotSize = 10.dp
val dotColor: Color = Color.Blue
const val delayUnit = 200
const val duration = numberOfDots * delayUnit
val spaceBetween = 2.dp

@Composable
@Preview
fun App(windowSizeClass: WindowSizeClass) {

    MaterialTheme {
        var isLoading by remember { mutableStateOf(false) }
        var showContent by remember { mutableStateOf(false) }
        var question by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val questionsAndAnswers: List<Triple<String, ApiResponseDto?, Boolean>> =
            remember { mutableStateListOf() }
        val questionsAndAnswers2 = remember {
            mutableStateListOf(
                QuestionAndAnswer("What is your name?", "My name is Claude", false),
                QuestionAndAnswer("What is your age?", "I am 3 years old", false),
                QuestionAndAnswer("What is your favorite color?", "I like all colors", false)
            )
        }
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        var answer: ApiResponseDto? by remember { mutableStateOf(null) }
        val alignment = when {

            windowSizeClass.widthSizeClass > WindowWidthSizeClass.Expanded -> {
                0.5f
            }

            windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact -> {
                0.8f
            }

            else -> {
                1f
            }

        }

        val sideButton = if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact) {
            true
        } else {
            false

        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(alignment).padding(horizontal = 16.dp)
                    .weight(0.8f, false)
            ) {

                //Text(text = "My answer to you", style = MaterialTheme.typography.h4)
                item {
                    if (questionsAndAnswers.isEmpty()) {
                        Text(
                            "Ask me something...", style = MaterialTheme.typography.h4
                        )
                    } else {
                        Text(
                            "Current conversation", style = MaterialTheme.typography.h4
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                }

                items(questionsAndAnswers) { qa ->
                    Column(verticalArrangement = spacedBy(8.dp)) {
                        Card(
                            modifier = Modifier.background(color = MaterialTheme.colors.primary)
                                .fillMaxWidth().padding(24.dp).align(Alignment.End),
                            elevation = 5.dp
                        ) {
                            Text("Question: ${qa.first}")
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                                .align(Alignment.CenterHorizontally), elevation = 5.dp
                        ) {
                            Text(
                                "Response: ${
                                    qa.second?.choices?.map { it.message.content }
                                        ?.fold("Response: ") { acc, messageDto -> "$acc $messageDto" }
                                }"
                            )
                        }
                    }
                }


                items(questionsAndAnswers2) { qa ->
                    Column(
                        verticalArrangement = spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "You ",
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.align(Alignment.End),
                            color = MaterialTheme.colors.secondaryVariant
                        )
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = Color(209, 245, 224),
                            contentColor = MaterialTheme.colors.onSecondary,
                            modifier = Modifier.align(Alignment.End),
                            elevation = 0.2.dp
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = qa.question,
                                style = MaterialTheme.typography.body1
                            )
                        }

                        if (qa.isTyping) {
                            DotsTyping()
                        } else {
                            Text("Claude-3-haiku ", style = MaterialTheme.typography.caption)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = Color(240, 240, 240),
                                contentColor = MaterialTheme.colors.onSecondary,
                                modifier = Modifier.align(Alignment.Start),
                                elevation = 0.2.dp
                            ) {
                                Text(
                                    modifier = Modifier.padding(16.dp), text =
                                    qa.answer.fold("") { acc, messageDto -> "$acc $messageDto" },
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }

            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(alignment).padding(horizontal = 16.dp)
            )
            {


                if (sideButton) {
                    FooterHorizontal(
                        question,
                        keyboardController,
                        questionsAndAnswers2,
                        scope,
                        listState,
                        isLoading,
                        { question = it }
                    ) { isLoading = it }
                } else {
                    Footer(
                        question,
                        keyboardController,
                        questionsAndAnswers2,
                        scope,
                        listState,
                        isLoading,
                        { question = it }
                    ) { isLoading = it }
                }


            }

        }


    }

}

@Composable
private fun Footer(
    question: String,
    keyboardController: SoftwareKeyboardController?,
    questionsAndAnswers2: SnapshotStateList<QuestionAndAnswer>,
    scope: CoroutineScope,
    listState: LazyListState,
    isLoading: Boolean,
    onQuestionChanged: (String) -> Unit,
    onLoadingChanged: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
    ) {
        OutlinedTextField(
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondaryVariant,
                backgroundColor = Color(240, 240, 240),
                focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                focusedLabelColor = MaterialTheme.colors.secondaryVariant
            ),
            modifier = Modifier.fillMaxWidth(),
            value = question,
            onValueChange = onQuestionChanged,
            label = { Text("Whats your question?") },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                handleMessage(
                    questionsAndAnswers2,
                    question,
                    scope,
                    listState,
                    onQuestionChanged,
                    onLoadingChanged
                )
            })
        )
        Button(
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant,
                contentColor = MaterialTheme.colors.surface
            ), modifier = Modifier.fillMaxWidth(0.75f), onClick = {
                keyboardController?.hide()
                handleMessage(
                    questionsAndAnswers2,
                    question,
                    scope,
                    listState,
                    onQuestionChanged,
                    onLoadingChanged
                )
                /*CoroutineScope(Job()).launch {
                            isLoading = true
                            *//*answer = client.post("/") {
                                setBody(
                                    ApiRequestDto(
                                        model = "claude-3-haiku-20240307",
                                        messages = listOf(
                                            MessageDto(
                                                content = question,
                                                role = "user"
                                            )
                                        )
                                    )
                                )
                            }.body()*//*
                            //questionsAndAnswers.add(Pair(question, answer))
                            delay(3000)
                            questionsAndAnswers2[questionsAndAnswers2.size - 1] = QuestionAndAnswer(question, "New answer for $question",false)
                            question = ""
                            isLoading = false
                        }*/
            }) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colors.surface
                )
            } else {
                Text("Confirm", style = MaterialTheme.typography.subtitle1)
            }
        }
    }
}

@Composable
private fun FooterHorizontal(
    question: String,
    keyboardController: SoftwareKeyboardController?,
    questionsAndAnswers2: SnapshotStateList<QuestionAndAnswer>,
    scope: CoroutineScope,
    listState: LazyListState,
    isLoading: Boolean,
    onQuestionChanged: (String) -> Unit,
    onLoadingChanged: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        OutlinedTextField(
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondaryVariant,
                backgroundColor = Color(240, 240, 240),
                focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                focusedLabelColor = MaterialTheme.colors.secondaryVariant
            ),
            modifier = Modifier.fillMaxWidth().weight(0.8f),
            value = question,
            onValueChange = onQuestionChanged,
            label = { Text("Whats your question?") },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                handleMessage(
                    questionsAndAnswers2,
                    question,
                    scope,
                    listState,
                    onQuestionChanged,
                    onLoadingChanged
                )
            })
        )

        Box(Modifier.weight(0.2f)) {
            Button(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant,
                    contentColor = MaterialTheme.colors.surface
                ), modifier = Modifier.size(60.dp).align(Alignment.Center), onClick = {
                    keyboardController?.hide()
                    handleMessage(
                        questionsAndAnswers2,
                        question,
                        scope,
                        listState,
                        onQuestionChanged,
                        onLoadingChanged
                    )
                    /*CoroutineScope(Job()).launch {
                                isLoading = true
                                *//*answer = client.post("/") {
                                 setBody(
                                     ApiRequestDto(
                                         model = "claude-3-haiku-20240307",
                                         messages = listOf(
                                             MessageDto(
                                                 content = question,
                                                 role = "user"
                                             )
                                         )
                                     )
                                 )
                             }.body()*//*
                             //questionsAndAnswers.add(Pair(question, answer))
                             delay(3000)
                             questionsAndAnswers2[questionsAndAnswers2.size - ] = QuestionAndAnswer(question, "New answer for $question",false)
                             question = ""
                             isLoading = false
                         }*/
                }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colors.surface
                    )
                } else {
                    Icon(Icons.Default.Done, contentDescription = "content description")
                }
            }
        }
    }
}

private fun handleMessage(
    questionsAndAnswers2: SnapshotStateList<QuestionAndAnswer>,
    question: String,
    scope: CoroutineScope,
    listState: LazyListState,
    onQuestionChange: (String) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
) {
    questionsAndAnswers2.add(QuestionAndAnswer(question, "", true))
    onQuestionChange("")
    scope.launch {
        listState.animateScrollToItem(
            questionsAndAnswers2.size,
            scrollOffset = questionsAndAnswers2.size - 1
        )
        delay(1000)
    }
    CoroutineScope(Job()).launch {
        onLoadingChange(true)


        /*answer = client.post("/") {
                                    setBody(
                                        ApiRequestDto(
                                            model = "claude-3-haiku-20240307",
                                            messages = listOf(
                                                MessageDto(
                                                    content = question,
                                                    role = "user"
                                                )
                                            )
                                        )
                                    )
                                }.body()*/
        //questionsAndAnswers.add(Pair(question, answer))


        delay(2000)
        questionsAndAnswers2[questionsAndAnswers2.size - 1] = questionsAndAnswers2.last().copy(
            answer = "" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question" +
                    "lorem fefd asdfd asdff eeeew sdfdfd sdfdfd sdf New answer for $question",
            isTyping = false
        )
        scope.launch {

            listState.animateScrollToItem(
                questionsAndAnswers2.size,
                scrollOffset = questionsAndAnswers2.size
            )
            delay(1000)
        }

        onLoadingChange(false)
    }
}

@Composable
fun DotsTyping() {
    val maxOffset = (numberOfDots * 2).toFloat()

    @Composable
    fun Dot(offset: Float) {
        Spacer(
            Modifier
                .size(dotSize)
                .offset(y = -offset.dp)
                .background(
                    color = MaterialTheme.colors.secondaryVariant,
                    shape = CircleShape
                )
        )
    }

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(animation = keyframes {
            durationMillis = duration
            0f at delay with LinearEasing
            maxOffset at delay + delayUnit with LinearEasing
            0f at delay + (duration / 2)
        })
    )

    val offsets = arrayListOf<State<Float>>()
    for (i in 0 until numberOfDots) {
        offsets.add(animateOffsetWithDelay(delay = i * delayUnit))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        offsets.forEach {
            Dot(it.value)
            Spacer(Modifier.width(spaceBetween))
        }
    }
}

data class QuestionAndAnswer(
    val question: String,
    val answer: String,
    val isTyping: Boolean = false
)