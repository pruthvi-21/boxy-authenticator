package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.interop.LocalUIViewController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIViewController

@Composable
actual fun PlatformAlertDialog(
    title: String?,
    message: String?,
    confirmText: String,
    dismissText: String,
    isDestructive: Boolean,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val currentViewController: UIViewController = LocalUIViewController.current

    val alert = UIAlertController.alertControllerWithTitle(
        title = title,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )

    val confirmAction = UIAlertAction.actionWithTitle(
        title = confirmText,
        style = if(isDestructive) UIAlertActionStyleDestructive else UIAlertActionStyleDefault,
        handler = {
            alert.dismissViewControllerAnimated(true, completion = null)
            onConfirmation()
        }
    )

    val cancelAction = UIAlertAction.actionWithTitle(
        title = dismissText,
        style = UIAlertActionStyleCancel,
        handler = {
            alert.dismissViewControllerAnimated(true, completion = null)
            onDismissRequest()
        }
    )

    alert.addAction(confirmAction)
    alert.addAction(cancelAction)

    currentViewController.presentViewController(alert, animated = true, completion = null)
}