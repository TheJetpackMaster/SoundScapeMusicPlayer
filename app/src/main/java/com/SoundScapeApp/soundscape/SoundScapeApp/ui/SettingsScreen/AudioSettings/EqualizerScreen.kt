import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.Preset
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.eqBg
import com.SoundScapeApp.soundscape.ui.theme.radialColor1
import com.SoundScapeApp.soundscape.ui.theme.radialColor2
import com.SoundScapeApp.soundscape.ui.theme.radialColor3
import com.SoundScapeApp.soundscape.ui.theme.sliderColor1
import com.SoundScapeApp.soundscape.ui.theme.sliderColor2
import com.SoundScapeApp.soundscape.ui.theme.thumb
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    viewModel: AudioViewModel,
    navController: NavController
) {

    val context = LocalContext.current

    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val selectedPresetBrush = Brush.verticalGradient(listOf(sliderColor1, sliderColor2))

    val sliderValues by viewModel.equalizerBandLevels.collectAsState()
    val hzValues = listOf("60 Hz", "230 Hz", "910 Hz", "4 kHz", "14 kHz", "18 kHz", "36 kHz")

    val currentBassLevel by viewModel.currentBassLevel.collectAsState()
    val currentVirtualizerLevel by viewModel.currentVirtualizerLevel.collectAsState()
    val currentLoudnessValue by viewModel.currentLoudnessLevel.collectAsState()

    var bassValue by remember { mutableStateOf(currentBassLevel) }
    var virtualizerValue by remember { mutableStateOf(currentVirtualizerLevel) }
    var loudnessValue by remember { mutableStateOf(currentLoudnessValue) }


    val turnToCustom = remember{ mutableStateOf(false) }


    LaunchedEffect(bassValue) {
        viewModel.adjustBass(bassValue)
    }

    LaunchedEffect(virtualizerValue) {
        viewModel.adjustVirtualizer(virtualizerValue)
    }

    LaunchedEffect(loudnessValue) {
        viewModel.adjustLoudnessEnhancer(loudnessValue)
    }

    LaunchedEffect(turnToCustom.value){
        if(turnToCustom.value){
            viewModel.setCurrentPreset(Preset.CUSTOM)
        }
    }


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "",
                            tint = White90
                        )
                    }
                },
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(24.dp))
                        Text(text = "Equalizer", color = White90)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PRESETS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = White90.copy(.5f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(initial = Preset.entries.indexOf(selectedPreset))),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Preset.entries.forEach { preset ->
                    PresetButton(
                        text = preset.name,
                        color = selectedPresetBrush,
                        selected = selectedPreset == preset,
                        onClick = {
                            viewModel.setCurrentPreset(preset)
                            turnToCustom.value = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            Box(contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "0", color = sliderColor2)
                    HorizontalDivider(color = sliderColor2)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 8.dp, end = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    sliderValues.forEachIndexed { index, value ->
                        Log.d("val", value.toString())
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Log.d("show", value.toString())
                            Text(
                                text = ((value * 15).toInt()).toString(),
                                color = White90,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            CustomVerticalSlider(
                                value = value,
                                onValueChange = {
                                    if (selectedPreset == Preset.CUSTOM) {
                                        viewModel.setBandLevel(index, it)
                                    }
                                },
                                enabled = selectedPreset.name == "CUSTOM",
                                preset = selectedPreset,
                                turnToCustom = turnToCustom
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = hzValues[index],
                                color = sliderColor2,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.volume),
                    contentDescription = "",
                    tint = White50
                )
                Spacer(modifier = Modifier.width(8.dp))


                Slider(
                    modifier = Modifier.weight(.6f),
                    colors = SliderDefaults.colors(
                        inactiveTrackColor = White50,
                        activeTrackColor = Color.White,
                    ),
                    value = loudnessValue,
                    onValueChange = {
                        loudnessValue = it

                    },
                    onValueChangeFinished = {
                        Toast.makeText(
                            context,
                            "WARNING! Increasing loudness may make audio noisy.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    thumb = {
                        Box(
                            Modifier
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(White90)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(loudnessValue * 100).toInt()}%",
                    color = White50
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CustomCircularSlider(
                    value = bassValue,
                    onValueChange = { bassValue = it },
                    sliderSize = 140.dp,

                    )

                Spacer(modifier = Modifier.weight(1f))

                CustomCircularSlider(
                    value = virtualizerValue,
                    onValueChange = { virtualizerValue = it },
                    sliderSize = 140.dp,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bass Boost ${((bassValue * 100).toInt())}%",
                    color = sliderColor2
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Virtualizer ${((virtualizerValue * 100).toInt())}%",
                    color = sliderColor2
                )
            }
        }
    }
}


@Composable
fun PresetButton(
    text: String,
    color: Brush,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(CircleShape)
            .clickable { onClick() }
            .background(
                if (selected) color else Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Transparent
                    )
                ), CircleShape
            )
            .border(
                if (selected) .5.dp else 1.dp, if (selected) White90.copy(.3f) else White90,
                CircleShape
            )
            .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = text,
            color = White90,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun CustomVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    sliderHeight: Dp = 220.dp,
    sliderWidth: Dp = 36.dp,
    trackBgColor: Color = Color.White.copy(.9f),
    trackColor: Brush = Brush.verticalGradient(
        colors = listOf(
            sliderColor1,
            sliderColor2
        )
    ),
    thumbColor: Color = Color.White,
    thumbRadius: Dp = 10.dp,
    enabled: Boolean,
    preset: Preset,
    turnToCustom:MutableState<Boolean>
) {
    val sliderValue by remember { mutableStateOf(value) }
    val density = LocalDensity.current
    var sliderPosition by remember { mutableStateOf((sliderValue + 1) / 2) }
    var canvasHeight by remember { mutableStateOf(0f) }
    var canvasWidth by remember { mutableStateOf(0f) }
    val enabledTouch = remember { mutableStateOf(enabled) }

    LaunchedEffect(enabled) {
        enabledTouch.value = enabled
    }

    LaunchedEffect(preset) {
        sliderPosition = (value + 1) / 2
    }

    Box(
        modifier = modifier
            .height(sliderHeight)
            .width(sliderWidth)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (enabledTouch.value) {
                        val newValue = sliderPosition - dragAmount / canvasHeight
                        sliderPosition = newValue.coerceIn(0f, 1f)
                        onValueChange(sliderPosition * 2 - 1)
                        change.consume()
                    }else{
                        turnToCustom.value = true
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    if (enabledTouch.value) {
                        val newValue = 1f - (tapOffset.y / canvasHeight)
                        sliderPosition = newValue.coerceIn(0f, 1f)
                        onValueChange(sliderPosition * 2 - 1)
                    }else{
                        turnToCustom.value = true
                    }
                }
            }
            .background(Color.Transparent)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            canvasHeight = size.height
            canvasWidth = size.width

            // Draw the track background
            drawRoundRect(
                color = trackBgColor,
                size = Size(width = canvasWidth / 2.5f, height = canvasHeight),
                topLeft = Offset((canvasWidth - canvasWidth / 2) / 2, 0f),
                cornerRadius = CornerRadius(8.dp.toPx())
            )

            // Draw the progress track
            drawRoundRect(
                brush = trackColor,
                size = Size(width = canvasWidth / 2.5f, height = canvasHeight * sliderPosition),
                topLeft = Offset(
                    (canvasWidth - canvasWidth / 2) / 2,
                    canvasHeight * (1 - sliderPosition)
                ),
                cornerRadius = CornerRadius(8.dp.toPx())
            )

            // Draw the thumb
            val thumbCenterY = canvasHeight * (1 - sliderPosition)
            val thumbRadiusPx = with(density) { thumbRadius.toPx() }
            drawCircle(
                color = thumbColor,
                radius = thumbRadiusPx,
                center = Offset(canvasWidth / 2.25f, thumbCenterY)
            )


            // Draw lines on the thumb
            val lineStartX = (canvasWidth / 2.25f - thumbRadiusPx / 2)
            val lineEndX = (canvasWidth / 2.3f + thumbRadiusPx / 2)
            val lineSpacing = thumbRadiusPx / 3

            for (i in 1..3) {
                val lineY = thumbCenterY - thumbRadiusPx / 1.6f + i * lineSpacing
                drawLine(
                    color = sliderColor2,
                    start = Offset(lineStartX, lineY),
                    end = Offset(lineEndX, lineY),
                    strokeWidth = 1.3.dp.toPx()
                )
            }
        }
    }
}


