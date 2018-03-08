package com.rayhahah.library.core

import com.rayhahah.library.delegate.LambdaDelegate
import com.rayhahah.library.http.HttpFile
import com.rayhahah.library.http.HttpHeader
import com.rayhahah.library.http.TYPE
import com.rayhahah.library.service.RequestManager
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File
import java.lang.Exception
import java.util.*

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
 * @time 2018/2/28
 * @tips 这个类是Object的子类
 * @fuction
 */
class EasyHttp {

    var client: OkHttpClient? = null
    lateinit var baseUrl: String
    var src: String = ""
    var type: String = TYPE.METHOD_POST

    var json: String? = null
    val params = Params()
    val head = HttpHeader(HashMap<String, String>())
    val downloadParams = Download()
    var data: Params.() -> Unit by LambdaDelegate<Params>(params)
    var header: HttpHeader.() -> Unit by LambdaDelegate<HttpHeader>(head)
    var download: Download.() -> Unit by LambdaDelegate<Download>(downloadParams)

    /**
     * 异步加载数据
     */
    fun go(success: (data: Response) -> Unit) {
        go(success, { call, exception -> }, { value, total -> })
    }

    /**
     * 异步加载数据
     */
    fun go(success: (data: Response) -> Unit
           , failed: (call: Call, exception: Exception) -> Unit
           , progress: (value: Float, total: Long) -> Unit) {
        when (type) {
            TYPE.METHOD_GET ->
                RequestManager.goGet<Response>(client, baseUrl + src, head, params.data, success, failed, progress)
            TYPE.METHOD_POST ->
                if (json != null) {
                    RequestManager.goJson<Response>(client, baseUrl + src, head, type, json!!, success, failed, progress)
                } else {
                    val paramsType = params.getType()
                    when (paramsType) {
                        Params.SINGLE_FILE -> {
                            val fileMap = HashMap<String, HttpFile>()
                            val fileList = ArrayList<File>()
                            fileList.add(params.files.DEFAULT!!)
                            fileMap.put("", HttpFile(Files.FILE_TYPE_FILE, fileList))
                            RequestManager.goFile<Response>(client, baseUrl + src, head, params.data, fileMap, success, failed, progress)
                        }
                        Params.LIST_FILE -> RequestManager.goFile<Response>(client, baseUrl + src, head, params.data, params.files.data, success, failed, progress)
                        else -> RequestManager.goForm<Response>(client, baseUrl + src, head, type, params.data, success, failed, progress)
                    }
                }
            TYPE.METHOD_PUT,
            TYPE.METHOD_DELETE ->
                if (json != null) {
                    RequestManager.goJson<Response>(client, baseUrl + src, head, type, json!!, success, failed, progress)
                } else {
                    RequestManager.goForm<Response>(client, baseUrl + src, head, type, params.data, success, failed, progress)
                }
        }
    }

    /**
     * 异步加载数据
     */
    fun download(success: (data: File) -> Unit) {
        download(success, { call, exception -> }, { value, total -> })
    }


    /**
     * 异步加载数据
     */
    fun download(success: (data: File) -> Unit
                 , failed: (call: Call, exception: Exception) -> Unit
                 , progress: (value: Float, total: Long) -> Unit) {
        RequestManager.goDownload<File>(client, baseUrl + src, head, downloadParams.fileDir, downloadParams.fileName, success, failed, progress)
    }


    fun rx(progress: (value: Float, total: Long) -> Unit = { v, t -> }): Observable<Response> {
        return when (type) {
            TYPE.METHOD_GET ->
                RequestManager.rxGet<Response>(client, baseUrl + src, head, params.data, progress)
            TYPE.METHOD_POST ->
                if (json != null) {
                    RequestManager.rxJson<Response>(client, baseUrl + src, head, type, json!!, progress)
                } else {
                    val paramsType = params.getType()
                    when (paramsType) {
                        Params.SINGLE_FILE -> {
                            val fileMap = HashMap<String, HttpFile>()
                            val fileList = ArrayList<File>()
                            fileList.add(params.files.DEFAULT!!)
                            fileMap.put("", HttpFile(Files.FILE_TYPE_FILE, fileList))
                            RequestManager.rxFile<Response>(client, baseUrl + src, head, params.data, fileMap, progress)
                        }
                        Params.LIST_FILE -> RequestManager.rxFile<Response>(client, baseUrl + src, head, params.data, params.files.data, progress)
                        else -> RequestManager.rxForm<Response>(client, baseUrl + src, head, type, params.data, progress)
                    }
                }
            TYPE.METHOD_PUT,
            TYPE.METHOD_DELETE ->
                if (json != null) {
                    RequestManager.rxJson<Response>(client, baseUrl + src, head, type, json!!, progress)
                } else {
                    RequestManager.rxForm<Response>(client, baseUrl + src, head, type, params.data, progress)
                }
            else -> {
                Observable.create<Response> { emitter: ObservableEmitter<Response> ->
                    emitter.onError(throw Throwable("Please Enter Right Type"))
                }
            }
        }
    }
}
