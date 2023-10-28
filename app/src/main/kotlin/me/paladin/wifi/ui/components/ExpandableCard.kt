package me.paladin.wifi.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    expanded: Boolean,
    onCardClicked: () -> Unit,
    expandedContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = updateTransition(expanded, label = "transition")

    val paddingVertical by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "paddingHorizontalTransition"
    ) { isExpanded ->
        if (isExpanded) 4.dp else 0.dp
    }

    val paddingHorizontal by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "paddingHorizontalTransition"
    ) { isExpanded ->
        if (isExpanded) 8.dp else 0.dp
    }

    val cardElevation by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "cardElevationTransition"
    ) { isExpanded ->
        if (isExpanded) 2.dp else 0.dp
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = paddingVertical,
                horizontal = paddingHorizontal
            ),
        onClick = onCardClicked
    ) {
        Column {
            Box(content = content)

            ExpandableContent(
                visible = expanded,
                content = expandedContent
            )
        }
    }
}

@Composable
fun ExpandableContent(
    visible: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(450)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(450)
        )
    }

    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(450)
        ) + fadeOut(
            animationSpec = tween(450)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column {
            HorizontalDivider()
            Box(content = content)
        }
    }
}