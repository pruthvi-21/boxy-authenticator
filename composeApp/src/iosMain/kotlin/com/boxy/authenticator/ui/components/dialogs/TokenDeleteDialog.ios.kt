package com.boxy.authenticator.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.interop.LocalUIViewController
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.dialog_message_delete_token
import boxy_authenticator.composeapp.generated.resources.remove
import boxy_authenticator.composeapp.generated.resources.remove_account
import org.jetbrains.compose.resources.stringResource
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIViewController

@Composable
actual fun TokenDeleteDialog(
    issuer: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val title = stringResource(Res.string.remove_account)
    val message = stringResource(Res.string.dialog_message_delete_token, issuer, label)
    val destructiveActionTitle = stringResource(Res.string.remove)
    val cancelActionTitle = stringResource(Res.string.cancel)

    val currentViewController: UIViewController = LocalUIViewController.current

    val alert = UIAlertController.alertControllerWithTitle(
        title = title,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )

    val confirmAction = UIAlertAction.actionWithTitle(
        title = destructiveActionTitle,
        style = UIAlertActionStyleDestructive,
        handler = {
            alert.dismissViewControllerAnimated(true, completion = null)
            onConfirm()
        })

    val cancelAction = UIAlertAction.actionWithTitle(
        title = cancelActionTitle,
        style = UIAlertActionStyleCancel,
        handler = {
            alert.dismissViewControllerAnimated(true, completion = null)
            onDismiss()
        })

    alert.addAction(confirmAction)
    alert.addAction(cancelAction)

    currentViewController.presentViewController(alert, animated = true, completion = null)
}