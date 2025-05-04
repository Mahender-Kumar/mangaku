////package com.example.mangaku.detection
////
////
////import android.content.Context
////import androidx.camera.core.CameraSelector
////import androidx.camera.core.Preview
////import androidx.camera.lifecycle.ProcessCameraProvider
////import androidx.camera.view.PreviewView
////import androidx.compose.foundation.layout.fillMaxSize
////import androidx.compose.runtime.Composable
////import androidx.compose.runtime.LaunchedEffect
////import androidx.compose.runtime.remember
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.platform.LocalContext
////import androidx.compose.ui.platform.LocalLifecycleOwner
////import androidx.compose.ui.viewinterop.AndroidView
////import androidx.core.content.ContextCompat
////import kotlin.coroutines.resume
////import kotlin.coroutines.suspendCoroutine
////
////@Composable
////fun CameraPreviewScreen() {
////    val lensFacing = CameraSelector.LENS_FACING_BACK
////    val lifecycleOwner = LocalLifecycleOwner.current
////    val context = LocalContext.current
////    val preview = Preview.Builder().build()
////    val previewView = remember {
////        PreviewView(context)
////    }
////    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
////    LaunchedEffect(lensFacing) {
////        val cameraProvider = context.getCameraProvider()
////        cameraProvider.unbindAll()
////        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview)
////        preview.setSurfaceProvider(previewView.surfaceProvider)
////    }
////    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
////}
////
////private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
////    suspendCoroutine { continuation ->
////        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
////            cameraProvider.addListener({
////                continuation.resume(cameraProvider.get())
////            }, ContextCompat.getMainExecutor(this))
////        }
////    }
//
//
//package com.example.mangaku.detection
//
//import android.content.Context
//import android.view.ViewGroup
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.OnLifecycleEvent
//import kotlinx.coroutines.delay
//import kotlin.coroutines.resume
//import kotlin.coroutines.suspendCoroutine
//
////@Composable
////fun CameraPreviewScreen() {
////    val lifecycleOwner = LocalLifecycleOwner.current
////    val context = LocalContext.current
////    val preview = remember { Preview.Builder().build() }
////    val previewView = remember {
////        PreviewView(context).apply {
////            layoutParams = ViewGroup.LayoutParams(
////                ViewGroup.LayoutParams.MATCH_PARENT,
////                ViewGroup.LayoutParams.MATCH_PARENT
////            )
////        }
////    }
////
////    val cameraSelector = CameraSelector.Builder()
////        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
////        .build()
////
////    LaunchedEffect(Unit) {
////        delay(100) // Optional delay to ensure lifecycle is ready
////        val cameraProvider = context.getCameraProvider()
////        cameraProvider.unbindAll()
////        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
////        preview.setSurfaceProvider(previewView.surfaceProvider)
////    }
////
////    AndroidView(
////        factory = { previewView },
////        modifier = Modifier.fillMaxSize()
////    )
////}
////
//private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
//    suspendCoroutine { continuation ->
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener(
//            { continuation.resume(cameraProviderFuture.get()) },
//            ContextCompat.getMainExecutor(this)
//        )
//    }
//
//
//@Composable
//fun CameraPreviewScreen() {
//    val lensFacing = CameraSelector.LENS_FACING_BACK
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val context = LocalContext.current
//    val preview = Preview.Builder().build()
//    val previewView = remember {
//        PreviewView(context)
//    }
//    val overlayView = remember {
//        OverlayView(context, null)
//    }
//    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//
//    OnLifecycleEvent { owner, event ->
//        when (event) {
//            ON_RESUME -> owner.lifecycleScope.launch { doOnResume() }
//            ON_PAUSE -> owner.lifecycleScope.launch { doOnPause() }
//            else -> {}
//        }
//    }
//    LaunchedEffect(lensFacing) {
//        val cameraProvider = context.getCameraProvider()
//        cameraProvider.unbindAll()
//        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//
//        integrateFaceDetector(lifecycleOwner)
//
//        withContext(Dispatchers.Default) {
//            if (faceDetectorHelper.isClosed()) {
//                faceDetectorHelper.setupFaceDetector()
//            }
//        }
//    }
//
//    this.previewView = previewView.apply {
//        scaleType = PreviewView.ScaleType.FIT_START
//    }
//    this.overlayView = overlayView
//
//    Box(modifier = Modifier.aspectRatio(0.75f), contentAlignment = Alignment.Center) {
//        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
//        AndroidView(factory = { overlayView }, modifier = Modifier.fillMaxSize())
//    }
//}
//
