package com.blockstream.compose.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.koin.koinScreenModel
import com.blockstream.common.Parcelable
import com.blockstream.common.Parcelize
import com.blockstream.common.data.GreenWallet
import com.blockstream.common.events.Events
import com.blockstream.common.models.login.Bip39PassphraseViewModel
import com.blockstream.common.models.login.Bip39PassphraseViewModelAbstract
import com.blockstream.common.models.login.Bip39PassphraseViewModelPreview
import com.blockstream.compose.GreenPreview
import com.blockstream.compose.R
import com.blockstream.compose.components.GreenBottomSheet
import com.blockstream.compose.components.GreenButton
import com.blockstream.compose.components.GreenButtonType
import com.blockstream.compose.components.GreenColumn
import com.blockstream.compose.components.GreenRow
import com.blockstream.compose.components.LearnMoreButton
import com.blockstream.compose.extensions.onValueChange
import com.blockstream.compose.navigation.resultKey
import com.blockstream.compose.navigation.setNavigationResult
import com.blockstream.compose.theme.titleSmall
import com.blockstream.compose.utils.HandleSideEffect
import com.blockstream.compose.utils.TextInputPassword
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Parcelize
data class Bip39PassphraseBottomSheet(
    val greenWallet: GreenWallet,
    val passphrase: String
) : BottomScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<Bip39PassphraseViewModel> {
            parametersOf(greenWallet, passphrase)
        }

        Bip39PassphraseBottomSheet(
            viewModel = viewModel,
            sheetState = sheetState(skipPartiallyExpanded = true),
            setBip39Passphrase = { setNavigationResult(resultKey, it) },
            onDismissRequest = onDismissRequest()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bip39PassphraseBottomSheet(
    viewModel: Bip39PassphraseViewModelAbstract,
    sheetState: SheetState = rememberModalBottomSheetState(),
    setBip39Passphrase: (passphrase: String) -> Unit = { },
    onDismissRequest: () -> Unit,
) {

    HandleSideEffect(viewModel = viewModel) {
        if (it is Bip39PassphraseViewModel.LocalSideEffects.SetBip39Passphrase) {
            setBip39Passphrase(it.passphrase)
        }
    }

    GreenBottomSheet(
        title = stringResource(id = R.string.id_login_with_bip39_passphrase),
        viewModel = viewModel,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {

        val passphrase by viewModel.passphrase.collectAsStateWithLifecycle()
        val passwordVisibility = remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        TextField(
            value = passphrase,
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            onValueChange = viewModel.passphrase.onValueChange(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            label = { Text(stringResource(id = R.string.id_passphrase)) },
            supportingText = {
                Text(
                    text = "${passphrase.length} / 100",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            },
            trailingIcon = {
                TextInputPassword(passwordVisibility)
            }
        )

        Column {
            Text(text = stringResource(R.string.id_different_passphrases_generate))

            LearnMoreButton {
                viewModel.postEvent(Bip39PassphraseViewModel.LocalEvents.LearnMore)
            }
        }

        GreenColumn(padding = 0, space = 8) {
            Text(text = stringResource(R.string.id_always_ask), style = titleSmall)

            val isAlwaysAsk by viewModel.isAlwaysAsk.collectAsStateWithLifecycle()

            GreenRow(padding = 0, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.id_you_will_be_asked_to_enter_your),
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = isAlwaysAsk,
                    onCheckedChange = viewModel.isAlwaysAsk.onValueChange(),
                )
            }
        }

        GreenRow(padding = 0, modifier = Modifier.fillMaxWidth()) {
            GreenButton(
                text = stringResource(id = R.string.id_clear),
                modifier = Modifier.weight(1f),
                type = GreenButtonType.OUTLINE
            ) {
                viewModel.postEvent(Bip39PassphraseViewModel.LocalEvents.Clear)

            }

            GreenButton(
                text = stringResource(id = android.R.string.ok),
                modifier = Modifier.weight(1f)
            ) {
                viewModel.postEvent(Events.Continue)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Bip39PassphraseSheetPreview() {
    GreenPreview {
        Bip39PassphraseBottomSheet(
            viewModel = Bip39PassphraseViewModelPreview.preview(),
            onDismissRequest = { }
        )
    }
}