package com.fergdev.hagah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal abstract class FViewModel<State, Action>(
    initialState: State,
    internal val dispatcher: CoroutineDispatcher
) : ViewModel() {
    inline fun launch(crossinline block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(dispatcher) { block() }

    private val _state = MutableStateFlow(initialState)
    internal fun updateState(block: State.() -> State) = _state.update(block)
    val state: StateFlow<State> = _state

    private val _actions = MutableSharedFlow<Action>()
    internal fun emitAction(action: Action) = launch { _actions.emit(action) }
    val actions: Flow<Action> = _actions
}
