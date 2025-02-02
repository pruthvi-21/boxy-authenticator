package com.boxy.authenticator.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boxy.authenticator.data.models.Thumbnail
import com.boxy.authenticator.data.models.TokenEntry
import com.boxy.authenticator.data.models.otp.OtpInfo
import com.boxy.authenticator.domain.usecases.FetchTokensUseCase
import com.boxy.authenticator.helpers.TokenEntryBuilder.buildNewToken
import com.boxy.authenticator.helpers.serializers.BoxyJson
import com.boxy.authenticator.utils.AccountEntryMethod
import com.boxy.authenticator.utils.Constants
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.pickFile
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class ExportableTokenEntry(
    var issuer: String,
    var label: String = "",
    var thumbnail: Thumbnail,
    val otpInfo: OtpInfo,
) {
    fun toTokenEntry(): TokenEntry {
        return buildNewToken(
            issuer = issuer,
            label = label,
            thumbnail = thumbnail,
            otpInfo = otpInfo,
            addedFrom = AccountEntryMethod.RESTORED,
        )
    }

    companion object {
        fun fromTokenEntry(token: TokenEntry): ExportableTokenEntry {
            return ExportableTokenEntry(
                issuer = token.issuer,
                label = token.label,
                thumbnail = token.thumbnail,
                otpInfo = token.otpInfo,
            )
        }
    }
}

class TransferAccountsViewModel(
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {

    fun exportTokensToFile(onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            fetchTokensUseCase()
                .fold(
                    onSuccess = { tokens ->
                        val exportData = JsonArray(tokens.map { token ->
                            BoxyJson.encodeToJsonElement(ExportableTokenEntry.fromTokenEntry(token))
                        })

                        val prettyJsonData = BoxyJson.encodeToString(exportData)

                        val file = FileKit.saveFile(
                            baseName = buildFileName(),
                            extension = Constants.EXPORT_FILE_EXTENSION,
                            bytes = prettyJsonData.encodeToByteArray()
                        )
                        onFinished(file != null)
                    },
                    onFailure = {
                        onFinished(false)
                    }
                )
        }
    }

    private fun buildFileName(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return Constants.EXPORT_FILE_NAME_PREFIX +
                now.year +
                now.monthNumber.toString().padStart(2, '0') +
                now.dayOfMonth.toString().padStart(2, '0') +
                "_" +
                now.hour.toString().padStart(2, '0') +
                now.minute.toString().padStart(2, '0') +
                now.second.toString().padStart(2, '0')
    }

    fun importTokensFromFile(onSuccess: (List<TokenEntry>) -> Unit, onFailure: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val file = FileKit.pickFile() ?: return@launch
                val fileContent = file.readBytes().decodeToString(throwOnInvalidSequence = true)

                val tokensJson = BoxyJson.decodeFromString<List<ExportableTokenEntry>>(fileContent)

                onSuccess(tokensJson.map { it.toTokenEntry() })
            } catch (e: CharacterCodingException) {
                onFailure(e.message)
            } catch (e: SerializationException) {
                onFailure(e.message)
            } catch (e: IllegalArgumentException) {
                onFailure(e.message)
            }
        }
    }

    companion object {
        private const val TAG = "TransferAccountsViewModel"
    }
}