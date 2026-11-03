package com.pokedex.pokemon_details.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.pokedex.compose_core.R as ComposeCoreR
import com.pokedex.compose_core.common.PokemonTypePresentationModel
import com.pokedex.compose_core.components.PokemonTypeChip
import com.pokedex.compose_core.di.daggerViewModel
import com.pokedex.compose_core.theme.AppTheme
import com.pokedex.compose_core.transition.pokemonSharedImage
import com.pokedex.core.domain.model.PokemonId
import com.pokedex.pokemon_details.R
import com.pokedex.pokemon_details.composables.StatBar
import com.pokedex.pokemon_details.route.PokemonDetailsRoute
import com.pokedex.pokemon_details.state.PokemonDetails
import com.pokedex.pokemon_details.state.PokemonDetailsUiState
import com.pokedex.pokemon_details.viewmodel.PokemonDetailsViewModel
import kotlin.math.roundToInt

@Composable
internal fun PokemonDetailsScreen(
    onBackClick: () -> Unit,
    route: PokemonDetailsRoute,
    viewModel: PokemonDetailsViewModel = daggerViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initialize(route)
    }

    PokemonDetailsScreenContent(
        uiStateMain = uiState,
        onBackClick = onBackClick,
        leftClick = viewModel::onPrevClick,
        rightClick = viewModel::onNextClick,
        initialId = route.id,
        initialImageUrl = route.imageUrl
    )
}

private enum class SwipeDirection { Next, Prev }

private const val COLOR_ANIMATION_MS = 300
private const val SWIPE_THRESHOLD_DIVIDER = 15f
private const val PLACEHOLDER_ALPHA = 0.6f
private const val PLACEHOLDER_DARKEN = 0.3f

/** Рисует [delegate], перекрашивая его в [tint] (placeholder-силуэт — одноцветный). */
private class TintedPainter(
    private val delegate: Painter,
    private val tint: Color,
) : Painter() {
    override val intrinsicSize: Size get() = delegate.intrinsicSize

    override fun DrawScope.onDraw() {
        with(delegate) {
            draw(size = size, alpha = 1f, colorFilter = ColorFilter.tint(tint))
        }
    }
}

@Composable
private fun PokemonTypePresentationModel.backgroundColor(): Color =
    if (this == PokemonTypePresentationModel.Unknown) {
        AppTheme.grayscaleColor().whireframe
    } else {
        color
    }

