# Metropolitan Museum App 🎨

Una aplicació Android desenvolupada amb **Kotlin** i **Jetpack Compose** que permet explorar la col·lecció del Metropolitan Museum of Art de Nova York.

![Metropolitan Museum Logo](https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/The_Metropolitan_Museum_of_Art_Logo.svg/320px-The_Metropolitan_Museum_of_Art_Logo.svg.png)

## 📱 Funcionalitats

### 🏠 Pantalla d'Inici
- Logo del Metropolitan Museum estilitzat
- Missatge de benvinguda multiidioma
- Animacions Lottie de benvinguda
- Disseny responsive per tauletes i mòbils

### 🗺️ Cerca per Mapa
- **Google Maps** integrat amb marcadors personalitzats
- Capitals europees amb obres del museu:
  - Alemanya (Berlin), Espanya (Madrid), França (Paris)
  - Itàlia (Roma), Països Baixos (Amsterdam), Bèlgica (Brussels)
  - Àustria (Vienna), Suïssa (Bern), Suècia (Stockholm)
- Informació detallada de cada obra al fer clic
- Bottom Sheet amb llistat d'obres ordenades per autor

### 🔍 Cerca per Text
- Cerca per paraules clau
- Filtratge per departament del museu
- Resultats amb imatges d'alta qualitat
- Navegació a la fitxa completa de cada obra

### 📋 Detalls d'Obra
- Imatge d'alta resolució
- Informació completa: autor, títol, data, medi, dimensions
- Enllaç a la pàgina oficial del museu
- Gestió d'errors si no hi ha connexió

### 🎬 Crèdits
- Informació del desenvolupador
- **Reproductor de vídeo** del Metropolitan Museum amb controls
- Enllaç oficial al museu
- Animacions i efectes visuals

## 🛠️ Tecnologies Utilitzades

### Framework i Arquitectura
- **Kotlin** - Llenguatge de programació
- **Jetpack Compose** - UI moderna declarativa
- **MVVM** - Arquitectura Model-View-ViewModel
- **Material Design 3** - Sistema de disseny

### APIs i Dades
- **Metropolitan Museum API** - Font de dades oficial
- **Google Maps API** - Integració de mapes
- **Retrofit** + **OkHttp** - Comunicació HTTP
- **Kotlinx Serialization** - Serialització JSON

### Multimèdia
- **Coil** - Càrrega d'imatges amb cache
- **Lottie** - Animacions vectorials
- **ExoPlayer** - Reproducció de vídeo
- **Google Maps Compose** - Components de mapa

### Altre
- **Navigation Compose** - Navegació entre pantalles
- **StateFlow** + **Coroutines** - Gestió d'estat reactiu
- **Material 3 Dynamic Colors** - Colors adaptatitus del sistema

## 🎨 Disseny

### Paleta de Colors
- **Primari**: Vermell Metropolitan (#DC143C)
- **Secundari**: Daurat (#FFD700)
- **Suport**: Tema clar/fosc automàtic
- **Responsive**: Adaptació a tauletes i mòbils

### Característiques UI/UX
- ✅ **Responsive Design** - S'adapta a diferents mides de pantalla
- ✅ **Mode Fosc/Clar** - Canvi automàtic segons preferències del sistema
- ✅ **Multiidioma** - Preparada per traduccions (strings.xml)
- ✅ **Accessibilitat** - Content descriptions i navegació per teclat
- ✅ **Animacions** - Transicions fluides i Lottie animations

## 🚀 Instal·lació i Execució

### Prerequisits
- Android Studio Hedgehog (2023.1.1) o superior
- Android SDK 28 o superior
- Kotlin 1.9.0 o superior

### Passos
1. **Clona el repositori**
   ```bash
   git clone https://github.com/username/Metropolitan.git
   cd Metropolitan
   ```

2. **Configura la clau de Google Maps**
   - La clau ja està configurada al `build.gradle.kts`:
   ```kotlin
   manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyB0-vnHBgiZ3zaO2tsneH7hUkxITalOI6s"
   ```

3. **Sincronitza el projecte**
   - Obre Android Studio
   - File → Open → Selecciona la carpeta del projecte
   - Deixa que Android Studio sincronitzi automàticament

4. **Executa l'aplicació**
   - Connecta un dispositiu Android o inicia un emulador
   - Clica "Run" o Shift+F10

## 📁 Estructura del Projecte

```
app/src/main/java/cat/dam/mamadou/metropolitan/
├── data/
│   ├── api/           # Retrofit services
│   ├── model/         # Data classes i models
│   ├── repository/    # Repository pattern
│   └── local/         # SharedPreferences
├── ui/
│   ├── screen/        # Pantalles Compose
│   ├── viewmodel/     # ViewModels MVVM
│   ├── navigation/    # Navegació
│   ├── theme/         # Colors, tipografia
│   └── components/    # Components reutilitzables
├── utils/             # Utilities i extensions
└── MainActivity.kt    # Activitat principal
```

## 🔧 APIs Utilitzades

### Metropolitan Museum API
- **Base URL**: `https://collectionapi.metmuseum.org/public/collection/v1/`
- **Endpoints**:
  - `GET /departments` - Llistat de departaments
  - `GET /search` - Cerca d'obres
  - `GET /objects/{id}` - Detalls d'una obra

### Google Maps API
- **Clau**: Configurada automàticament
- **Funcions**: Mapa interactiu, marcadors personalitzats

## 📱 Captures de Pantalla

### Pantalla d'Inici
Logo estilitzat del Metropolitan Museum amb animació de benvinguda i targeta informativa.

<img src="docs/screenshots/home_screen.png" alt="Pantalla d'Inici" width="300"/>

### Pantalla de Crèdits
Informació del desenvolupador amb reproductor de vídeo del Metropolitan Museum integrat.

<img src="docs/screenshots/credits_screen.png" alt="Pantalla de Crèdits" width="300"/>

### Cerca per Text
Camp de cerca amb selector de departament i llistat d'obres amb imatges d'alta qualitat.

<img src="docs/screenshots/search_screen.png" alt="Cerca per Text" width="300"/>

### Mapa Interactiu
Marcadors a capitals europees amb bottom sheet mostrant obres d'art de cada país.

<img src="docs/screenshots/map_screen.png" alt="Mapa Interactiu" width="300"/>

## 🔄 Estats de l'Aplicació

- ✅ **Loading**: Animacions Lottie durant la càrrega
- ✅ **Success**: Mostrar dades correctament
- ✅ **Error**: Gestió d'errors amb missatges clars
- ✅ **No Connection**: Avisos quan no hi ha internet
- ✅ **Empty**: Animacions quan no hi ha resultats

## 👨‍💻 Desenvolupador

**Mamadou**  
Estudiant de Desenvolupament d'Aplicacions Multiplataforma  
Institut Escola del Treball  

## 🏛️ Crèdits

- **Metropolitan Museum of Art** - Font de dades i inspiració
- **Google Maps** - Servei de mapes
- **Material Design** - Sistema de disseny
- **Jetpack Compose** - Framework UI

## 📄 Llicència

Aquest projecte és una aplicació educativa desenvolupada per a l'assignatura de Desenvolupament d'Aplicacions Multiplataforma.

---

### 🔗 Enllaços Útils

- [Metropolitan Museum](https://www.metmuseum.org/)
- [Metropolitan API Documentation](https://metmuseum.github.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)

---

**Nota**: Assegura't de tenir connexió a internet per utilitzar totes les funcionalitats de l'aplicació. 📶