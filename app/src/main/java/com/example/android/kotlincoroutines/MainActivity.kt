package com.example.android.kotlincoroutines

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.android.kotlincoroutines.main.MainViewModel
import com.example.android.kotlincoroutines.main.TitleRepository
import com.example.android.kotlincoroutines.main.getDatabase
import com.example.android.kotlincoroutines.main.getNetworkService
import com.example.android.kotlincoroutines.ui.theme.CoroutinesCodelabTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoroutinesCodelabTheme {
                val database = getDatabase(this)
                val repository = TitleRepository(getNetworkService(), database.titleDao)
                val viewModel = ViewModelProvider(this, MainViewModel.FACTORY(repository))
                    .get(MainViewModel::class.java)
                var title by remember { mutableStateOf("title")}
                var taps by remember { mutableStateOf("taps")}
                var spinner by remember { mutableStateOf(false)}
                var snackbar by remember { mutableStateOf("")}
                viewModel.title.observe(this) { value ->
                    value?.let {
                        title = it
                    }
                }

                viewModel.taps.observe(this) { value ->
                    taps = value
                }

                // show the spinner when [MainViewModel.spinner] is true
                viewModel.spinner.observe(this) { value ->
                    value.let { show ->
                        spinner = show
                    }
                }
                viewModel.snackbar.observe(this) { text ->
                    text.let { value ->
                        snackbar = value
                    }
                }
                val snackBarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {Text("Kotlin Coroutines")}
                        )
                    },
                    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                ) { innerPadding ->
                    val coroutineScope = rememberCoroutineScope()
                    LaunchedEffect(snackbar) {
                        if (snackbar.isNotEmpty()) coroutineScope.launch {
                            Log.d("KOTLINCLASS", "coroutineScope.launch")
                            snackBarHostState.showSnackbar(
                                message = snackbar,
                                duration = SnackbarDuration.Short,
                                actionLabel = "label",
                                withDismissAction = true,
                            )
                            viewModel.onSnackbarShown()
                        }
                    }
                    Box(Modifier.padding(innerPadding)
                        .fillMaxSize()
                        .background(Color.White)
                        .clickable{
                            viewModel.onMainViewClicked()
                        },
                        //horizontalAlignment = Alignment.CenterHorizontally,
                        //verticalArrangement = Arrangement.Center
                        contentAlignment = Alignment.Center
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(" $title", Modifier.padding(10.dp))
                            Text(" $taps", Modifier.padding(10.dp))
                            Text(" $snackbar", Modifier.padding(10.dp))
                        }

                        if(spinner) {
                            CircularProgressIndicator(Modifier.align(Alignment.BottomEnd))
                        }
                    }
                }
            }
        }
    }
}
