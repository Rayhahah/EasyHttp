package com.rayhahah.library.service

import android.os.Handler
import android.os.Looper
import com.rayhahah.library.callback.AbstractCallBack
import com.rayhahah.library.callback.WrapperCallBack
import com.rayhahah.library.core.EasyClient
import com.rayhahah.library.http.HttpFile
import com.rayhahah.library.http.HttpHeader
import com.rayhahah.library.http.TYPE
import com.rayhahah.library.parser.FileParser
import com.rayhahah.library.parser.Parser
import com.rayhahah.library.request.HttpRequest
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.Call
import okhttp3.OkHttpClient


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
class RequestManager {
    companion object {
        var client: OkHttpClient = EasyClient().init()!!
        var type: String = TYPE.METHOD_GET
        var parser: Parser? = null
        var baseUrl: String? = null
        var header = HashMap<String, String>()

        val mMainHandler = Handler(Looper.getMainLooper())

        fun <T> goGet(okHttpClient: OkHttpClient?, url: String
                      , header: HttpHeader
                      , params: HashMap<String, String>
                      , parser: Parser?
                      , success: (data: T) -> Unit
                      , failed: (call: Call, exception: Exception) -> Unit
                      , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(okHttpClient, url, parser, progress, failed, success)
            httpRequest.header(header)
                    .paramsGet(params)
                    .excute()
        }

        fun <T> goForm(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , type: String
                       , params: HashMap<String, String>
                       , parser: Parser?
                       , success: (data: T) -> Unit
                       , failed: (call: Call, exception: Exception) -> Unit
                       , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(okHttpClient, url, parser, progress, failed, success)
            httpRequest.header(header)
                    .paramsForm(type, params)
                    .excute()
        }

        fun <T> goFile(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , params: HashMap<String, String>
                       , fileMap: HashMap<String, HttpFile>
                       , parser: Parser?
                       , success: (data: T) -> Unit
                       , failed: (call: Call, exception: Exception) -> Unit
                       , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(okHttpClient, url, parser, progress, failed, success)
            httpRequest.header(header)
                    .paramsFile(params, fileMap)
                    .excute()
        }

        fun <T> goJson(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , type: String
                       , json: String
                       , parser: Parser?
                       , success: (data: T) -> Unit
                       , failed: (call: Call, exception: Exception) -> Unit
                       , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(okHttpClient, url, parser, progress, failed, success)
            httpRequest.header(header)
                    .paramsJson(type, json)
                    .excute()
        }

        fun <T> goDownload(okHttpClient: OkHttpClient?, url: String
                           , header: HttpHeader
                           , fileDir: String
                           , fileName: String
                           , success: (data: T) -> Unit
                           , failed: (call: Call, exception: Exception) -> Unit
                           , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(
                    okHttpClient,
                    url,
                    FileParser<T>(fileDir, fileName, progress),
                    progress,
                    failed,
                    success
            )
            httpRequest.header(header)
                    .paramsGet(HashMap<String, String>())
                    .excute()
        }


        fun <T> initHttpRequest(okHttpClient: OkHttpClient?
                                , url: String
                                , parser: Parser?
                                , progress: (value: Float, total: Long) -> Unit
                                , failed: (call: Call, exception: Exception) -> Unit
                                , success: (data: T) -> Unit): HttpRequest {


            val wrapperCallBack = initCallBack<T>(parser, failed, success)
            val httpRequest = if (okHttpClient == null) {
                HttpRequest(client, url, wrapperCallBack, progress)
            } else {
                HttpRequest(okHttpClient, url, wrapperCallBack, progress)
            }
            return httpRequest
        }

        fun <T> initCallBack(parser: Parser?,
                             failed: (call: Call, exception: Exception) -> Unit,
                             success: (data: T) -> Unit): WrapperCallBack<T> {
            val wrapperCallBack = WrapperCallBack(object : AbstractCallBack<T>() {
                override fun fail(call: Call, e: Exception) {
                    mMainHandler.post { failed(call, e) }
                }

                override fun success(call: Call, response: T) {
                    mMainHandler.post {
                        success(response)
                    }
                }

            }, parser)


            return wrapperCallBack
        }


        fun <T> rxGet(okHttpClient: OkHttpClient?, url: String
                      , header: HttpHeader
                      , params: HashMap<String, String>
                      , parser: Parser?
                      , progress: (value: Float, total: Long) -> Unit
        ): Observable<T> {
            return Observable.create<T> { emitter: ObservableEmitter<T> ->
                val httpRequest = initRxHttpRequest<T>(okHttpClient, url, parser, progress, emitter)
                httpRequest.header(header)
                        .paramsGet(params)
                        .excute()
            }
        }


        fun <T> rxForm(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , type: String
                       , params: HashMap<String, String>
                       , parser: Parser?
                       , progress: (value: Float, total: Long) -> Unit
        ): Observable<T> {
            return Observable.create<T> { emitter: ObservableEmitter<T> ->
                val httpRequest = initRxHttpRequest<T>(okHttpClient, url, parser, progress, emitter)
                httpRequest.header(header)
                        .paramsForm(type, params)
                        .excute()
            }
        }

        fun <T> rxFile(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , params: HashMap<String, String>
                       , fileMap: HashMap<String, HttpFile>
                       , parser: Parser?
                       , progress: (value: Float, total: Long) -> Unit
        ): Observable<T> {
            return Observable.create<T> { emitter: ObservableEmitter<T> ->
                val httpRequest = initRxHttpRequest<T>(okHttpClient, url, parser, progress, emitter)
                httpRequest.header(header)
                        .paramsFile(params, fileMap)
                        .excute()
            }
        }

        fun <T> rxJson(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , type: String
                       , json: String
                       , parser: Parser?
                       , progress: (value: Float, total: Long) -> Unit
        ): Observable<T> {
            return Observable.create<T> { emitter: ObservableEmitter<T> ->
                val httpRequest = initRxHttpRequest<T>(okHttpClient, url, parser, progress, emitter)
                httpRequest.header(header)
                        .paramsJson(type, json)
                        .excute()
            }
        }


        fun <T> initRxHttpRequest(
                okHttpClient: OkHttpClient?
                , url: String
                , parser: Parser?
                , progress: (value: Float, total: Long) -> Unit
                , emitter: ObservableEmitter<T>): HttpRequest {
            val wrapperCallBack = initRxCallBack<T>(parser, emitter)
            val httpRequest = if (okHttpClient == null) {
                HttpRequest(client, url, wrapperCallBack, progress)
            } else {
                HttpRequest(okHttpClient, url, wrapperCallBack, progress)
            }

            return httpRequest
        }

        fun <T> initRxCallBack(parser: Parser?, emitter: ObservableEmitter<T>): WrapperCallBack<T> {
            val wrapperCallBack = WrapperCallBack(object : AbstractCallBack<T>() {
                override fun fail(call: Call, e: Exception) {
                    emitter.onError(e)

                }

                override fun success(call: Call, response: T) {
                    emitter.onNext(response)
                }

            }, parser)
            return wrapperCallBack

        }

    }
}