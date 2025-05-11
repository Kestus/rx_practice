package ru.kestus.rx_practice.data

import android.util.Log
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.kestus.rx_practice.data.network.ApiService
import ru.kestus.rx_practice.domain.MovieItem
import ru.kestus.rx_practice.domain.mapToListOfMovies

object Repository {
    private val apiService: ApiService = ApiService

    private val disposables = CompositeDisposable()

    private val _movieListSubject = PublishSubject.create<List<MovieItem>>()
    val movieListObservable: Observable<List<MovieItem>> = _movieListSubject

    val timerObservable = Observable
        .create<Int> { emitter ->
            var time = 0
            while (!emitter.isDisposed) {
                emitter.onNext(time++)
                Thread.sleep(1000)
            }
        }
        .map { it.toString() }

    fun updateMovieList() = Completable.create { completable ->
        apiService.fetchMovies()
            .observeOn(Schedulers.io())
            .map { it.mapToListOfMovies() }
            .subscribe(object : SingleObserver<List<MovieItem>> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "updateMovieList: update started...")
                    disposables.add(d)
                }

                override fun onSuccess(list: List<MovieItem>) {
                    Log.d(TAG, "updateMovieList: success ${list.size} movies...")
                    _movieListSubject.onNext(list)
                    completable.onComplete()
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "updateMovieList: error ${e.message}")
                    completable.onError(e)
                }
            })
    }

    fun dispose() {
        disposables.dispose()
        _movieListSubject.onComplete()
    }

    private const val TAG = "TAG: Repository"
}