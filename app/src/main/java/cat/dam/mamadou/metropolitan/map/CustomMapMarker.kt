package cat.dam.mamadou.metropolitan.map

import android.content.Context
import android.graphics.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import cat.dam.mamadou.metropolitan.R

data class MapMarker(
    val position: LatLng,
    val city: String,
    val country: String
)

fun createCustomMarkerBitmapDescriptor(
    context: Context,
    cityName: String
): BitmapDescriptor {
    // Cargar la imagen the_met.png desde recursos
    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.the_met)
    
    // Factor de escala MUCHO MÁS reducido (0.1 = 10% del tamaño original)
    val scaleFactor = 0.1f
    
    // Calcular las nuevas dimensiones
    val scaledWidth = (originalBitmap.width * scaleFactor).toInt()
    val scaledHeight = (originalBitmap.height * scaleFactor).toInt()
    
    // Escalar el bitmap original
    val scaledBitmap = Bitmap.createScaledBitmap(
        originalBitmap, 
        scaledWidth, 
        scaledHeight, 
        true
    )
    
    // Crear un bitmap para el marcador completo con espacio mínimo para el texto
    val width = scaledWidth
    val height = scaledHeight + (10 * scaleFactor).toInt()
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(resultBitmap)
    
    // Dibujar el logo escalado
    canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
    
    // Configurar el texto con tamaño muy reducido
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 10f * scaleFactor
        textAlign = Paint.Align.CENTER
    }
    
    // Dibujar un fondo para el texto
    val textBackgroundPaint = Paint().apply {
        color = Color.argb(200, 255, 255, 255)
    }
    val textWidth = paint.measureText(cityName)
    val textBackgroundRect = RectF(
        (width - textWidth) / 2 - 1,
        scaledHeight.toFloat(),
        (width + textWidth) / 2 + 1,
        height.toFloat()
    )
    canvas.drawRect(textBackgroundRect, textBackgroundPaint)
    
    // Dibujar el texto
    canvas.drawText(cityName, width / 2f, scaledHeight + (8 * scaleFactor), paint)
    
    return BitmapDescriptorFactory.fromBitmap(resultBitmap)
}