package com.boxy.authenticator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.cancel
import boxy_authenticator.composeapp.generated.resources.cd_fab_add_new
import com.boxy.authenticator.utils.applyPrimaryColor
import com.boxy.authenticator.utils.toUIColor
import org.jetbrains.compose.resources.stringResource
import platform.Foundation.setValue
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIViewController

@Composable
actual fun ExpandableFab(
    isFabExpanded: Boolean,
    items: List<ExpandableFabItem>,
    onItemClick: (index: Int) -> Unit,
    onFabExpandChange: (Boolean) -> Unit,
    modifier: Modifier,
) {
    if (isFabExpanded) {
        FabOptionsSheet(
            options = items.map { it.label },
            onOptionSelected = { _, idx -> onItemClick(idx) },
            onDismiss = { onFabExpandChange(false) },
        )
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = { onFabExpandChange(!isFabExpanded) },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.cd_fab_add_new)
            )
        }
    }
}

@Composable
private fun FabOptionsSheet(
    title: String? = null,
    message: String? = null,
    options: List<String>,
    onOptionSelected: (String, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentViewController: UIViewController = LocalUIViewController.current

    val actionSheet = UIAlertController.alertControllerWithTitle(
        title = title,
        message = message,
        preferredStyle = UIAlertControllerStyleActionSheet
    )

    options.forEachIndexed { index, option ->
        val action = UIAlertAction.actionWithTitle(
            title = option,
            style = UIAlertActionStyleDefault,
            handler = {
                actionSheet.dismissViewControllerAnimated(true, completion = null)
                onOptionSelected(option, index)
            }
        )
        action.setValue(MaterialTheme.colorScheme.primary.toUIColor(), forKey = "titleTextColor")
        actionSheet.addAction(action.applyPrimaryColor())
    }

    val cancelAction = UIAlertAction.actionWithTitle(
        title = stringResource(Res.string.cancel),
        style = UIAlertActionStyleCancel,
        handler = {
            actionSheet.dismissViewControllerAnimated(true, completion = null)
            onDismiss()
        }
    )
    actionSheet.addAction(cancelAction.applyPrimaryColor())

    currentViewController.presentViewController(actionSheet, animated = true, completion = {
        onDismiss()
    })
}
