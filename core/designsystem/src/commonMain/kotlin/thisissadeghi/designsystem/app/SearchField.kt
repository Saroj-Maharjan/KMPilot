package thisissadeghi.designsystem.app

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import thisissadeghi.designsystem.XTextField

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmitQuery: () -> Unit,
    showClearQueryButton: Boolean,
    onClearQuery: (() -> Unit)?,
    modifier: Modifier,
) {
    XTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty() && showClearQueryButton) {
                onClearQuery?.let {
                    IconButton(onClick = it) {
                        Icon(imageVector = Icons.Outlined.Clear, contentDescription = null)
                    }
                }
            }
        },
        singleLine = true,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(223, 223, 223),
                focusedBorderColor = Color(223, 223, 223),
                unfocusedBorderColor = Color(223, 223, 223),
            ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions { onSubmitQuery() },
        textStyle = MaterialTheme.typography.bodyLarge,
    )
}
