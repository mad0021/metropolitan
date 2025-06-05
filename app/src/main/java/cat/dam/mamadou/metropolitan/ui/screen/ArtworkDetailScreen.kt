package cat.dam.mamadou.metropolitan.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cat.dam.mamadou.metropolitan.R
import cat.dam.mamadou.metropolitan.data.model.ArtworkDetail
import cat.dam.mamadou.metropolitan.data.model.NetworkResult
import cat.dam.mamadou.metropolitan.ui.components.ErrorState
import cat.dam.mamadou.metropolitan.ui.components.LoadingState
import cat.dam.mamadou.metropolitan.ui.components.ArtworkImage
import cat.dam.mamadou.metropolitan.ui.viewmodel.ArtworkDetailViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkDetailScreen(
    objectId: Int,
    onBackClick: () -> Unit,
    viewModel: ArtworkDetailViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val context = LocalContext.current

    val artworkState by viewModel.artworkState.collectAsState()

    LaunchedEffect(objectId) {
        viewModel.loadArtworkDetail(objectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.artwork_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = artworkState) {
                is NetworkResult.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingState()
                    }
                }
                is NetworkResult.Success -> {
                    ArtworkDetailContent(
                        artwork = state.data,
                        isTablet = isTablet,
                        onOpenOriginalClick = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )
                }
                is NetworkResult.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadArtworkDetail(objectId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtworkDetailContent(
    artwork: ArtworkDetail,
    isTablet: Boolean,
    onOpenOriginalClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imatge principal amb Lottie animations
        if (artwork.primaryImage != null || artwork.primaryImageSmall != null) {
            ArtworkImage(
                primaryImage = artwork.primaryImage,
                primaryImageSmall = artwork.primaryImageSmall,
                title = artwork.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 400.dp else 300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Informació bàsica
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = artwork.title ?: stringResource(R.string.unknown_value),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                DetailRow(
                    label = stringResource(R.string.artist),
                    value = artwork.artistDisplayName ?: artwork.artistAlphaSort ?: stringResource(R.string.unknown_value)
                )

                if (artwork.artistNationality != null) {
                    DetailRow(
                        label = stringResource(R.string.artist_nationality),
                        value = artwork.artistNationality
                    )
                }

                if (artwork.objectDate != null) {
                    DetailRow(
                        label = stringResource(R.string.artwork_date),
                        value = artwork.objectDate
                    )
                }

                if (artwork.medium != null) {
                    DetailRow(
                        label = stringResource(R.string.artwork_medium),
                        value = artwork.medium
                    )
                }

                if (artwork.dimensions != null) {
                    DetailRow(
                        label = stringResource(R.string.artwork_dimensions),
                        value = artwork.dimensions
                    )
                }

                if (artwork.classification != null) {
                    DetailRow(
                        label = stringResource(R.string.artwork_classification),
                        value = artwork.classification
                    )
                }

                if (artwork.repository != null) {
                    DetailRow(
                        label = stringResource(R.string.artwork_repository),
                        value = artwork.repository
                    )
                }

                if (artwork.creditLine != null) {
                    DetailRow(
                        label = stringResource(R.string.artwork_credit),
                        value = artwork.creditLine
                    )
                }
            }
        }

        // Botó per obrir la pàgina original
        if (artwork.objectURL != null) {
            Button(
                onClick = { onOpenOriginalClick(artwork.objectURL) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.open_original))
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ErrorState(
            message = message,
            onRetry = onRetry
        )
    }
}
