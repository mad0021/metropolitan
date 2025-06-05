package cat.dam.mamadou.metropolitan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.dam.mamadou.metropolitan.R
import cat.dam.mamadou.metropolitan.utils.NetworkUtils
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun ArtworkImage(
    primaryImage: String?,
    primaryImageSmall: String?,
    title: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val isConnected = NetworkUtils.isNetworkAvailable(context)

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(primaryImage ?: primaryImageSmall)
            .crossfade(true)
            .build(),
        contentDescription = title ?: stringResource(R.string.cd_artwork_image),
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            // Animació de càrrega amb Lottie
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                LoadingLottieAnimation(
                    modifier = Modifier.size(60.dp)
                )
            }
        },
        error = {
            // Animació d'error amb Lottie
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isConnected) {
                        ErrorLottieAnimation(
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_internet_connection),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        ErrorLottieAnimation(
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.image_load_error),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}