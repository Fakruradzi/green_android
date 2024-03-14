package com.blockstream.compose.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blockstream.common.models.settings.DenominationExchangeRateViewModel
import com.blockstream.common.models.settings.DenominationExchangeRateViewModelAbstract
import com.blockstream.common.models.settings.DenominationExchangeRateViewModelPreview
import com.blockstream.compose.R
import com.blockstream.compose.components.GreenButton
import com.blockstream.compose.components.GreenButtonType
import com.blockstream.compose.components.GreenCard
import com.blockstream.compose.components.GreenColumn
import com.blockstream.compose.theme.GreenThemePreview
import com.blockstream.compose.theme.titleSmall
import com.blockstream.compose.utils.HandleSideEffectDialog
import com.blockstream.compose.utils.stringResourceId


@Composable
fun DenominationExchangeDialog(
    viewModel: DenominationExchangeRateViewModelAbstract,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {

        HandleSideEffectDialog(viewModel, onDismiss = {
            onDismissRequest()
        })

        GreenCard(modifier = Modifier.fillMaxWidth()) {
            GreenColumn(padding = 8, space = 16) {
                Text(
                    text = stringResource(id = R.string.id_denomination__exchange_rate),
                    style = titleSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Denomination
                var denominationExpanded by remember { mutableStateOf(false) }
                val selectedUnit by viewModel.selectedUnit.collectAsStateWithLifecycle()

                ExposedDropdownMenuBox(
                    expanded = denominationExpanded,
                    onExpandedChange = { denominationExpanded = it },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        // The `menuAnchor` modifier must be passed to the text field for correctness.
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = selectedUnit,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.id_denomination)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = denominationExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = denominationExpanded,
                        onDismissRequest = { denominationExpanded = false },
                    ) {
                        viewModel.units.forEachIndexed { index, selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    viewModel.postEvent(
                                        DenominationExchangeRateViewModel.LocalEvents.Set(
                                            unit = viewModel.units.get(index)
                                        )
                                    )
                                    denominationExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                // Exchange and Currencies
                var exchangeExpanded by remember { mutableStateOf(false) }
                val selectedExchange by viewModel.selectedExchangeAndCurrency.collectAsStateWithLifecycle()
                val exchangeAndCurrencies by viewModel.exchangeAndCurrencies.collectAsStateWithLifecycle()

                if (exchangeAndCurrencies.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = exchangeExpanded,
                        onExpandedChange = { exchangeExpanded = it },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            value = stringResourceId(selectedExchange),
                            onValueChange = {},
                            label = { Text(stringResource(R.string.id_exchange_rate)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = exchangeExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = exchangeExpanded,
                            onDismissRequest = { exchangeExpanded = false },
                        ) {
                            exchangeAndCurrencies.forEachIndexed { index, selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(stringResourceId(selectionOption)) },
                                    onClick = {
                                        viewModel.postEvent(
                                            DenominationExchangeRateViewModel.LocalEvents.Set(
                                                exchangeAndCurrency = viewModel.exchangeAndCurrencies.value[index]
                                            )
                                        )
                                        exchangeExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    GreenButton(
                        text = stringResource(id = R.string.id_cancel),
                        type = GreenButtonType.TEXT
                    ) {
                        onDismissRequest()
                    }

                    GreenButton(
                        text = stringResource(id = android.R.string.ok),
                        type = GreenButtonType.TEXT,
                    ) {
                        viewModel.postEvent(DenominationExchangeRateViewModel.LocalEvents.Save)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DenominationExchangeDialogPreview() {
    GreenThemePreview {
        DenominationExchangeDialog(
            viewModel = DenominationExchangeRateViewModelPreview.preview()
        ) {

        }
    }
}