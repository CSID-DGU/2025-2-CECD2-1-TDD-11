package com.tdd.talktobook.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUserDomainMask
import platform.Foundation.downloadTaskWithURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.UIKit.UIDocumentInteractionControllerDelegateProtocol


@OptIn(ExperimentalForeignApi::class)
private class IoSPdfDownloader(
    private val rootViewControllerProvider: () -> UIViewController?
) : PdfDownloader {

    override fun download(url: String, suggestedFileName: String?) {
        val nsUrl = NSURL.URLWithString(url) ?: return

        val session = NSURLSession.sharedSession
        val task = session.downloadTaskWithURL(nsUrl) { location, response, error ->

            if (location == null || error != null) {
                println("PDF download error: $error")
                return@downloadTaskWithURL
            }

            val fileManager = NSFileManager.defaultManager
            val urls = fileManager.URLsForDirectory(
                directory = NSDocumentDirectory,
                inDomains = NSUserDomainMask
            )

            val docsDir = (urls[0] as? NSURL) ?: return@downloadTaskWithURL

            val fileName =
                suggestedFileName
                    ?: (response?.suggestedFilename ?: "대화로책 자서전.pdf")

            val destUrl = docsDir.URLByAppendingPathComponent(fileName)
                ?: return@downloadTaskWithURL

            runCatching {
                destUrl.path?.let { path ->
                    if (fileManager.fileExistsAtPath(path)) {
                        fileManager.removeItemAtURL(destUrl, null)
                    }
                }
            }

            runCatching {
                fileManager.moveItemAtURL(location, destUrl, null)
            }.onFailure {
                println("Move file failed: $it")
                return@downloadTaskWithURL
            }

            dispatch_async(dispatch_get_main_queue()) {
                rootViewControllerProvider()?.let { vc ->
                    val docController =
                        UIDocumentInteractionController.interactionControllerWithURL(destUrl)
                    docController.delegate =
                        object : NSObject(), UIDocumentInteractionControllerDelegateProtocol {
                            override fun documentInteractionControllerViewControllerForPreview(
                                controller: UIDocumentInteractionController
                            ): UIViewController = vc
                        }

                    docController.presentPreviewAnimated(true)
                }
            }

            println("PDF saved at: ${destUrl.path}")
        }

        task.resume()
    }
}

@Composable
actual fun rememberPdfDownloader(): PdfDownloader {

    return remember {
        IoSPdfDownloader(
            rootViewControllerProvider = { UIApplication.sharedApplication.keyWindow?.rootViewController }
        )
    }
}