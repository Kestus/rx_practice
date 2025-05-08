package ru.kestus.rx_practice.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.kestus.rx_practice.databinding.ActivityMainBinding
import ru.kestus.rx_practice.domain.addTo

class MainActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()
    private val moviesAdapter by lazy { MoviesAdapter() }

    // TODO: Сделайте ресайклер. По нажатию на элемент передавайте его позицию во фрагмент. и во фрагменте этот номер отображайте в тосте. (Subject)
    private val toastMessageSubject = PublishSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAdapter()
        setupListeners()
        observeViewModel()
        observeToastMessage()
    }

    private fun setupAdapter() {
        moviesAdapter.onItemClickListener = {
            toastMessageSubject.onNext(it.name)
        }
        binding.rvMovies.adapter = moviesAdapter
    }

    private fun setupListeners() {
        setFabOnClickListener()
        setTextInputListener()
    }

    private fun setFabOnClickListener() {
        binding.fab.setOnClickListener {
            viewModel.updateMovieList()
        }
    }

    private fun setTextInputListener() {
        binding.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.submitTextInput(text.toString())
        }
    }

    private fun observeViewModel() {
        observeMovieList()
        observeLoadingState()
        observeTimer()
    }

    private fun observeMovieList() {
        viewModel.movieListLiveData.observe(this) {
            moviesAdapter.submitList(it)
        }
    }

    private fun observeLoadingState() {
        viewModel.loading.observe(this) {
            binding.rvMoviesProgressCircular.visibility = if (it) View.VISIBLE else View.GONE
            binding.fab.isEnabled = !it
        }
    }

    private fun observeTimer() {
        viewModel.timer.observe(this) {
            binding.fab.text = it
        }
    }

    private fun observeToastMessage() {
        toastMessageSubject.subscribe {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }.addTo(disposables)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}