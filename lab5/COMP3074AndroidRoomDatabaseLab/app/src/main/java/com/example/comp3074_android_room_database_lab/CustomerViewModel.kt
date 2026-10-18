package com.example.comp3074_android_room_database_lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

import com.example.comp3074_android_room_database_lab.data.Customer
import com.example.comp3074_android_room_database_lab.data.CustomerDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class CustomerViewModel(
    private val customerDao: CustomerDao
) : ViewModel() {

    val customers: Flow<List<Customer>> =
        customerDao.getAllCustomers()

    private val _filteredCustomers = MutableStateFlow<List<Customer>>(emptyList())
    val filteredCustomers: StateFlow<List<Customer>> = _filteredCustomers.asStateFlow()

    fun addCustomer(
        name: String,
        age: Int,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            customerDao.insert(
                Customer(
                    name = name,
                    age = age,
                    isActive = isActive
                )
            )
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.delete(customer)
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.update(customer)
        }
    }

    fun searchByName(searchName: String) {
        viewModelScope.launch {
            _filteredCustomers.value = customerDao.searchCustomers(searchName).first()
        }
    }
}
