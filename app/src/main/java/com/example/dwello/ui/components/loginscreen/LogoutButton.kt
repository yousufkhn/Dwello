import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dwello.utils.auth.GoogleAuthManager

@Composable
fun LogoutButton(
    googleAuthManager: GoogleAuthManager,
    onLogout: () -> Unit
) {
    Button(
        onClick = {
            googleAuthManager.signOut(
                onSignOutSuccess = { onLogout() },
                onSignOutFailure = { /* handle error or show toast */ }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Logout")
    }
}