@Composable
private fun PokemonDetailsScreenContent(
    uiStateMain: PokemonDetailsUiState,
    leftClick: () -> Unit,
    rightClick: () -> Unit,
    onBackClick: () -> Unit,
    initialId: PokemonId? = null,
    initialImageUrl: String = "",
) {
    // null = смена id не от пользователя (первый заход, initialize, догрузка данных)
    var pendingSwipe by remember { mutableStateOf<SwipeDirection?>(null) }
    val onPrev = {
        if (uiStateMain.isPrevVisible) {
            pendingSwipe = SwipeDirection.Prev
            leftClick()
        }
    }
    val onNext = {
        if (uiStateMain.isNextVisible) {
            pendingSwipe = SwipeDirection.Next
            rightClick()
        }
    }
    val currentOnPrev by rememberUpdatedState(onPrev)
    val currentOnNext by rememberUpdatedState(onNext)
    LaunchedEffect(uiStateMain.currentPokemonById) { pendingSwipe = null }

    val details = uiStateMain.pokemonMap[uiStateMain.currentPokemonById] as? PokemonDetails.Loaded
    val containerColor by animateColorAsState(
        targetValue = details?.pokemonTypeMain?.backgroundColor() ?: Color.Transparent,
        animationSpec = tween(COLOR_ANIMATION_MS),
        label = "containerColor"
    )
    SharedTransitionLayout {
        AnimatedContent(
            modifier = Modifier
                .background(color = containerColor)
                .pointerInput(Unit) {
                    // порог свайпа — 1/15 ширины экрана
                    val threshold = size.width / SWIPE_THRESHOLD_DIVIDER
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onDragCancel = { totalDrag = 0f },
                        onDragEnd = {
                            when {
                                totalDrag <= -threshold -> currentOnNext()
                                totalDrag >= threshold -> currentOnPrev()
                            }
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount -> totalDrag += dragAmount }
                    )
                },
            targetState = uiStateMain.currentPokemonById,
            transitionSpec = {
                when (pendingSwipe) {
                    SwipeDirection.Next -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )

                    SwipeDirection.Prev -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )

                    null -> EnterTransition.None togetherWith ExitTransition.None
                } using SizeTransform(clip = false)
            }
        ) { id ->
            val uiState = (uiStateMain.pokemonMap[id] as? PokemonDetails.Loaded) ?: PokemonDetails.Loaded()
            val backgroundColor = uiState.pokemonTypeMain.backgroundColor()
            Scaffold(
                topBar = {
                    TopBar(
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState("top"),
                            animatedVisibilityScope = this@AnimatedContent
                        ),
                        pokemonName = uiState.pokemonName,
                        pokemonNumber = uiState.pokemonNumber,
                        onBackClick = onBackClick
                    )
                },
                contentColor = backgroundColor
            ) { contentPadding ->

                val paddingTopStatusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            backgroundColor
                        ).sharedElement(
                            sharedContentState = rememberSharedContentState("background"),
                            animatedVisibilityScope = this@AnimatedContent
                        ),
                    )
                    SubcomposeLayout(
                        modifier = Modifier
                    ) { constraints ->
                        val imagePokemon = subcompose("image") {
                            ImageWithScroll(
                                modifier = Modifier.sharedElement(
                                    sharedContentState = rememberSharedContentState("image"),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    zIndexInOverlay = 1000f
                                ),
                                imageModifier = Modifier.pokemonSharedImage(id),
                                imageUrl = uiState.pokemonImageUrl.ifBlank {
                                    if (id == initialId) initialImageUrl else ""
                                },
                                placeholderColor = backgroundColor,
                                leftClick = onPrev,
                                rightClick = onNext,
                                chevroneRightVisible = uiStateMain.isNextVisible,
                                chevroneLeftVisibility = uiStateMain.isPrevVisible
                            )
                        }.first().measure(constraints)

                        val contentWithTopBarPaddingTop = contentPadding.calculateTopPadding().roundToPx()

                        val yPositionContent = imagePokemon.measuredHeight * 0.78 + contentWithTopBarPaddingTop

                        val paddingTopForTypes = (imagePokemon.measuredHeight * 0.23f).toDp() + 12.dp

                        val maxWidthForPokeballIcon = (constraints.maxWidth * 0.57).roundToInt()

                        val icon = subcompose("pokeball") {
                            Icon(
                                modifier = Modifier
                                    .aspectRatio(
                                        ratio = 1f
                                    ).sharedElement(
                                        sharedContentState = rememberSharedContentState("pokeball"),
                                        animatedVisibilityScope = this@AnimatedContent
                                    ),
                                painter = painterResource(ComposeCoreR.drawable.ic_pokeball),
                                contentDescription = null,
                                tint = AppTheme.grayscaleColor().white.copy(
                                    alpha = 0.1f
                                )
                            )
                        }.first().measure(
                            constraints.copy(
                                maxHeight = constraints.maxHeight,
                                maxWidth = maxWidthForPokeballIcon
                            )
                        )

                        val content = subcompose("content") {
                            Content(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        horizontal = 4.dp,
                                    )
                                    .padding(
                                        bottom = 4.dp + contentPadding.calculateBottomPadding(),
                                    )
                                    .background(
                                        color = AppTheme.grayscaleColor().white,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                uiState = uiState,
                                paddingForTypes = paddingTopForTypes
                            )
                        }.first().measure(constraints)

                        layout(constraints.maxWidth, constraints.maxHeight) {
                            icon.place(
                                position = IntOffset(
                                    y = paddingTopStatusBar.roundToPx(),
                                    x = constraints.maxWidth - maxWidthForPokeballIcon - 8.dp.roundToPx()
                                )
                            )

                            content.place(
                                position = IntOffset(
                                    y = yPositionContent.roundToInt(),
                                    x = 0
                                )
                            )

                            imagePokemon.place(
                                position = IntOffset(
                                    y = contentWithTopBarPaddingTop,
                                    x = 0
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageWithScroll(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    leftClick: () -> Unit,
    rightClick: () -> Unit,
    chevroneRightVisible: Boolean,
    chevroneLeftVisibility: Boolean,
    imageUrl: String,
    placeholderColor: Color,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier.width(24.dp)
        )

        Icon(
            modifier = Modifier.size(32.dp).clickable(onClick = leftClick).
            visible(chevroneLeftVisibility),
            painter = painterResource(ComposeCoreR.drawable.ic_chevron_left),
            contentDescription = null,
            tint = AppTheme.grayscaleColor().white,
        )

        Spacer(
            modifier = Modifier.width(32.dp)
        )

        val basePlaceholder = painterResource(ComposeCoreR.drawable.pokemon_placeholder)
        val placeholder = remember(basePlaceholder, placeholderColor) {
            TintedPainter(
                delegate = basePlaceholder,
                // затемняем цвет типа, чтобы силуэт читался и на цветном фоне, и на белой карточке
                tint = lerp(placeholderColor, Color.Black, PLACEHOLDER_DARKEN)
                    .copy(alpha = PLACEHOLDER_ALPHA)
            )
        }

        AsyncImage(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .then(imageModifier),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl.ifBlank { null })
                // пока грузится полноразмерная картинка, показываем уже закэшированную из списка
                .placeholderMemoryCacheKey(imageUrl)
                .build(),
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        Spacer(
            modifier = Modifier.width(32.dp)
        )

        Icon(
            modifier = Modifier.size(32.dp).clickable(onClick = rightClick).visible(chevroneRightVisible),
            painter = painterResource(ComposeCoreR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = AppTheme.grayscaleColor().white,
        )

        Spacer(
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    paddingForTypes: Dp,
    uiState: PokemonDetails.Loaded,
) {
    Box(
        modifier = modifier
            .background(
                color = AppTheme.grayscaleColor().white, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier.height(paddingForTypes)
            )

            TypeRow(
                pokemonTypes = uiState.pokemonTypes
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.details_about_title),
                style = AppTheme.headerTypography().subtitle1,
                color = uiState.pokemonTypeMain.color
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            AboutRow(
                uiState = uiState
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    ),
                text = uiState.description,
                style = AppTheme.bodyTypography().body3,
                color = AppTheme.grayscaleColor().dark
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.base_stats_title),
                style = AppTheme.headerTypography().subtitle1,
                color = uiState.pokemonTypeMain.color
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            StatRow(
                statValue = uiState.statHp,
                pokemonTypePresentationModel = uiState.pokemonTypeMain,
                statName = stringResource(R.string.stat_hp)
            )

            StatRow(
                statValue = uiState.statAtk,
                pokemonTypePresentationModel = uiState.pokemonTypeMain,
                statName = stringResource(R.string.stat_atk)
            )

            StatRow(
                statValue = uiState.statDef,
                pokemonTypePresentationModel = uiState.pokemonTypeMain,
                statName = stringResource(R.string.stat_def)
            )

            StatRow(
                statValue = uiState.statSAtk,
                pokemonTypePresentationModel = uiState.pokemonTypeMain,
                statName = stringResource(R.string.stat_satk)
            )

            StatRow(
                statValue = uiState.statSDef,
                pokemonTypePresentationModel = uiState.pokemonTypeMain,
                statName = stringResource(R.string.stat_sdef)
            )

            StatRow(
                statValue = uiState.statSpd,
                pokemonTypePresentationModel = uiState.pokemonTypeMain,
                statName = stringResource(R.string.stat_spd)
            )
        }
    }
}

@Composable
private fun ColumnScope.StatRow(
    statValue: Int,
    pokemonTypePresentationModel: PokemonTypePresentationModel,
    statName: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
            .padding(
                horizontal = 20.dp
            )
            .height(IntrinsicSize.Min),
    ) {
        Text(
            modifier = Modifier.weight(0.1f),
            text = statName,
            style = AppTheme.headerTypography().subtitle3,
            color = pokemonTypePresentationModel.color,
            textAlign = TextAlign.End,
            maxLines = 1
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        VerticalDivider(
            color = AppTheme.grayscaleColor().light
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            modifier = Modifier.weight(0.1f),
            text = if (statValue.toString().length == 3) {
                statValue.toString()
            } else {
                "0${statValue}"
            },
            style = AppTheme.bodyTypography().body3,
            color = AppTheme.grayscaleColor().dark,
            textAlign = TextAlign.End
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        StatBar(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            pokemonTypePresentationModel = pokemonTypePresentationModel,
            statValue = statValue
        )
    }
}

@Composable
private fun ColumnScope.TypeRow(pokemonTypes: Set<PokemonTypePresentationModel>) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pokemonTypes.onEach { type ->
            PokemonTypeChip(
                pokemonType = type
            )
        }
    }
}

@Composable
private fun AboutRow(
    uiState: PokemonDetails.Loaded,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Spacer(
            modifier = Modifier.width(20.dp)
        )

        WeightItem(
            weight = uiState.weight
        )

        VerticalDivider(
            color = AppTheme.grayscaleColor().light
        )

        HeightItem(
            height = uiState.height
        )

        VerticalDivider(
            color = AppTheme.grayscaleColor().light
        )

        MovesItem(
            moves = uiState.moves
        )

        Spacer(
            modifier = Modifier.width(20.dp)
        )
    }
}

@Composable
private fun RowScope.WeightItem(
    weight: String,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(ComposeCoreR.drawable.ic_weight),
                contentDescription = null,
                tint = AppTheme.grayscaleColor().dark,
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                text = weight,
                color = AppTheme.grayscaleColor().dark,
                style = AppTheme.bodyTypography().body3
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.weight_details),
            color = AppTheme.grayscaleColor().medium,
            style = AppTheme.bodyTypography().caption,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.HeightItem(
    height: String,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Spacer(
            Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .rotate(90f),
                painter = painterResource(ComposeCoreR.drawable.ic_straighten),
                contentDescription = null,
                tint = AppTheme.grayscaleColor().dark,
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(
                text = height,
                color = AppTheme.grayscaleColor().dark,
                style = AppTheme.bodyTypography().body3
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.height_details),
            color = AppTheme.grayscaleColor().medium,
            style = AppTheme.bodyTypography().caption,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.MovesItem(
    moves: List<String>,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {

        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            moves.onEach {
                Text(
                    text = it,
                    style = AppTheme.bodyTypography().body3,
                    color = AppTheme.grayscaleColor().dark
                )
            }
        }

        Spacer(
            Modifier.weight(1f)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.moves_details),
            color = AppTheme.grayscaleColor().medium,
            style = AppTheme.bodyTypography().caption,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    pokemonName: String,
    pokemonNumber: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(
                top = 24.dp, bottom = 24.dp
            )
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier.width(24.dp)
        )

        Icon(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    onClick = onBackClick
                ),
            painter = painterResource(ComposeCoreR.drawable.ic_arrow_back),
            contentDescription = null,
            tint = AppTheme.grayscaleColor().white
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            modifier = Modifier.weight(1f),
            text = pokemonName,
            color = AppTheme.grayscaleColor().white,
            style = AppTheme.headerTypography().headline
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = pokemonNumber,
            color = AppTheme.grayscaleColor().white,
            style = AppTheme.headerTypography().subtitle2
        )

        Spacer(
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
@Preview(device = "id:Nexus S")
private fun PokemonDetailsScreenContentPreview() {
    AppTheme {
        PokemonDetailsScreenContent(
            uiStateMain = PokemonDetailsUiState(
                pokemonMap = mapOf(
                    PokemonId(1) to PokemonDetails.Loaded(
                        pokemonName = "Pokémon Name",
                        pokemonNumber = "#999",
                        pokemonImageUrl = "",
                        weight = "6,9 kg",
                        height = "0,7 m",
                        statHp = 45,
                        statAtk = 49,
                        statDef = 49,
                        statSpd = 45,
                        statSAtk = 65,
                        statSDef = 65,
                        moves = listOf(
                            "Chlorophyll", "Overgrow"
                        ),
                        description = "There is a plant seed on its back right from the day this Pokémon is born. The seed slowly grows larger.",
                        pokemonTypeMain = PokemonTypePresentationModel.Grass,
                        pokemonTypes = setOf(PokemonTypePresentationModel.Grass, PokemonTypePresentationModel.Poison)
                    )
                ),
                currentPokemonById = PokemonId(1)
            ),
            onBackClick = {},
            rightClick = {},
            leftClick = {}
        )
    }
}

@Composable
@Preview(device = "id:Nexus S")
private fun PokemonDetailsScreenContentLoadingPreview() {
    AppTheme {
        PokemonDetailsScreenContent(
            uiStateMain = PokemonDetailsUiState(
                pokemonMap = mapOf(
                    PokemonId(1) to PokemonDetails.Loading
                ),
                currentPokemonById = PokemonId(1)
            ),
            onBackClick = {},
            rightClick = {},
            leftClick = {}
        )
    }
}