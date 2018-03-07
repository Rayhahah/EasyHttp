package com.rayhahah.library.request

import com.rayhahah.library.callback.AbstractCallBack
import com.rayhahah.library.http.HttpFile
import com.rayhahah.library.http.HttpHeader
import okhttp3.*
import java.io.IOException

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

class HttpRequest(private val httpClient: OkHttpClient,
                     private var url: String,
                     private val callback: AbstractCallBack<Response>,
                     private val progress:(value: Float, total: Long)->Unit) {

    private var mOkHttpRequest: Request? = null//请求对象
    private var mRequestBuilder: Request.Builder? = null//请求对象的构建者

    init {
        mRequestBuilder = Request.Builder()
    }


    /**
     * 设置头参数
     */
    fun header(headerMap: HttpHeader): HttpRequest{
        for (key in headerMap.keys) {
            mRequestBuilder?.addHeader(key, headerMap[key])
        }
        return this
    }

    /**
     * get请求，只有键值对参数
     */
    fun paramsGet(paramsMap: Map<String, String>): HttpRequest {
        url = url + "?"
        for (key in paramsMap.keys) {
            url = url + key + "=" + paramsMap[key] + "&"
        }
        url = url.substring(0, url.length - 1)
        return this
    }

    /**
     * 设置JSON请求参数
     */
    fun paramsJson(type: String, json: String): HttpRequest {
        mRequestBuilder?.method(type, jsonRequestBody(json))
        return this
    }

    /**
     * 上传表单参数
     */
    fun paramsForm(type: String, paramsMap: Map<String, String>): HttpRequest {
        mRequestBuilder?.method(type, mapRequestBody(paramsMap))
        return this
    }

    fun jsonRequestBody(json: String): RequestBody {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        return RequestBody.create(JSON, json)
    }

    fun mapRequestBody(paramsMap: Map<String, String>): FormBody {
        val formBody = FormBody.Builder()
        for (key in paramsMap.keys) {
            formBody.add(key, paramsMap[key])
        }
        return formBody.build()
    }

    fun paramsFile(paramsMap: Map<String, String>,
                   fileMap: Map<String, HttpFile>): HttpRequest{
        if (fileMap.size == 1) {
            val fileKey = fileMap.keys.lastOrNull() ?: ""
            val httpFile = fileMap.get(fileKey) ?: return this
            val mediaType = MediaType.parse(httpFile.fileType)
            if (fileKey.isBlank()) {
                val body = RequestBody.create(mediaType, httpFile.fileList.get(0))//json数据，
                mRequestBuilder?.post(ProgressRequestBody(body, progress))
            } else {
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                for (key in paramsMap.keys) {
                    builder.addFormDataPart(key, paramsMap[key])
                }
                for (f in httpFile.fileList) {
                    builder.addFormDataPart(fileKey, f.getName(), RequestBody.create(mediaType, f))
                }
                mRequestBuilder?.post(ProgressRequestBody(builder.build(), progress))
            }
        } else if (fileMap.size > 1) {
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            for (key in paramsMap.keys) {
                builder.addFormDataPart(key, paramsMap[key])
            }

            for (key in fileMap.keys) {
                fileMap[key]?.apply {
                    val file = fileList.get(0)
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(fileType), file))
                }
            }
            mRequestBuilder?.post(ProgressRequestBody(builder.build(), progress))
        }
        return this
    }


    /**
     * 执行请求
     */
    fun excute() {
        mRequestBuilder?.url(url)
        mOkHttpRequest = mRequestBuilder?.build()
        httpClient.newCall(mOkHttpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.fail(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.success(call, response)

            }
        })
    }
}