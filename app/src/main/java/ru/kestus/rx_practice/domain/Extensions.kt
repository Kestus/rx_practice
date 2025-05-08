package ru.kestus.rx_practice.domain

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Disposable.addTo(disposables: CompositeDisposable) = disposables.add(this)

fun List<Int>.mapToListOfMovies() = map { MovieItem(it, "Movie #$it") }