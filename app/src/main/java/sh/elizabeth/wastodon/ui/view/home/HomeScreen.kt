package sh.elizabeth.wastodon.ui.view.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import sh.elizabeth.wastodon.ui.theme.WastodonTheme

@Composable
fun HomeScreen(uiState: HomeUiState) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Home screen")
        }
    }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview
@Composable
fun HomeScreenPreview() {
    WastodonTheme { HomeScreen(HomeUiState.NoPosts(isLoading = false, activeAccount = "blahaj.zone:abcd")) }
}
