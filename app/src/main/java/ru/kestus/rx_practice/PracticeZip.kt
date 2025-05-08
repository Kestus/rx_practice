package ru.kestus.rx_practice

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


// TODO: Есть 2 сервера на которых лежат скидочные карты. Нужно получить эти данные и вывести в единый список. (zip и тд)
fun main() {
    val onNextAction: (Int) -> Unit = { println(it) }
    val onErrorAction: (Throwable) -> Unit = { println(it) }

    val sources = listOf(
        fetchSource().toObservable(),
        fetchSourceError().toObservable(),
    )

    // а) Если 1 из запросов падает, то все равно выводить (найти метод RX для такого, чтоб самому не прописывать логику)
    println("delayError")
    val delayError = Observable
        .concatDelayError(sources)
        .flatMapIterable { it }
        .subscribe(
            onNextAction,
            onErrorAction
        )

    println("concatEager")
    // б) Если 1 из запросов падает, то не выводить ничего (найти метод RX)
    val eager = Observable
        .concatEager(sources)
        .flatMapIterable { it }
        .subscribe(
            onNextAction,
            onErrorAction
        )

    readln()
}


fun fetchSource() = Single.create<List<Int>> { emitter ->
    Thread.sleep(100)
    emitter.onSuccess(listOf(1, 2, 3))
}

fun fetchSourceError() = Single.create<List<Int>> { emitter ->
    Thread.sleep(50)
//    emitter.onSuccess(listOf(4,5,6))
    emitter.onError(RuntimeException())
}