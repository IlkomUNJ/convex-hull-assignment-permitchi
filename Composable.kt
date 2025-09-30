package com.example.basiccodelabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.MutableState

@Composable
fun NewScreen(navController: NavController) {
    var currentInput by remember {mutableStateOf("") }

    Scaffold { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentInput.ifEmpty { "0" },
                fontSize = 48.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
                {
                Button(onClick = { currentInput = "" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "AC")
                }
                Button(onClick = {
                    if (currentInput.isNotEmpty()) {
                        currentInput = currentInput.dropLast(1)}
                },
                    modifier = Modifier.weight(1f)) {
                    Text(text = "<-")
                }
                Button(onClick = { },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "%")
                }
                Button(onClick = { currentInput += "/" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "/")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
            {
                Button(onClick = { currentInput += "7" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "7")
                }
                Button(onClick = { currentInput += "8" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "8")
                }
                Button(onClick = { currentInput += "9" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "9")
                }
                Button(onClick = { currentInput += "*" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "*")

                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
            {
                Button(onClick = { currentInput += "4" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "4")
                }
                Button(onClick = { currentInput += "5"},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "5")
                }
                Button(onClick = { currentInput += "6"},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "6")
                }
                Button(onClick = { currentInput += "-"
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "-")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
            {
                Button(onClick = { currentInput += "1"},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "1")
                }
                Button(onClick = { currentInput += "2" },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "2")
                }
                Button(onClick = { currentInput += "3"},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "3")
                }
                Button(onClick = { currentInput += "+"},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "+")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
            {
                Button(onClick = { currentInput += "0"},
                    modifier = Modifier.weight(2f)
                ) {
                    Text(text = "0")
                }
                Button(onClick = { currentInput += "."},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = ".")
                }
                Button(onClick = {

                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "=")
                }
            }
        }
    }
}

fun calculate(operand1: Double, operand2: Double, operation: String): String {
    return when (operation) {
        "+" -> (operand1 + operand2).toString()
        "-" -> (operand1 - operand2).toString()
        "*" -> (operand1 * operand2).toString()
        "/" -> if (operand2 != 0.0) (operand1 / operand2).toString() else "Error: Div by 0"
        "%" -> (operand1 % operand2).toString()
        else -> "Error: Invalid Op"
    }
}

fun handleOperation(
    operation: String,
    currentInputState: MutableState<String>,
    firstOperandState: MutableState<Double?>,
    pendingOperationState: MutableState<String?>
) {
    var currentInput by currentInputState
    var firstOperand by firstOperandState
    var pendingOperation by pendingOperationState

    val currentInputValue = currentInput.toDoubleOrNull()

    if (firstOperand != null && pendingOperation != null && currentInputValue != null) {
        val result = calculate(firstOperand!!, currentInputValue, pendingOperation!!)
        currentInput = result // Update the MutableState's value
        if (result.startsWith("Error")) {
            firstOperand = null
            pendingOperation = null
        } else {
            firstOperand = result.toDoubleOrNull()
            pendingOperation = operation
            currentInput = ""
        }
    } else if (currentInputValue != null) {
        firstOperand = currentInputValue
        pendingOperation = operation
        currentInput = ""
    } else if (firstOperand != null) {
        pendingOperation = operation
    }
}

fun handleEquals(
    currentInputState: MutableState<String>,
    firstOperandState: MutableState<Double?>,
    pendingOperationState: MutableState<String?>
) {
    var currentInput by currentInputState
    var firstOperand by firstOperandState
    var pendingOperation by pendingOperationState

    val secondOperand = currentInput.toDoubleOrNull()
    if (firstOperand != null && secondOperand != null && pendingOperation != null) {
        currentInput = calculate(firstOperand!!, secondOperand, pendingOperation!!)
        firstOperand = null
        pendingOperation = null
    }
}

fun handleClear(
    currentInputState: MutableState<String>,
    firstOperandState: MutableState<Double?>,
    pendingOperationState: MutableState<String?>
) {
    currentInputState.value = ""
    firstOperandState.value = null
    pendingOperationState.value = null
}

fun handleBackspace(currentInputState: MutableState<String>) {
    if (currentInputState.value.isNotEmpty()) {
        currentInputState.value = currentInputState.value.dropLast(1)
    }
}

fun handleDigitOrDecimal(entry: String, currentInputState: MutableState<String>) {
    if (entry == "." && currentInputState.value.contains(".")) {
        return
    }
    currentInputState.value += entry
}
