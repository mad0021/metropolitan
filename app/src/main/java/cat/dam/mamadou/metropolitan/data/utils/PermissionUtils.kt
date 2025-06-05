package cat.dam.mamadou.metropolitan.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

object PermissionUtils {
    
    // Permisos esenciales (la app no funciona sin estos)
    val ESSENTIAL_PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )
    
    // Permisos de localización (necesarios para Maps)
    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    
    // Todos los permisos requeridos
    val REQUIRED_PERMISSIONS = ESSENTIAL_PERMISSIONS + LOCATION_PERMISSIONS

    val OPTIONAL_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasEssentialPermissions(context: Context): Boolean {
        return ESSENTIAL_PERMISSIONS.all { permission ->
            hasPermission(context, permission)
        }
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return LOCATION_PERMISSIONS.any { permission ->
            hasPermission(context, permission)
        }
    }
    
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            hasPermission(context, permission)
        }
    }
    
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}

@Composable
fun RequestRequiredPermissions(
    onPermissionsResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var permissionDeniedCount by remember { mutableStateOf(0) }
    
    // Launcher para permisos esenciales
    val essentialPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val essentialGranted = PermissionUtils.hasEssentialPermissions(context)
        
        if (essentialGranted) {
            // Si los esenciales están concedidos, pedir localización
            if (!PermissionUtils.hasLocationPermissions(context)) {
                showLocationDialog = true
            } else {
                onPermissionsResult(true)
            }
        } else {
            permissionDeniedCount++
            if (permissionDeniedCount >= 2) {
                showSettingsDialog = true
            } else {
                showPermissionDialog = true
            }
        }
    }
    
    // Launcher para permisos de localización
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = PermissionUtils.hasLocationPermissions(context)
        // La app puede funcionar sin localización, pero con funcionalidad limitada
        onPermissionsResult(true)
    }

    LaunchedEffect(Unit) {
        when {
            !PermissionUtils.hasEssentialPermissions(context) -> {
                essentialPermissionLauncher.launch(PermissionUtils.ESSENTIAL_PERMISSIONS)
            }
            !PermissionUtils.hasLocationPermissions(context) -> {
                showLocationDialog = true
            }
            else -> {
                onPermissionsResult(true)
            }
        }
    }

    // Dialog para permisos esenciales
    if (showPermissionDialog) {
        EssentialPermissionDialog(
            onDismiss = { 
                showPermissionDialog = false
                onPermissionsResult(false)
            },
            onRetry = {
                showPermissionDialog = false
                essentialPermissionLauncher.launch(PermissionUtils.ESSENTIAL_PERMISSIONS)
            }
        )
    }
    
    // Dialog para permisos de localización
    if (showLocationDialog) {
        LocationPermissionDialog(
            onDismiss = { 
                showLocationDialog = false
                onPermissionsResult(true) // Continuar sin localización
            },
            onRetry = {
                showLocationDialog = false
                locationPermissionLauncher.launch(PermissionUtils.LOCATION_PERMISSIONS)
            }
        )
    }
    
    // Dialog para ir a configuración
    if (showSettingsDialog) {
        SettingsPermissionDialog(
            onDismiss = { 
                showSettingsDialog = false
                onPermissionsResult(false)
            },
            onOpenSettings = {
                showSettingsDialog = false
                PermissionUtils.openAppSettings(context)
                onPermissionsResult(false)
            }
        )
    }
}

@Composable
fun EssentialPermissionDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Essential Permissions Required",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("This app needs the following essential permissions:")
                
                Text("• Internet Access")
                Text("  To load artworks from Metropolitan Museum")
                
                Text("• Network State")
                Text("  To check your connection status")
                
                Text(
                    text = "\nWithout these permissions, the app cannot function.",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Grant Permissions")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Exit App")
            }
        }
    )
}

@Composable
fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Location Permission")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Location permission enables:")
                
                Text("• Interactive map with European capitals")
                Text("• Location-based artwork recommendations")
                Text("• Enhanced museum experience")
                
                Text(
                    text = "\nYou can skip this and use the app without map features.",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Allow Location")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip for now")
            }
        }
    )
}

@Composable
fun SettingsPermissionDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Permissions Required")
        },
        text = {
            Text(
                "The app needs essential permissions to work. Please go to Settings and enable the required permissions manually."
            )
        },
        confirmButton = {
            Button(onClick = onOpenSettings) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RequestLocationPermissions(
    onPermissionsResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showLocationDialog by remember { mutableStateOf(false) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasAnyLocationPermission = permissions.values.any { it }
        onPermissionsResult(hasAnyLocationPermission)
        
        if (!hasAnyLocationPermission) {
            showLocationDialog = true
        }
    }

    LaunchedEffect(Unit) {
        if (!PermissionUtils.hasLocationPermissions(context)) {
            locationPermissionLauncher.launch(PermissionUtils.LOCATION_PERMISSIONS)
        } else {
            onPermissionsResult(true)
        }
    }

    if (showLocationDialog) {
        LocationPermissionDialog(
            onDismiss = { 
                showLocationDialog = false
                onPermissionsResult(false)
            },
            onRetry = {
                showLocationDialog = false
                locationPermissionLauncher.launch(PermissionUtils.LOCATION_PERMISSIONS)
            }
        )
    }
}