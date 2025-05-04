package com.example.mangaku



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangaku.core.data.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    db: AppDatabase
) : ViewModel() {

    private val userDao = db.userDao()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userDao.getSignedInUser()
            _startDestination.value = if (user != null) "home" else "sign_in"
        }
    }
}
