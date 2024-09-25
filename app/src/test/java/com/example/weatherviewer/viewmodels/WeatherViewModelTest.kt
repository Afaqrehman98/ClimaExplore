import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherviewer.MainCoroutinesRule
import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.data.usecase.DeleteSaveCityUseCase
import com.example.weatherviewer.data.usecase.GetSaveCityUseCase
import com.example.weatherviewer.data.usecase.SaveCityUseCase
import com.example.weatherviewer.data.usecase.SearchWeatherUseCase
import com.example.weatherviewer.ui.states.WeatherUiState
import com.example.weatherviewer.ui.viewmodels.WeatherViewModel
import com.example.weatherviewer.utils.DEFAULT_WEATHER_DESTINATION
import com.example.weatherviewer.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @Mock
    private val mockSearchWeatherUseCase: SearchWeatherUseCase? = null

    @Mock
    private val mockGetSaveCityUseCase: GetSaveCityUseCase? = null

    @Mock
    private val mockSaveCityUseCase: SaveCityUseCase? = null

    @Mock
    private val mockDeleteSaveCityUseCase: DeleteSaveCityUseCase? = null

    @InjectMocks
    private var viewModel: WeatherViewModel? = null

    @Test
    fun testGetWeather_Success() {

        val expectedWeather = Weather(
            temperature = 25,
            date = "2023-09-25",
            wind = 10,
            humidity = 60,
            feelsLike = 24,
            uv = 5,
            name = "Cottbus",
            isFavourite = true
        )

        runTest {
            // setting up behaviours
            val expectedSavedCities = arrayListOf<Weather>(expectedWeather)

            `when`<Flow<Result<Weather>>>(
                mockSearchWeatherUseCase!!.getCities(DEFAULT_WEATHER_DESTINATION)
            ).thenReturn(flowOf(Result.Success(expectedWeather)))

            `when`<List<Weather>>(
                mockGetSaveCityUseCase!!.getSaveCities()
            ).thenReturn(expectedSavedCities)

            // calling component
            viewModel!!.getWeather()

            // assertion
            val actualUiState = viewModel!!.uiState.value
            Assert.assertEquals(WeatherUiState::class.java, viewModel!!.uiState.value.javaClass)
            Assert.assertEquals(expectedWeather, actualUiState.weather)
            Assert.assertEquals(expectedWeather.name, actualUiState.weather?.name)
        }
    }
}
