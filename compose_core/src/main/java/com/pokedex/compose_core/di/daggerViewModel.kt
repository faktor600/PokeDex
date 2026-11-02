package com.pokedex.compose_core.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.pokedex.core.di.ComponentFactoryMapOwner

@Composable
inline fun <reified T : ViewModel> daggerViewModel(
    key: String? = null,
): T {
    val componentsMap = (LocalContext.current.applicationContext as? ComponentFactoryMapOwner)
        ?.componentFactories
        ?: error("Application must implement ComponentFactoryMapOwner")

    return viewModel(
        modelClass = T::class.java,
        key = key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return (componentsMap[modelClass]?.create()?.viewModel() as? T) ?: error("No ${modelClass.name} found in map dependencies")
            }
        }
    )
}