package com.rayhahah.library.core

import com.rayhahah.library.parser.Parser
import com.rayhahah.library.service.RequestManager
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ┌───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
 * │Esc│ │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│ ┌┐    ┌┐    ┌┐
 * └───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘ └┘    └┘    └┘
 * ┌──┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐┌───┬───┬───┐┌───┬───┬───┬───┐
 * │~`│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp ││Ins│Hom│PUp││N L│ / │ * │ - │
 * ├──┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤├───┼───┼───┤├───┼───┼───┼───┤
 * │Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ ││Del│End│PDn││ 7 │ 8 │ 9 │   │
 * ├────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤└───┴───┴───┘├───┼───┼───┤ + │
 * │Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │             │ 4 │ 5 │ 6 │   │
 * ├─────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤    ┌───┐    ├───┼───┼───┼───┤
 * │Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │    │ ↑ │    │ 1 │ 2 │ 3 │   │
 * ├────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤┌───┼───┼───┐├───┴───┼───┤ E││
 * │Ctrl│Ray │Alt │         Space         │ Alt│code│fuck│Ctrl││ ← │ ↓ │ → ││   0   │ . │←─┘│
 * └────┴────┴────┴───────────────────────┴────┴────┴────┴────┘└───┴───┴───┘└───────┴───┴───┘
 *
 * @author Rayhahah
 * @blog http://rayhahah.com
 * @time 2018/3/5
 * @tips 这个类是Object的子类
 * @fuction
 */

class EasyClient {

    var timeUnit: TimeUnit = TimeUnit.SECONDS
    var connectTimeout: Long = 10
    var writeTimeout: Long = 10
    var readTimeout: Long = 10
    var interceptors: ArrayList<Interceptor> = ArrayList()
    var networkInterceptors: ArrayList<Interceptor> = ArrayList()
    var retryOnConnectionFailure: Boolean = false
    var cache: Cache? = null
    var parser: Parser? = null

    operator fun ArrayList<Interceptor>.invoke(vararg interceptor: Interceptor) {
        this.addAll(interceptor)
    }

    fun init(): OkHttpClient? {
        var builder = OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(connectTimeout, timeUnit)
                .writeTimeout(writeTimeout, timeUnit)
                .readTimeout(readTimeout, timeUnit)
                .retryOnConnectionFailure(retryOnConnectionFailure)
        builder = initNetworkInterceptors(builder, networkInterceptors)
        builder = initInterceptors(builder, interceptors)
        val okHttpClient = builder.build()
        initDefaultClient(okHttpClient)

        return okHttpClient
    }

    /**
     * 初始化默认使用的Client
     */
    fun initDefaultClient(okHttpClient: OkHttpClient) {
        RequestManager.client = okHttpClient
        RequestManager.parser = parser
    }


    private fun initNetworkInterceptors(builder: OkHttpClient.Builder?, networkInterceptors: ArrayList<Interceptor>): OkHttpClient.Builder? {
        networkInterceptors.forEach { i: Interceptor? ->
            builder?.addNetworkInterceptor(i)
        }
        return builder
    }

    private fun initInterceptors(builder: OkHttpClient.Builder?, interceptors: ArrayList<Interceptor>): OkHttpClient.Builder? {
        interceptors.forEach { i: Interceptor? ->
            builder?.addInterceptor(i)
        }
        return builder
    }
}