package cat.dam.mamadou.metropolitan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cat.dam.mamadou.metropolitan.ui.navigation.MetropolitanApp
import cat.dam.mamadou.metropolitan.ui.theme.MetropolitanTheme
import cat.dam.mamadou.metropolitan.utils.RequestRequiredPermissions
import cat.dam.mamadou.metropolitan.utils.PermissionUtils
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instal·lar splash screen
        installSplashScreen()

        // Habilitar edge-to-edge
        enableEdgeToEdge()

        setContent {
            MetropolitanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppWithPermissions()
                }
            }
        }
    }
}

@Composable
fun AppWithPermissions() {
    val context = LocalContext.current
    var permissionsState by remember { 
        mutableStateOf(
            if (PermissionUtils.hasAllRequiredPermissions(context)) {
                PermissionState.GRANTED
            } else {
                PermissionState.CHECKING
            }
        )
    }

    when (permissionsState) {
        PermissionState.CHECKING -> {
            // Pantalla de verificación de permisos
            RequestRequiredPermissions { granted ->
                permissionsState = if (granted) {
                    PermissionState.GRANTED
                } else {
                    PermissionState.DENIED
                }
            }
            
            LoadingPermissionsScreen()
        }
        
        PermissionState.GRANTED -> {
            // Todos los permisos concedidos, mostrar la app
            MetropolitanApp()
        }
        
        PermissionState.DENIED -> {
            // Permisos denegados, mostrar pantalla de error
            PermissionDeniedScreen(
                onRetry = {
                    permissionsState = PermissionState.CHECKING
                }
            )
        }
    }
}

@Composable
fun LoadingPermissionsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Checking permissions...",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Please grant required permissions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PermissionDeniedScreen(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "Permissions Required",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "This app needs certain permissions to function properly. Please grant the required permissions to continue.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant Permissions")
            }
        }
    }
}

enum class PermissionState {
    CHECKING,
    GRANTED,
    DENIED
}
