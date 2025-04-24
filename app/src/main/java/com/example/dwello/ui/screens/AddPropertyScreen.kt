package com.example.dwello.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dwello.data.model.PropertyPayload
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.repository.PropertyApiService
import com.example.dwello.utils.property.CloudinaryUploader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Create API service instance
    val propertyApiService = remember { PropertyApiService() }
    val sharedPrefManager = SharedPrefManager(context)
    val userProfile = sharedPrefManager.getUserProfile()
    val userEmail = userProfile?.email // In a real app, get from your auth system

    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf<String?>(null) }
    var selectedLocation by remember { mutableStateOf("") }
    var showLocationDropdown by remember { mutableStateOf(false) }

    // Image selection state
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var thumbnailUri by remember { mutableStateOf<Uri?>(null) }
    var uploadingImages by remember { mutableStateOf(false) }

    // Form validation state
    var isFormValid by remember { mutableStateOf(false) }
    var formErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Submission state
    var isSubmitting by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Available locations
    val locations = listOf("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune")

    // Image picker launcher
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            imageUris = uris
            if (thumbnailUri == null) {
                thumbnailUri = uris.first()
            }
        }
    }

    // Single image picker for thumbnail
    val thumbnailPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            thumbnailUri = it
        }
    }

    // Validate form
    LaunchedEffect(title, description, price, selectedLocation, thumbnailUri, imageUris) {
        val errors = mutableMapOf<String, String>()

        if (title.isBlank()) errors["title"] = "Title is required"
        if (description.isBlank()) errors["description"] = "Description is required"

        if (price.isBlank()) {
            errors["price"] = "Price is required"
            priceError = "Price is required"
        } else {
            try {
                val priceValue = price.toInt()
                if (priceValue <= 0) {
                    errors["price"] = "Price must be positive"
                    priceError = "Price must be positive"
                } else {
                    priceError = null
                }
            } catch (e: NumberFormatException) {
                errors["price"] = "Price must be a valid number"
                priceError = "Price must be a valid number"
            }
        }

        if (selectedLocation.isBlank()) errors["location"] = "Location is required"
        if (thumbnailUri == null) errors["thumbnail"] = "Thumbnail image is required"
        if (imageUris.size < 2) errors["images"] = "At least 2 images are required"

        formErrors = errors
        isFormValid = errors.isEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a Property") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Form Title
                Text(
                    text = "Property Details",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Form fields
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Property Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = "title" in formErrors,
                    supportingText = {
                        if ("title" in formErrors) {
                            Text(formErrors["title"] ?: "")
                        }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    isError = "description" in formErrors,
                    supportingText = {
                        if ("description" in formErrors) {
                            Text(formErrors["description"] ?: "")
                        }
                    }
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = priceError != null,
                    supportingText = {
                        priceError?.let { Text(it) }
                    },
                    singleLine = true,
                    prefix = { Text("₹") }
                )

                // Location dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedLocation,
                        onValueChange = {},
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select location",
                                modifier = Modifier.clickable { showLocationDropdown = true }
                            )
                        },
                        isError = "location" in formErrors,
                        supportingText = {
                            if ("location" in formErrors) {
                                Text(formErrors["location"] ?: "")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = showLocationDropdown,
                        onDismissRequest = { showLocationDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        locations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    selectedLocation = location
                                    showLocationDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Thumbnail section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Property Thumbnail",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (thumbnailUri != null) {
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .padding(8.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(thumbnailUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Thumbnail Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                IconButton(
                                    onClick = { thumbnailUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if ("thumbnail" in formErrors)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { thumbnailPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Add thumbnail",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Add Thumbnail",
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            if ("thumbnail" in formErrors) {
                                Text(
                                    text = formErrors["thumbnail"] ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Property images section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Property Images (min 2)",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (imageUris.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                items(imageUris) { uri ->
                                    Box(modifier = Modifier.size(120.dp)) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(uri)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Property Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )

                                        IconButton(
                                            onClick = {
                                                imageUris = imageUris.filter { it != uri }
                                                if (thumbnailUri == uri) {
                                                    thumbnailUri = imageUris.firstOrNull()
                                                }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(28.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                                    shape = CircleShape
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Remove",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { multiplePhotoPickerLauncher.launch("image/*") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.PlayArrow,
                                                contentDescription = "Add more images",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Add More",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if ("images" in formErrors)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { multiplePhotoPickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Add images",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Add Property Images",
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }

                        if ("images" in formErrors) {
                            Text(
                                text = formErrors["images"] ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Text(
                            text = "${imageUris.size}/5 images selected",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (isFormValid) {
                                try {
                                    isSubmitting = true

                                    // Upload images to Cloudinary
                                    uploadingImages = true
                                    val cloudinaryUploader = CloudinaryUploader(context)

                                    // Upload thumbnail
                                    val thumbnailUrl = cloudinaryUploader.uploadImage(thumbnailUri!!)

                                    // Upload property images
                                    val imageUrls = imageUris.map { uri ->
                                        cloudinaryUploader.uploadImage(uri)
                                    }

                                    uploadingImages = false

                                    // Create property payload
                                    val propertyPayload = PropertyPayload(
                                        title = title,
                                        description = description,
                                        price = price.toInt(),
                                        location = selectedLocation,
                                        thumbnail = thumbnailUrl,
                                        pictures = imageUrls
                                    )

                                    // Submit the property
                                    val result = userEmail?.let {
                                        propertyApiService.createProperty(propertyPayload,
                                            it
                                        )
                                    }

                                    if (result != null) {
                                        if (result.isSuccess) {
                                            showSuccessDialog = true
                                        } else {
                                            if (result != null) {
                                                submissionError = result.exceptionOrNull()?.message ?: "Unknown error"
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    submissionError = e.message ?: "An error occurred"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isSubmitting && !uploadingImages
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Property")
                    }
                }

                if (uploadingImages) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Uploading images...",
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                submissionError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Success") },
            text = { Text("Property added successfully!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigateUp()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}