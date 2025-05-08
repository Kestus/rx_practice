package ru.kestus.rx_practice.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.kestus.rx_practice.data.Repository
import ru.kestus.rx_practice.domain.MovieItem
import ru.kestus.rx_practice.domain.addTo
import java.util.concurrent.TimeUnit


class MainViewModel : ViewModel() {

    private val repository = Repository
    private val disposables = CompositeDisposable()

    private val _movieListLiveData = MutableLiveData<List<MovieItem>>()
    val movieListLiveData: LiveData<List<MovieItem>> = _movieListLiveData

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    // TODO: Сделайте таймер. TextView которая раз в секунду меняется (timer)
    private val _timer = MutableLiveData<String>()
    val timer : LiveData<String> = _timer

    // TODO: Сделайте EditText. При наборе текста выводите в лог содержимое EditText всегда, когда пользователь 3 секунды что-то не вводил (debounce)
    private val textInputSubject = PublishSubject.create<String>()
    private val textInputObservable = textInputSubject.debounce(3, TimeUnit.SECONDS, Schedulers.io())

    init {
        Log.d(TAG, "MainViewModel: init")
        observeMovieList()
        updateMovieList()
        observeTimer()
        observeTextInput()
    }

    // TODO: Сделайте сетевой запрос и отобразите результат на экране? (база)
    private fun observeMovieList() {
        repository.movieListObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<MovieItem>> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: subscribed")
                    disposables.add(d)
                }

                override fun onNext(list: List<MovieItem>) {
                    Log.d(TAG, "onSubscribe: onNext ${list.size} size list")
                    _movieListLiveData.value = list
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: completed")
                }
            })
    }

    fun updateMovieList() {
        repository.updateMovieList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "updateMovieList: loading started")
                    disposables.add(d)
                    _loading.value = true
                }

                override fun onComplete() {
                    Log.d(TAG, "updateMovieList: loading finished")
                    _loading.value = false
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "updateMovieList: loading error")
                    _loading.value = false
                }
            })
    }
    
    private fun observeTimer() {
        repository.timerObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: timer")
                    disposables.add(d)
                }

                override fun onNext(timer: String) {
                   // Log.d(TAG, "onNext: $timer")
                    _timer.value = timer
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: ${e.message}")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: timer completed")
                }
            })
    }

    private fun observeTextInput() {
        textInputObservable
            .subscribe {
                Log.d(TAG, "observeTextInput: $it : ${Thread.currentThread().name}")
            }.addTo(disposables)
    }

    fun submitTextInput(input: String) {
        textInputSubject.onNext(input)
    }

    override fun onCleared() {
        disposables.dispose()
        repository.dispose()
        super.onCleared()
    }

    companion object {
        private const val TAG = "TAG: MainViewModel"
    }

}