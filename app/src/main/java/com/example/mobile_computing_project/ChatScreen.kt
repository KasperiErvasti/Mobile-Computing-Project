package com.example.mobile_computing_project

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mobile_computing_project.ui.theme.MobileComputingProjectTheme
import java.io.File


@Composable
fun ChatScreen(
    onNavigateToProfile: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(4.dp)

        ) {

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = onNavigateToProfile,
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Go to profile",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Chat(SampleData.chatSample)
    }

}


data class Message(val author: String, val body: String)

@Composable
fun MessageCard(msg: Message, imageFile: File) {

    var text by remember { mutableStateOf(msg.author) }

    Row(modifier = Modifier.padding(all = 8.dp)) {
        AsyncImage(
            model = imageFile.toURI().toString() + "?timestamp=${System.currentTimeMillis()}",
            contentDescription = "profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this
        // variable
        var isExpanded by remember { mutableStateOf(false) }
        // surfaceColor will be updated gradually from one color to the other
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )

        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun Chat(messages: List<Message>) {
    val context = LocalContext.current
    val file = File(context.filesDir, "profile_picture")
    val usernameFile = File(context.filesDir, "username")
    val author = usernameFile.readBytes().decodeToString()

    LazyColumn {
        items(messages) { message ->
            MessageCard(Message(author, message.body), file)
        }
    }
}


/**
 * SampleData for Jetpack Compose Tutorial
 */
object SampleData {
    private const val AUTHOR: String = "Hullu"

    // Sample chat data
    val chatSample = listOf(
        Message(
            AUTHOR,
            "Test...Test...Test..."
        ),
        Message(
            AUTHOR,
            """List of Android versions:
            |Android KitKat (API 19)
            |Android Lollipop (API 21)
            |Android Marshmallow (API 23)
            |Android Nougat (API 24)
            |Android Oreo (API 26)
            |Android Pie (API 28)
            |Android 10 (API 29)
            |Android 11 (API 30)
            |Android 12 (API 31)""".trim()
        ),
        Message(
            AUTHOR,
            """I think Kotlin is my favorite programming language.
            |It's so much fun!""".trim()
        ),
        Message(
            AUTHOR,
            "Searching for alternatives to XML layouts..."
        ),
        Message(
            AUTHOR,
            """Hey, take a look at Jetpack Compose, it's great!
            |It's the Android's modern toolkit for building native UI.
            |It simplifies and accelerates UI development on Android.
            |Less code, powerful tools, and intuitive Kotlin APIs :)""".trim()
        ),
        Message(
            AUTHOR,
            "It's available from API 21+ :)"
        ),
        Message(
            AUTHOR,
            "Writing Kotlin for UI seems so natural, Compose where have you been all my life?"
        ),
        Message(
            AUTHOR,
            "Android Studio next version's name is Arctic Fox"
        ),
        Message(
            AUTHOR,
            "Android Studio Arctic Fox tooling for Compose is top notch ^_^"
        ),
        Message(
            AUTHOR,
            "I didn't know you can now run the emulator directly from Android Studio"
        ),
        Message(
            AUTHOR,
            "Compose Previews are great to check quickly how a composable layout looks like"
        ),
        Message(
            AUTHOR,
            "Previews are also interactive after enabling the experimental setting"
        ),
        Message(
            AUTHOR,
            "Have you tried writing build.gradle with KTS?"
        ),
    )
}

@Preview
@Composable
fun PreviewMessageCard() {
    val context = LocalContext.current
    val file = File(context.filesDir, "profile_picture")

    MobileComputingProjectTheme {
        Surface {
            MessageCard(
                msg = Message("Hullu", "Hey, take a look at Jetpack Compose, it's great!"), file
            )
        }
    }
}

@Preview
@Composable
fun PreviewChatScreen() {
    ChatScreen { }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)

@Preview
@Composable
fun PreviewChat() {
    MobileComputingProjectTheme {
        Chat(SampleData.chatSample)
    }
}
