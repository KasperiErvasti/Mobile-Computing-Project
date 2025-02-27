package com.example.mobile_computing_project

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.Bitmap
import coil3.compose.AsyncImage
import com.example.mobile_computing_project.camera.CameraPreview
import com.example.mobile_computing_project.camera.PhotoBottomSheetContent
import com.example.mobile_computing_project.sensor.NotificationHelper
import java.io.File
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToChat: () -> Unit,
    notificationHelper: NotificationHelper
) {
    val context = LocalContext.current
    val resolver = context.contentResolver

    val file = File(context.filesDir, "profile_picture")
    val usernameFile = File(context.filesDir, "username")


    var filePath by remember {
        mutableStateOf(
            file.toURI().toString() + "?timestamp=${System.currentTimeMillis()}"
        )
    }

    Column {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val iconSpacerSize = 48.dp // Used to center Profile Text to center

            IconButton(
                onClick = onNavigateToChat,
                modifier = Modifier.size(iconSpacerSize)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back to Chat",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = "Profile",
                fontSize = 40.sp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(iconSpacerSize))
        }


        val pickMedia = pickMedia(resolver, file) {
            filePath = file.absolutePath + "?timestamp=${System.currentTimeMillis()}"
        }


        Column(modifier = Modifier.padding(8.dp)) {

            AsyncImage(
                model = filePath,
                contentDescription = "profile picture",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .size(100.dp)
                    .clickable {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.size(8.dp))

            var text by remember {
                mutableStateOf(
                    usernameFile.readBytes().decodeToString()
                )
            }

            TextField(
                value = text,
                onValueChange = {
                    usernameFile.writeBytes(it.toByteArray())
                    text = it
                },
                label = { Text("Username") },
                singleLine = true
            )

            Spacer(modifier = Modifier.size(20.dp))

            val requestPermissionLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        notificationHelper.createNotification(
                            "Notifications Enabled",
                            "You will be notified when the device senses over 10000 lux"
                        )
                    }
                }

            Button(onClick = {
                notificationHelper.requestNotificationPermissions(
                    requestPermissionLauncher
                )

            }) {
                Text("Enable notifications")
            }

            val scope = rememberCoroutineScope()
            val scaffoldState = rememberBottomSheetScaffoldState()

            val cameraController = remember {
                LifecycleCameraController(context).apply {
                    setEnabledUseCases(
                        CameraController.IMAGE_CAPTURE or
                                CameraController.VIDEO_CAPTURE
                    )
                }
            }

            val hasCameraPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )


            var flashEffect by remember { mutableStateOf(false) }

            val viewModel = viewModel<MainViewModel>()
            val bitmaps by viewModel.bitmaps.collectAsState()

            Spacer(modifier = Modifier.size(8.dp))

            if (hasCameraPermission) {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetContent = {
                        PhotoBottomSheetContent(
                            bitmaps = bitmaps,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        CameraPreview(
                            controller = cameraController,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        // Flash effect to notice taking photo
                        if (flashEffect) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent black
                            )
                        }


                        IconButton(
                            onClick = {
                                switchCamera(cameraController)
                            },
                            modifier = Modifier
                                .offset(16.dp, 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch Camera"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Photo,
                                    "Open Gallery"
                                )
                            }

                            IconButton(
                                onClick = {
                                    takePhoto(
                                        cameraController = cameraController,
                                        onPhotoTaken = viewModel::onTakePhoto,
                                        context = context,
                                        triggerFlash = {
                                            flashEffect = true

                                            scope.launch {
                                                delay(100)

                                                flashEffect = false
                                            }

                                        }
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    "Take photo"
                                )
                            }


                        }
                    }

                }
            } else {
                Button(onClick = {
                    val activity = context as? Activity
                    // Might not work if this permissions has never been requested before.
                    // Implement in different way if permissions are not requested on app launch
                    val isPermanentlyDeclined = activity?.let {
                        !shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
                    } ?: false

                    if (isPermanentlyDeclined) {
                        // Open app settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } else {
                        cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                    }

                }) {
                    Text("Enable Camera")
                }
            }


        }
    }
}

private fun takePhoto(
    cameraController: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context,
    triggerFlash: () -> Unit
) {
    cameraController.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())

                    // Image is mirrored when taken with front camera
                    if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        postScale(-1f, 1f) // mirror image
                    }

                }

                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true

                )

                onPhotoTaken(rotatedBitmap)
                image.close()

                triggerFlash()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo due to exception: ", exception)
            }
        }
    )
}

private fun switchCamera(cameraController: LifecycleCameraController) {

    cameraController.cameraSelector =
        if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
}

@Composable
private fun pickMedia(
    resolver: ContentResolver,
    file: File,
    onImagePicked: () -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                resolver.openInputStream(uri).use { stream ->
                    val outputStream = file.outputStream()
                    stream?.copyTo(outputStream)

                    outputStream.close()
                    stream?.close()
                }

                onImagePicked()

            }
        }

    return pickMedia
}


