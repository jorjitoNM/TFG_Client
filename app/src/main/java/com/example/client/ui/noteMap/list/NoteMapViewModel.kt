package com.example.client.ui.noteMap.list

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.model.NoteDTO
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.note.NoteType
import com.example.client.domain.usecases.map.LoadSelectedNoteImagesUseCase
import com.example.client.domain.usecases.note.GetNoteSearchUseCase
import com.example.client.domain.usecases.note.GetNotesUseCase
import com.example.client.domain.usecases.note.OrderNoteByTypUseCase
import com.example.client.ui.common.UiEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class NoteMapViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getNoteSearchUseCase: GetNoteSearchUseCase,
    private val orderNoteByTypUseCase: OrderNoteByTypUseCase,
    private val application: Application,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val loadSelectedNoteImagesUseCase: LoadSelectedNoteImagesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteMapState())
    val uiState = _uiState.asStateFlow()
    private var locationReceiver: BroadcastReceiver? = null

    private val _selectedNotes = mutableStateListOf<NoteDTO>()
    val selectedNotes: List<NoteDTO> = _selectedNotes

    private val _selectedLocation = mutableStateOf<LatLng?>(null)
    val selectedLocation: LatLng? get() = _selectedLocation.value
    private val _bottomSheetState = MutableStateFlow(SheetValue.Hidden)
    val bottomSheetState = _bottomSheetState.asStateFlow()
    private val _isBottomSheetExpanded = mutableStateOf(false)
    val isBottomSheetExpanded: Boolean get() = _isBottomSheetExpanded.value
    // Estado del scroll
    val bottomSheetScrollState = LazyListState()

    fun setBottomSheetExpanded(expanded: Boolean) {
        _isBottomSheetExpanded.value = expanded
    }

    fun setSelectedNotes(notes: List<NoteDTO>) {
        _selectedNotes.clear()
        _selectedNotes.addAll(notes)
    }

    fun setSelectedLocation(location: LatLng?) {
        _selectedLocation.value = location
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    fun handleEvent(event: NoteMapEvent) {
        when (event) {
            is NoteMapEvent.GetNotes -> getNotes()
            is NoteMapEvent.AvisoVisto -> avisoVisto()

            is NoteMapEvent.GetCurrentLocation -> getCurrentLocation()
            is NoteMapEvent.CheckLocationPermission -> checkLocationPermission()
            is NoteMapEvent.SearchNote -> searchNote(event.query)
            is NoteMapEvent.SaveCameraPosition -> saveCameraPosition(event.latLng, event.zoom)
            is NoteMapEvent.UpdateSelectedType -> updateSelectedType(event.noteType)
            is NoteMapEvent.UpdateSearchText -> updateSearchText(event.text)
            is NoteMapEvent.FilterByType -> filterByType(event.noteType)
            is NoteMapEvent.NavigateToSearch -> {
                _uiState.update { it.copy(aviso = UiEvent.PopBackStack) }
            }

            is NoteMapEvent.SelectedNote -> selectNote(event.noteId)
            is NoteMapEvent.GetSelectedNotesImages -> getSelectedNotesImages(event.selectedNotes)
        }
    }

    private fun getSelectedNotesImages(selectedNotes : List<NoteDTO> ) {
        viewModelScope.launch(dispatcher) {
            loadSelectedNoteImagesUseCase.invoke(selectedNotes)
                .collect { result ->
                    when (result) {
                        is NetworkResult.Error -> _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message),
                                isLoading = false
                            )
                        }

                        is NetworkResult.Loading -> _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }

                        is NetworkResult.Success -> parseImagesIntoNotes(result.data)
                    }
                }
        }
    }

    private fun parseImagesIntoNotes (selectedNotesWithImages : List<NoteDTO>) {
        val updatedNotes = ArrayList<NoteDTO>()
        for (note : NoteDTO in _uiState.value.notes) {
            if (selectedNotesWithImages.map { it.id }.contains(note.id))
                selectedNotesWithImages.find { it.id == note.id }?.let { updatedNotes.add(it) }
            else updatedNotes.add(note)
        }
        _uiState.update {
            it.copy(
                notes =  updatedNotes,
                isLoading = false
            )
        }
    }

    fun registerLocationReceiver() {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                checkLocationEnabled()
            }
        }
        application.registerReceiver(locationReceiver, filter)
    }

    fun unregisterLocationReceiver() {
        locationReceiver?.let {
            application.unregisterReceiver(it)
            locationReceiver = null
        }
    }

    private fun filterByType(noteType: NoteType?) {
        _uiState.update { it.copy(isLoading = true) }
        if (noteType == null) {
            viewModelScope.launch {
                when (val result = getNotesUseCase()) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                notes = result.data,
                                selectedType = null,
                                isLoading = false
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                                isLoading = false
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        } else {
            viewModelScope.launch {
                when (val result = orderNoteByTypUseCase(noteType)) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(notes = result.data, selectedType = noteType, isLoading = false)
                        }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                                isLoading = false
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }


    private fun updateSearchText(text: String) {
        _uiState.update { it.copy(currentSearch = text) }
    }

    private fun updateSelectedType(noteType: NoteType?) {
        _uiState.update { it.copy(selectedType = noteType) }
    }

    private fun saveCameraPosition(latLng: LatLng, zoom: Float) {
        _uiState.update { it.copy(cameraLatLng = latLng, cameraZoom = zoom) }
    }

    private fun searchNote(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNoteSearchUseCase(query)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(notes = result.data, isLoading = false)
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNotesUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(notes = result.data, isLoading = false)
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                            isLoading = false
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun checkLocationPermission() {
        val hasPermission =
            ActivityCompat.checkSelfPermission(
                application,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        application,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) ==
                    PackageManager.PERMISSION_GRANTED

        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            if (
                ActivityCompat.checkSelfPermission(
                    application,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    application,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { location ->
                        location?.let {
                            _uiState.update { state ->
                                state.copy(currentLocation = location)
                            }
                        }
                    }
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            _uiState.update { state ->
                                state.copy(currentLocation = location)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun selectNote(id: Int) {
        _uiState.update {
            it.copy(
                noteSelectedId = id,
                aviso = UiEvent.PopBackStack
            )
        }
    }

    fun checkLocationEnabled() {
        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        _uiState.update { it.copy(isLocationEnabled = isEnabled) }
    }

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}