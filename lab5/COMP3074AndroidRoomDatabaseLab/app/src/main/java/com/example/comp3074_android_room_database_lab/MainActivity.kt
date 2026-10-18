package com.example.comp3074_android_room_database_lab

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.comp3074_android_room_database_lab.data.AppDatabase
import com.example.comp3074_android_room_database_lab.data.Customer
import java.util.Optional

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MaterialTheme {
                val viewModel: CustomerViewModel = viewModel(
                    factory = CustomerViewModelFactory(
                        database.customerDao()
                    )
                )

                CustomerApp(viewModel)
            }
        }
    }
}

@Composable
fun CustomerApp(
    viewModel: CustomerViewModel
) {
    val customers by viewModel.customers
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val filteredCustomers by viewModel.filteredCustomers
        .collectAsStateWithLifecycle(initialValue = emptyList())

    CustomerScreen(
        customers = customers,
        filteredCustomers = filteredCustomers,
        onAddCustomer = viewModel::addCustomer,
        onDeleteCustomer = viewModel::deleteCustomer,
        onUpdateCustomer = viewModel::updateCustomer,
        onSearchCustomer = viewModel::searchByName
    )
}

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    filteredCustomers: List<Customer>,
    onAddCustomer: (String, Int, Boolean) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    onUpdateCustomer: (Customer) -> Unit,
    onSearchCustomer: (String) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    var name by rememberSaveable { mutableStateOf("") }
    var ageText by rememberSaveable { mutableStateOf("") }

    var searchString by remember { mutableStateOf("") }
    var isSearch by remember { mutableStateOf(false) }

    var isActive by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Room Customer Demo",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Customer name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ageText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    ageText = newValue
                }
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = searchString,
            onValueChange = { searchString = it },
            label = { Text("Filter by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Active customer")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
        }

        Button(
            onClick = {
                val age = ageText.toIntOrNull()

                if (name.isBlank() || age == null) {
                    message = "Enter a valid name and age."
                } else {
                    onAddCustomer(
                        name.trim(),
                        age,
                        isActive
                    )

                    name = ""
                    ageText = ""
                    isActive = false
                    message = "Customer submitted."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Customer")
        }

        selectedCustomer?.let { selected ->
            Button(
                onClick = {
                    val age = ageText.toIntOrNull()

                    if (name.isBlank() || age == null) {
                        message = "Enter a valid name and age."
                    } else {
                        onUpdateCustomer(
                            selected.copy(
                                name = name.trim(),
                                age = age,
                                isActive = isActive
                            )
                        )
                        if (isSearch) onSearchCustomer(searchString)

                        selectedCustomer = null
                        name = ""
                        ageText = ""
                        isActive = false
                        message = "Customer updated."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Customer")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                onSearchCustomer(searchString)
                isSearch = true
            }, modifier = Modifier.weight(1f)) { Text("Search") }
            Button(onClick = {
                isSearch = false
                searchString = ""
            }, modifier = Modifier.weight(1f)) { Text("Clear") }
        }

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(it)
        }

        Spacer(Modifier.height(12.dp))

        CustomerList(
            customers = customers,
            selectedId = selectedCustomer?.id,
            onSelectCustomer = { customer ->
                if (customer.id == selectedCustomer?.id) {
                    selectedCustomer = null
                    name = ""
                    ageText = ""
                    isActive = false
                } else {
                    selectedCustomer = customer
                    name = customer.name
                    ageText = customer.age.toString()
                    isActive = customer.isActive
                }
            },
            onDeleteCustomer = {
                if (it.id == selectedCustomer?.id) selectedCustomer = null
                onDeleteCustomer(it)
            },
            modifier = Modifier.weight(1f),
            filteredCustomers = filteredCustomers,
            isSearch = isSearch,
        )
    }
}

@Composable
fun CustomerList(
    customers: List<Customer>,
    selectedId: Int?,
    onSelectCustomer: (Customer) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    modifier: Modifier = Modifier,
    filteredCustomers: List<Customer>,
    isSearch: Boolean
) {
    val customersToShow = if (isSearch) filteredCustomers else customers

    if (customersToShow.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("No customers in the database.")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = customersToShow,
                key = { customer -> customer.id }
            ) {
                customer ->
                CustomerRow(
                    customer = customer,
                    onDelete = {
                        onDeleteCustomer(customer)
                    },
                    isSelected = customer.id == selectedId,
                    onSelected = { onSelectCustomer(customer) },
                )
            }
        }
    }
}


@Composable
fun CustomerRow(
    customer: Customer,
    onDelete: () -> Unit,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth().selectable(
            selected = isSelected,
            onClick = onSelected
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("ID: ${customer.id}")
                Text("Age: ${customer.age}")
                Text(
                    if (customer.isActive)
                        "Active customer"
                    else
                        "Inactive customer"
                )
            }

            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
