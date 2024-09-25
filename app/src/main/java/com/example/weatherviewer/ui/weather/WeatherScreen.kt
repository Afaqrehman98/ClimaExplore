package com.example.weatherviewer.ui.weather

import FavouriteComponent
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weatherviewer.R
import com.example.weatherviewer.data.models.Forecast
import com.example.weatherviewer.data.models.ForecastResponse
import com.example.weatherviewer.data.models.Hour
import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.ui.components.Animation
import com.example.weatherviewer.ui.components.WeatherPredictionComponent
import com.example.weatherviewer.ui.states.SearchWidgetState
import com.example.weatherviewer.ui.states.WeatherUiState
import com.example.weatherviewer.ui.theme.WeatherViewerTheme
import com.example.weatherviewer.ui.viewmodels.WeatherViewModel
import com.example.weatherviewer.utils.DateUtil.toFormattedDate
import kotlin.random.Random

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState
    val uiState: WeatherUiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val favCityUiState: FavCitiesUiState by viewModel.favCityUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            WeatherTopAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = { viewModel.updateSearchTextState(it) },
                onCloseClicked = { viewModel.updateSearchWidgetState(SearchWidgetState.CLOSED) },
                onSearchClicked = { viewModel.getWeather(it) },
                onSearchTriggered = {
                    viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                WeatherScreenContent(
                    uiState = uiState,
                    modifier = modifier,
                    viewModel = viewModel
                ) {
                    viewModel.getWeather(it)

                }


            }
        },
    )
}

@Composable
fun WeatherScreenContent(
    uiState: WeatherUiState,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel?,
    onFavoriteClick: (String) -> Unit
) {
    when {
        uiState.isLoading -> {
            Animation(modifier = Modifier.fillMaxSize(), animation = R.raw.animation_loading)
        }

        uiState.errorMessage.isNotEmpty() -> {
            WeatherErrorState(uiState = uiState, viewModel = viewModel)
        }

//        uiState.favouriteCity?.isNotEmpty() == true -> {
//
//        }

        else -> {
            WeatherSuccessState(
                modifier = modifier,
                uiState = uiState,
                viewModel = viewModel,
            ) {
                onFavoriteClick(it)
            }
        }
    }
}

@Composable
private fun WeatherErrorState(
    modifier: Modifier = Modifier,
    uiState: WeatherUiState,
    viewModel: WeatherViewModel?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Animation(
            modifier = Modifier
                .fillMaxWidth()
                .weight(8f),
            animation = R.raw.animation_error,
        )

        Button(onClick = { viewModel?.getWeather() }) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Retry",
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.retry),
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            modifier = modifier
                .weight(2f)
                .alpha(0.5f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            text = "Something went wrong: ${uiState.errorMessage}",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WeatherSuccessState(
    modifier: Modifier,
    uiState: WeatherUiState,
    viewModel: WeatherViewModel?,
    onFavoriteClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(12.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = uiState.weather?.name.orEmpty(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,

                            contentDescription = stringResource(R.string.favorite_star),
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Top)
                                .clickable {
                                    val isFound = uiState.favouriteCity?.find {
                                        it.name == uiState.weather?.name
                                    }
                                    if (isFound != null) {
                                        viewModel?.deleteCity(
                                            uiState.weather?.name ?: ""
                                        )

                                    } else {
                                        viewModel?.saveCity(
                                            uiState.weather!!
                                        )
                                    }

                                },
                            tint = if (uiState.isFavorite) Color.Yellow else Color.Gray
                        )
                    }

                    Text(
                        text = uiState.weather?.date?.toFormattedDate().orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    AsyncImage(
                        modifier = Modifier.size(72.dp),
                        model = stringResource(
                            R.string.icon_image_url,
                            uiState.weather?.condition?.icon.orEmpty(),
                        ),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        error = painterResource(id = R.drawable.ic_placeholder),
                        placeholder = painterResource(id = R.drawable.ic_placeholder),
                    )

                    Text(
                        text = stringResource(
                            R.string.temperature_value_in_celsius,
                            uiState.weather?.temperature.toString()
                        ),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        text = uiState.weather?.condition?.text.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )

                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = stringResource(
                            R.string.feels_like_temperature_in_celsius,
                            uiState.weather?.feelsLike.toString()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.forecast),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            modifier = modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 8.dp, start = 16.dp),
        ) {
            uiState.weather?.let { weather ->
                items(weather.forecasts.drop(1)) { forecast ->
                    WeatherPredictionComponent(
                        date = forecast.date,
                        icon = forecast.icon,
                        minTemp = stringResource(
                            R.string.temperature_value_in_celsius,
                            forecast.minTemp
                        ),
                        maxTemp = stringResource(
                            R.string.temperature_value_in_celsius,
                            forecast.maxTemp,
                        ),
                    )
                }
            }
        }


        FavouriteSuccessState(modifier, uiState) {
            onFavoriteClick(it)
        }

    }
}

@Composable
private fun FavouriteSuccessState(
    modifier: Modifier,
    uiState: WeatherUiState,
    onFavoriteClick: (String) -> Unit
) {
    if (!uiState.favouriteCity.isNullOrEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.favorite_star),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 8.dp, start = 16.dp),
    ) {
        uiState.favouriteCity?.let { favouriteCities ->
            items(favouriteCities) { forecast ->
                FavouriteComponent(
                    cityName = forecast.name,
                ) {
                    onFavoriteClick(it)
                }
            }
        }
    }

    Spacer(Modifier.height(16.dp))
}


@Preview(name = "Light Mode", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true,
    device = PIXEL_XL
)
@Composable
fun WeatherScreenContentPreview() {
    val hourlyForecast = mutableListOf<Hour>()
    for (i in 0 until 24) {
        hourlyForecast.add(
            Hour(
                "yyyy-mm-dd ${String.format("%02d", i)}",
                "",
                "${Random.nextInt(18, 21)}"
            )
        )
    }
    val forecasts = mutableListOf<Forecast>()
    for (i in 0..9) {
        forecasts.add(
            Forecast(
                "2023-10-${String.format("%02d", i)}",
                "${Random.nextInt(18, 21)}",
                "${Random.nextInt(10, 15)}",
                "07:20 am",
                "06:40 pm",
                "",
                hourlyForecast
            )
        )
    }
    WeatherViewerTheme {
        Surface {
            WeatherScreenContent(
                WeatherUiState(
                    Weather.createWithDetails(
                        temperature = 19,
                        date = "Sept 21",
                        wind = 22,
                        humidity = 35,
                        feelsLike = 18,
                        condition = ForecastResponse.Current.Condition(10, "", "Cloudy"),
                        uv = 2,
                        name = "Cottbus",
                        forecasts = forecasts
                    ),
                ),
                viewModel = null
            ) {}
        }
    }

}

@Composable
fun WeatherTopAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onSearchClicked = onSearchTriggered
            )
        }

        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(onSearchClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        actions = {
            IconButton(
                onClick = { onSearchClicked() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_icon),
                )
            }
        }
    )
}

@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = { onTextChange(it) },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = stringResource(R.string.search_hint),
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier.alpha(0.7f),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_icon),
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_icon),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                    keyboardController?.hide()
                },
            ),
        )
    }
}


@Composable
@Preview
fun DefaultAppBarPreview() {
    DefaultAppBar(onSearchClicked = {})
}

@Composable
@Preview
fun SearchAppBarPreview() {
    SearchAppBar(
        text = "Search for a city",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {}
    )
}