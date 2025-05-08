package ru.kestus.rx_practice.data.network

import android.util.Log
import io.reactivex.rxjava3.core.Single
import kotlin.random.Random

object ApiService {

    fun fetchMovies(): Single<List<Int>> = Single.create { emitter ->
        try {
            Log.d(TAG, "fetchMovies: started...")
            val list = mutableListOf<Int>()
            repeat(10) {
                val index = Random.nextInt(0, 100)
                Thread.sleep(index * 10L)
                list.add(index)
            }
            Log.d(TAG, "fetchMovies: emitting ${list.size} movies...")
            emitter.onSuccess(list)
        } catch (e: Throwable) {
            emitter.onError(e)
        }
    }

    private const val TAG = "TAG: ApiService"
}