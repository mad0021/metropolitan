package cat.dam.mamadou.metropolitan.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import cat.dam.mamadou.metropolitan.R
import cat.dam.mamadou.metropolitan.data.model.ArtworkDetail
import cat.dam.mamadou.metropolitan.data.model.Department
import cat.dam.mamadou.metropolitan.data.model.NetworkResult
import cat.dam.mamadou.metropolitan.data.model.SearchFilters
import cat.dam.mamadou.metropolitan.ui.components.ErrorLottieAnimation
import cat.dam.mamadou.metropolitan.ui.components.ErrorState
import cat.dam.mamadou.metropolitan.ui.components.LoadingLottieAnimation
import cat.dam.mamadou.metropolitan.ui.components.NoResultsLottieAnimation
import cat.dam.mamadou.metropolitan.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onArtworkClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val keyboardController = LocalSoftwareKeyboardController.current

    val departmentsState by viewModel.departmentsState.collectAsState()
    val searchResultsState by viewModel.searchResultsState.collectAsState()
    val searchFilters by viewModel.searchFilters.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Filtros de búsqueda
        when (val state = departmentsState) {
            is NetworkResult.Loading -> {
                // Mostrar loading para departamentos
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NetworkResult.Success -> {
                SearchForm(
                    searchQuery = searchFilters.query,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    selectedDepartment = searchFilters.selectedDepartment,
                    onDepartmentChange = viewModel::updateSelectedDepartment,
                    departments = state.data,
                    onSearch = viewModel::performSearch,
                    onClear = viewModel::clearSearch,
                    isSearching = isSearching,
                    isTablet = isTablet
                )
            }
            is NetworkResult.Error -> {
                // Mostrar error para departamentos
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading departments: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Resultats de la cerca
        SearchResults(
            searchResultsState = searchResultsState,
            searchFilters = searchFilters,
            onArtworkClick = onArtworkClick,
            isSearching = isSearching,
            isTablet = isTablet
        )
    }
}

@Composable
fun SearchForm(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDepartment: Department?,
    onDepartmentChange: (Department?) -> Unit,
    departments: List<Department>,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    isSearching: Boolean,
    isTablet: Boolean
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Camp de cerca per text
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() }
                ),
                singleLine = true
            )

            // Selector de departament
            DepartmentSelector(
                selectedDepartment = selectedDepartment,
                departments = departments,
                onDepartmentChange = onDepartmentChange,
                isTablet = isTablet
            )

            // Botons d'acció
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isTablet) {
                    Arrangement.spacedBy(16.dp)
                } else {
                    Arrangement.SpaceBetween
                }
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = if (isTablet) Modifier.weight(1f) else Modifier
                ) {
                    Text(stringResource(R.string.clear_search))
                }

                Button(
                    onClick = onSearch,
                    enabled = !isSearching && (searchQuery.isNotEmpty() || selectedDepartment != null),
                    modifier = if (isTablet) Modifier.weight(1f) else Modifier
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.search_button))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentSelector(
    selectedDepartment: Department?,
    departments: List<Department>,
    onDepartmentChange: (Department?) -> Unit,
    isTablet: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.department_filter),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedDepartment?.displayName ?: stringResource(R.string.all_departments),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .clickable { expanded = true },
                label = { Text(stringResource(R.string.select_department)) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Opció per tots els departaments
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.all_departments)) },
                    onClick = {
                        onDepartmentChange(null)
                        expanded = false
                    }
                )

                // Departaments específics
                departments.forEach { department ->
                    DropdownMenuItem(
                        text = { Text(department.displayName) },
                        onClick = {
                            onDepartmentChange(department)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResults(
    searchResultsState: NetworkResult<List<ArtworkDetail>>,
    searchFilters: SearchFilters,
    onArtworkClick: (Int) -> Unit,
    isSearching: Boolean,
    isTablet: Boolean
) {
    when (val state = searchResultsState) {
        is NetworkResult.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingLottieAnimation(
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        is NetworkResult.Success -> {
            if (state.data.isEmpty() && searchFilters.query.isNotEmpty()) {
                // Mostrar animació de no resultats
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NoResultsLottieAnimation(
                            modifier = Modifier.size(120.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_results_found),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.try_different_search),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.data) { artwork ->
                        SearchArtworkCard(  // Canviar de ArtworkSearchItem a SearchArtworkCard
                            artwork = artwork,
                            onArtworkClick = onArtworkClick,
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
        is NetworkResult.Error -> {
            ErrorState(
                message = state.message,
                onRetry = { /* implement retry logic */ }
            )
        }
    }
}

@Composable
fun SearchArtworkCard(
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
                    .size(if (isTablet) 120.dp else 100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Informació de l'obra
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = artwork.title ?: stringResource(R.string.unknown_value),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artwork.artistDisplayName ?: artwork.artistAlphaSort ?: stringResource(R.string.unknown_value),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (artwork.objectDate != null) {
                    Text(
                        text = artwork.objectDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (artwork.medium != null) {
                    Text(
                        text = artwork.medium,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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

// Al final del fitxer, afegir aquestes funcions:
@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    LoadingLottieAnimation(modifier = modifier)
}

@Composable
fun NoResultsAnimation(modifier: Modifier = Modifier) {
    NoResultsLottieAnimation(modifier = modifier)
}

@Composable
fun ErrorAnimation(modifier: Modifier = Modifier) {
    ErrorLottieAnimation(modifier = modifier)
}