@Composable
fun CustomCircularSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    sliderSize: Dp = 150.dp,
    trackColor: Color = sliderColor2,
    thumbColor: Color = thumb,
    trackWidth: Dp = 2.dp,
    thumbRadius: Dp = 6.dp,
) {
    var angle by remember { mutableStateOf(value * 360f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sliderSize)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
                    val touch = change.position
                    val dx = touch.x - center.x
                    val dy = touch.y - center.y
                    val newAngle = (atan2(dy, dx) * (180 / PI))
                        .toFloat()
                        .let {
                            if (it < 0) it + 360 else it
                        }

                    // Calculate the new value
                    val newValue = newAngle / 360f
                    // Coerce the value between 0 and 1
                    angle = (newValue * 360f).coerceIn(0f, 360f)
                    onValueChange(newValue.coerceIn(0f, 1f))
                    change.consume()
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 3.5f
            val center = Offset(size.width / 2, size.height / 2)
            val strokeWidthPx = trackWidth.toPx()
            val thumbRadiusPx = thumbRadius.toPx()
            val thumbInset = thumbRadiusPx * 2f // Adjust this value to move the thumb inside

            // Draw the radial gradient circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(radialColor3, radialColor2.copy(.83f), radialColor3.copy(.2f)),
                    center = center,
                    radius = canvasSize / 2f
                ),
                radius = canvasSize / 2f,
                center = center
            )

            // Draw the track
            drawCircle(
                color = trackColor,
                radius = radius - strokeWidthPx / 2,
                center = center,
                style = Stroke(
                    width = strokeWidthPx
                )
            )

            // Calculate the thumb position
            val angleRad = Math.toRadians(angle.toDouble())
            val thumbX = (center.x + (radius - thumbInset) * cos(angleRad)).toFloat()
            val thumbY = (center.y + (radius - thumbInset) * sin(angleRad)).toFloat()

            // Draw the thumb
            drawCircle(
                color = thumbColor,
                radius = thumbRadiusPx,
                center = Offset(thumbX, thumbY)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(thumbColor, radialColor2.copy(.1f)),
                    center = Offset(thumbX, thumbY),
                    radius = 6.dp.toPx()
                ),
                radius = 8.dp.toPx(),
                center = Offset(thumbX, thumbY)
            )
        }
    }
}

