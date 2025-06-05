package cat.dam.mamadou.metropolitan.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun LoadingLottieAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("loading.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier
        )
    } else {
        // Fallback si no es troba l'animació
        CircularProgressIndicator(modifier = modifier.size(48.dp))
    }
}

@Composable
fun ErrorLottieAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("error.json"))
    val progress by animateLottieCompositionAsState(composition = composition)

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier
        )
    } else {
        // Fallback si no es troba l'animació
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun NoResultsLottieAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("no_results.json"))
    val progress by animateLottieCompositionAsState(composition = composition)

    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier
        )
    } else {
        // Fallback si no es troba l'animació
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}