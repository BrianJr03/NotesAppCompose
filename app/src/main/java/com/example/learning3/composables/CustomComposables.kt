package com.example.learning3.composables

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learning3.data.Note
import com.example.learning3.ui.theme.Learning3Theme
import com.example.learning3.utilities.UtilityFunctions
import com.example.learning3.utilities.UtilityFunctions.calculateDelayAndEasing
import com.example.learning3.utilities.UtilityFunctions.formatDateAndTime
import com.example.learning3.utilities.UtilityFunctions.scaleAndAlpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Search notes...", style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
            ),
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }
}

@Composable
fun Dialog(
    titleText: String,
    bodyText: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit
) {
    AlertDialog(
        title = { Text(titleText) },
        text = { Text(bodyText) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                onClick = onConfirmButtonClick
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text("Cancel")
            }
        }
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CustomNavigationDrawer(
//    drawerState: DrawerState,
//    menuList: List<Pair<ImageVector, String>>,
//    selectedDrawerItem: Pair<ImageVector, String>,
//    itemOnClick: () -> Unit,
//    contentOnClick: () -> Unit,
//) {
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            ModalDrawerSheet {
//                Spacer(modifier = Modifier.height(12.dp))
//                menuList.forEachIndexed { index, data ->
//                    NavigationDrawerItem(
//                        label = {
//                            Text(text = data.second)
//                        },
//                        icon = {
//                            Icon(
//                                imageVector = data.first,
//                                contentDescription = "Drawer icon $index"
//                            )
//                        },
//                        modifier = Modifier
//                            .padding(NavigationDrawerItemDefaults.ItemPadding),
//                        selected = selectedDrawerItem == index,
//                        onClick = itemOnClick,
//                    )
//                }
//            }
//        },
//        content = {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(text = if (drawerState.isClosed) ">>> Swipe >>>" else "<<< Swipe <<<")
//                Spacer(Modifier.height(20.dp))
//                Button(onClick = { scope.launch { drawerState.open() } }) {
//                    Text("Click to open")
//                }
//            }
//        }
//        ) {
//
//    }
//}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesGrid(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit
) {
    val lazyStaggeredListState = rememberLazyStaggeredGridState()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.padding(
            horizontal = 12.dp
        ),
        state = lazyStaggeredListState
    ) {
        items(notes) { note ->
            val index = notes.indexOf(note)
            val (delay, easing) = lazyStaggeredListState.calculateDelayAndEasing(index, 3)
            val animation = tween<Float>(durationMillis = 150, delayMillis = delay, easing = easing)
            val args = UtilityFunctions.ScaleAndAlphaArgs(
                fromScale = 2f,
                toScale = 1f,
                fromAlpha = 0f,
                toAlpha = 1f
            )
            val (scale, alpha) = scaleAndAlpha(args = args, animation = animation)
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .combinedClickable(
                        onClick = { onNoteClick(note) },
                        onLongClick = { onNoteLongClick(note) }
                    )
                    .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale),
                colors = CardDefaults.cardColors(
                    containerColor = if (!note.isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (!note.isSelected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = CardDefaults.outlinedCardBorder(
                    note.isPinned
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(),
                ) {
                    Text(
                        text = note.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        ),
                    )
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )
                    Text(
                        text = note.content,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                        )
                    )
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )
                    Text(
                        text = note.lastModified.formatDateAndTime(),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                        ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsListItem(
    title: String,
    trailingContent: @Composable (() -> Unit)?
) {
    ListItem(
        headlineText = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp

                )
            )
        },
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
            headlineColor = MaterialTheme.colorScheme.inverseSurface,
            disabledHeadlineColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.6f)
        ),
//        modifier = Modifier
//            .padding(
//                top = 12.dp
//            )
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun CustomComposablePreview() {
    Learning3Theme {

    }
}