data class EqualizerPreset(val name: String, val bandLevels: List<Float>)

val presets = listOf(
    EqualizerPreset("Custom", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)),
    EqualizerPreset("Normal", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)),
    EqualizerPreset("Jazz", listOf(-0.2f, 0.1f, 0.3f, 0.5f, 0.2f, 0.1f, -0.2f)),
    EqualizerPreset("Pop", listOf(0.4f, 0.3f, 0.2f, 0f, -0.2f, -0.3f, -0.4f)),
    EqualizerPreset("Classic", listOf(-0.3f, -0.2f, 0f, 0.2f, 0.3f, 0.2f, -0.1f))
)


//@Composable
//fun CustomCircularSlider(
//    value: Float,
//    onValueChange: (Float) -> Unit,
//    modifier: Modifier = Modifier,
//    sliderSize: Dp = 150.dp,
//    trackColor: Color = sliderColor2,
//    thumbColor: Color = Color.Red,
//    trackWidth: Dp = 2.dp,
//    thumbRadius: Dp = 8.dp,
//    shadowColor: Color = sliderColor2.copy(alpha = 0.4f)
//) {
//    var angle by remember { mutableStateOf(value * 360f) }
//
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = modifier
//            .size(sliderSize)
//            .background(Color.Red)
//            .pointerInput(Unit) {
//                detectDragGestures { change, _ ->
//                    val center = Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
//                    val touch = change.position
//                    val dx = touch.x - center.x
//                    val dy = touch.y - center.y
//                    val newAngle = (atan2(dy, dx) * (180 / PI)).toFloat().let {
//                        if (it < 0) it + 360 else it
//                    }
//
//                    // Calculate the new value
//                    val newValue = newAngle / 360f
//                    // Coerce the value between 0 and 1
//                    angle = (newValue * 360f).coerceIn(0f, 360f)
//                    onValueChange(newValue.coerceIn(0f, 1f))
//                    change.consume()
//                }
//            }
//
//
//    ) {
//        Canvas(modifier = Modifier.fillMaxSize()){
//            val canvasSize = size.minDimension
//            val radius = canvasSize / 4f
//            val center = Offset(radius, radius)
//            val strokeWidthPx = trackWidth.toPx()
//            val thumbRadiusPx = thumbRadius.toPx()
//            val thumbInset = thumbRadiusPx * 1.5f // Adjust this value to move the thumb inside
//
//
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(radialColor3, radialColor2.copy(.6f), radialColor3.copy(.4f)),
//                    center = center,
//                    radius = canvasSize/2.2f
//                ),
//                radius = canvasSize/2.2f,
//                center = center
//            )
//            // Draw the track
//            drawCircle(
//                color = trackColor,
//                radius = radius - strokeWidthPx / 2,
//                center = center,
//                style = androidx.compose.ui.graphics.drawscope.Stroke(
//                    width = strokeWidthPx
//                )
//            )
//
//            // Calculate the thumb position
//            val angleRad = Math.toRadians(angle.toDouble())
//            val thumbX = (center.x + (radius - thumbInset) * cos(angleRad)).toFloat()
//            val thumbY = (center.y + (radius - thumbInset) * sin(angleRad)).toFloat()
//
//            // Draw the thumb
//            drawCircle(
//                color = thumbColor,
//                radius = thumbRadiusPx,
//                center = Offset(thumbX, thumbY)
//            )
//        }
//    }
//}

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun VerticalSlider(
//    value: Float,
//    onValueChange: (Float) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//    ) {
//        Slider(
//            value = value,
//            onValueChange = onValueChange,
//            valueRange = -1f..1f,
//            colors = SliderDefaults.colors(
//                inactiveTrackColor = White90,
//                activeTrackColor = SoundScapeThemes.colorScheme.secondary
//            ),
//            thumb = {
//               Box(
//                   modifier = Modifier
//                       .size(20.dp)
//                       .clip(CircleShape)
//                       .background(SoundScapeThemes.colorScheme.primary))
//            },
//            modifier = Modifier
//                .graphicsLayer {
//                    rotationZ = 270f
//                    transformOrigin = TransformOrigin(0f, 0f)
//                }
//                .layout { measurable, constraints ->
//                    val placeable = measurable.measure(
//                        Constraints(
//                            minWidth = constraints.minHeight,
//                            maxWidth = constraints.maxHeight,
//                            minHeight = constraints.minWidth,
//                            maxHeight = constraints.maxHeight,
//                        )
//                    )
//                    layout(placeable.height, placeable.width) {
//                        placeable.place(-placeable.width, 0)
//                    }
//                }
//                .width(240.dp)
//                .height(120.dp)
//                .scale(scaleX = 1f, scaleY = 1f)
//        )
//    }
//}

