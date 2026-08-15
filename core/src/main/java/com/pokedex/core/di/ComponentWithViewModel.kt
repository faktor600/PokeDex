package com.pokedex.core.di

interface ComponentWithViewModel<VM> {
    fun viewModel(): VM
}