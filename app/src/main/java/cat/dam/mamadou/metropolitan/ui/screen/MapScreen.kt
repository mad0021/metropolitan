package cat.dam.mamadou.metropolitan.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import cat.dam.mamadou.metropolitan.R
import cat.dam.mamadou.metropolitan.data.model.ArtworkDetail
import cat.dam.mamadou.metropolitan.data.model.EuropeanCapital
import cat.dam.mamadou.metropolitan.data.model.NetworkResult
import cat.dam.mamadou.metropolitan.ui.components.ErrorLottieAnimation
import cat.dam.mamadou.metropolitan.ui.components.LoadingLottieAnimation
import cat.dam.mamadou.metropolitan.ui.components.NoResultsLottieAnimation
import cat.dam.mamadou.metropolitan.ui.viewmodel.MapViewModel
import cat.dam.mamadou.metropolitan.utils.RequestLocationPermissions
import cat.dam.mamadou.metropolitan.utils.PermissionUtils
import cat.dam.mamadou.metropolitan.map.createCustomMarkerBitmapDescriptor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    isConnected: Boolean,
    onArtworkClick: (Int) -> Unit,
    viewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    var hasLocationPermission by remember { 
        mutableStateOf(PermissionUtils.hasLocationPermissions(context))
    }
    
    val selectedCapital by viewModel.selectedCapital.collectAsState()
    val artworksState by viewModel.artworksState.collectAsState()
    val isBottomSheetVisible by viewModel.isBottomSheetVisible.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Demanar permisos de localització si no els té
    if (!hasLocationPermission) {
        RequestLocationPermissions { granted ->
            hasLocationPermission = granted
        }
    }

    LaunchedEffect(isBottomSheetVisible) {
        if (isBottomSheetVisible) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !isConnected -> {
                NoConnectionScreen()
            }
            !hasLocationPermission -> {
                NoLocationPermissionScreen()
            }
            else -> {
                MapContent(
                    isTablet = isTablet,
                    onCapitalSelected = viewModel::onCapitalSelected,
                    context = context
                )
            }
        }

        // Instruccions sobre el mapa
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.map_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    // Bottom Sheet amb les obres d'art
    if (isBottomSheetVisible && selectedCapital != null) {
        ModalBottomSheet(
            onDismissRequest = viewModel::hideBottomSheet,
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            ArtworksBottomSheetContent(
                capital = selectedCapital!!,
                artworksState = artworksState,
                onArtworkClick = onArtworkClick,
                onCloseClick = viewModel::hideBottomSheet,
                isTablet = isTablet
            )
        }
    }
}

@Composable
fun MapContent(
    isTablet: Boolean,
    onCapitalSelected: (EuropeanCapital) -> Unit,
    context: Context
) {
    val europeCenter = LatLng(54.5260, 15.2551)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(europeCenter, if (isTablet) 5f else 4f)
    }

    // Variable para el capital seleccionado
    var selectedMarkerInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {  // Box añadido para contener el mapa y la tarjeta
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true
            )
        ) {
            EuropeanCapital.capitals.forEach { capital ->
                Marker(
                    state = MarkerState(
                        position = LatLng(capital.latitude, capital.longitude)
                    ),
                    title = capital.capital,
                    snippet = capital.country,
                    icon = createCustomMarkerBitmapDescriptor(context, capital.capital),
                    onClick = {
                        // Actualizar la información del marcador seleccionado
                        selectedMarkerInfo = Pair(capital.country, capital.capital)
                        onCapitalSelected(capital)
                        true
                    }
                )
            }
        }

        // Mostrar la información del país i capital seleccionats
        selectedMarkerInfo?.let { (country, capital) ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),  // Ahora esto está dentro de un Box
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = capital,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = country,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ArtworksBottomSheetContent(
    capital: EuropeanCapital,
    artworksState: NetworkResult<List<ArtworkDetail>>,
    onArtworkClick: (Int) -> Unit,
    onCloseClick: () -> Unit,
    isTablet: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.artworks_from, capital.country),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contingut basat en l'estat
        when (artworksState) {
            is NetworkResult.Loading -> {
                LoadingContent()
            }
            is NetworkResult.Success -> {
                if (artworksState.data.isEmpty()) {
                    NoArtworksContent()
                } else {
                    ArtworksList(
                        artworks = artworksState.data,
                        onArtworkClick = onArtworkClick,
                        isTablet = isTablet
                    )
                }
            }
            is NetworkResult.Error -> {
                ErrorContent(artworksState.message)
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LoadingAnimation(modifier = Modifier.size(80.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun NoArtworksContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NoResultsAnimation(modifier = Modifier.size(80.dp))
            Text(
                text = stringResource(R.string.no_artworks_found),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ErrorContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ErrorAnimation(modifier = Modifier.size(80.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ArtworksList(
    artworks: List<ArtworkDetail>,
    onArtworkClick: (Int) -> Unit,
    isTablet: Boolean
) {
    Column {
        Text(
            text = stringResource(R.string.total_artworks, artworks.size),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(artworks) { artwork ->
                ArtworkCard(
                    artwork = artwork,
                    onArtworkClick = onArtworkClick,
                    isTablet = isTablet
                )
            }
        }
    }
}

@Composable
fun ArtworkCard(
    artwork: ArtworkDetail,
    onArtworkClick: (Int) -> Unit,
    isTablet: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArtworkClick(artwork.objectID) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imatge de l'obra
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artwork.primaryImageSmall ?: artwork.primaryImage)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.cd_artwork_image),
                modifier = Modifier
                    .size(if (isTablet) 100.dp else 80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Informació de l'obra
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = artwork.artistDisplayName ?: artwork.artistAlphaSort ?: stringResource(R.string.unknown_value),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artwork.title ?: stringResource(R.string.unknown_value),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (artwork.objectDate != null) {
                    Text(
                        text = artwork.objectDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = stringResource(R.string.view_details),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun NoConnectionScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = stringResource(R.string.no_internet_connection),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = stringResource(R.string.check_connection),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NoLocationPermissionScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Please grant location permission to view the map with European artworks",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
