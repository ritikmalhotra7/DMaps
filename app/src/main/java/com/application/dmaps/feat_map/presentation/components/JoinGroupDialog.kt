package com.application.dmaps.feat_map.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun JoinGroupDialog(
    modifier: Modifier = Modifier,
    onCancelClicked: () -> Unit = {},
    onGroupCodeSubmit: (String) -> Unit = {}
) {
    var updatedGroupCode by remember{ mutableStateOf("") }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Dialog(onDismissRequest = { }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(size = 16.dp))
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Enter group code to join",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(8.dp)
                )
                HorizontalDivider()
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = updatedGroupCode,
                    onValueChange = {code -> updatedGroupCode = code},
                    label = { Text("Group Code") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Groups, // Email icon
                            contentDescription = "Username Icon"
                        )
                    }
                )
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(32.dp),
                        onClick = onCancelClicked,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Button(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(32.dp),
                        onClick = { onGroupCodeSubmit(updatedGroupCode) },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Join",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}