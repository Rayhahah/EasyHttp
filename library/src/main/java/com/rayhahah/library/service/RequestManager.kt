package com.rayhahah.library.service

import android.os.Handler
import android.os.Looper
import com.rayhahah.library.callback.AbstractCallBack
import com.rayhahah.library.callback.WrapperCallBack
import com.rayhahah.library.core.EasyClient
import com.rayhahah.library.http.HttpFile
import com.rayhahah.library.http.HttpHeader
import com.rayhahah.library.parser.DefaultParser
import com.rayhahah.library.request.HttpRequest
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

        val mMainHandler = Handler(Looper.getMainLooper())

        fun <T> goGet(okHttpClient: OkHttpClient?, url: String
                      , header: HttpHeader
                      , params: HashMap<String, String>
                      , success: (data: T) -> Unit
                      , failed: (call: Call, exception: Exception) -> Unit
                      , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(failed, success, okHttpClient, url, progress)
            httpRequest.header(header)
                    .paramsGet(params)
                    .excute()
        }

        fun <T> goForm(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , type: String
                       , params: HashMap<String, String>
                       , success: (data: T) -> Unit
                       , failed: (call: Call, exception: Exception) -> Unit
                       , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(failed, success, okHttpClient, url, progress)
            httpRequest.header(header)
                    .paramsForm(type, params)
                    .excute()
        }

        fun <T> goFile(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , params: HashMap<String, String>
                       , fileMap: HashMap<String, HttpFile>
                       , success: (data: T) -> Unit
                       , failed: (call: Call, exception: Exception) -> Unit
                       , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(failed, success, okHttpClient, url, progress)
            httpRequest.header(header)
                    .paramsFile(params, fileMap)
                    .excute()
        }

        fun <T> goJson(okHttpClient: OkHttpClient?, url: String
                       , header: HttpHeader
                       , type: String
                       , json: String
                       , success: (data: T) -> Unit
                       , failed: (call: Call, exception: Exception) -> Unit
                       , progress: (value: Float, total: Long) -> Unit
        ) {
            val httpRequest = initHttpRequest<T>(failed, success, okHttpClient, url, progress)
            httpRequest.header(header)
                    .paramsJson(type, json)
                    .excute()
        }

        fun <T> initHttpRequest(failed: (call: Call, exception: Exception) -> Unit, success: (data: T) -> Unit, okHttpClient: OkHttpClient?, url: String, progress: (value: Float, total: Long) -> Unit): HttpRequest {
            val wrapperCallBack = initCallBack<T>(failed, success)
            val httpRequest = if (okHttpClient == null) {
                HttpRequest(client, url, wrapperCallBack, progress)
            } else {
                HttpRequest(okHttpClient, url, wrapperCallBack, progress)
            }
            return httpRequest
        }

        fun <T> initCallBack(failed: (call: Call, exception: Exception) -> Unit,
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

            }, DefaultParser<T>())
            return wrapperCallBack
        }
    }
}