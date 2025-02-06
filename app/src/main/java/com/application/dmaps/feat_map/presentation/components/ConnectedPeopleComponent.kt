package com.application.dmaps.feat_map.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.dmaps.R

@Composable
fun ConnectedPeopleVertical(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "",
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
                .padding(horizontal = 4.dp)
        )
        Column() {
            Text(
                text = "Lorem ipsum dolor sit amet",
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 0.dp)
                    .fillMaxWidth(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 0.dp)
                    .fillMaxWidth(),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

            )
        }
    }
}

@Composable
fun ConnectedPeopleHorizontal(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background),
        contentDescription = "",
        modifier = Modifier
            .height(32.dp)
            .width(32.dp)
            .padding(horizontal = 4.dp)
    )
}