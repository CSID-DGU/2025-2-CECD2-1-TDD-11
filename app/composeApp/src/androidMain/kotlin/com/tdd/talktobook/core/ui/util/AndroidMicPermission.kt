package com.tdd.talktobook.core.ui.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
actual fun rememberMicPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDeniedPermanently: () -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val activity = context as? Activity

    val micPermission = Manifest.permission.RECORD_AUDIO

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showGoToSettingsDialog by remember { mutableStateOf(false) }

    // 권한 요청 launcher
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                // 거부됨 → 다시 묻지 않기
                if (activity != null &&
                    activity.shouldShowRequestPermissionRationale(micPermission)
                ) {
                    // 단순 거부 → 한 번 더 설명
                    showRationaleDialog = true
                } else {
                    // "다시 묻지 않기"까지 눌렀거나 더 이상 요청 못 하는 상태
                    showGoToSettingsDialog = true
                    onPermissionDeniedPermanently()
                }
            }
        }

    val requestPermission: () -> Unit = {
        val act = activity
        if (act == null) {
            //
        } else {
            val granted =
                ContextCompat.checkSelfPermission(
                    act,
                    micPermission,
                ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                onPermissionGranted()
            } else {
                launcher.launch(micPermission)
            }
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("마이크 권한이 필요해요") },
            text = { Text("음성 인식을 사용하려면 마이크 접근 권한이 필요합니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        launcher.launch(micPermission)
                    },
                ) {
                    Text("다시 시도")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("취소")
                }
            },
        )
    }

    if (showGoToSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showGoToSettingsDialog = false },
            title = { Text("설정에서 마이크 권한을 켜주세요") },
            text = {
                Text(
                    "마이크 권한이 꺼져 있어 음성 기능을 사용할 수 없습니다.\n" +
                            "설정 > 앱 정보에서 권한을 직접 허용해 주세요.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showGoToSettingsDialog = false
                        openAppSettings(activity)
                    },
                ) {
                    Text("설정으로 이동")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoToSettingsDialog = false }) {
                    Text("닫기")
                }
            },
        )
    }

    return requestPermission
}

private fun openAppSettings(activity: Activity?) {
    activity ?: return
    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null),
        )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    activity.startActivity(intent)
}