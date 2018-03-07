package com.rayhahah.easyhttp

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.rayhahah.library.core.EClient
import com.rayhahah.library.core.EHttp
import com.rayhahah.library.core.Files
import com.rayhahah.library.http.TYPE
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Cache
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var maxCacheSize = 10 * 1024 * 1024
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mInitClient.setOnClickListener {
            /**
             * 构建OkHttpClient
             * 使用这种方式构建的话，会直接配置成默认使用的OkHttpClient
             */
            EClient {
                timeUnit = TimeUnit.SECONDS
                connectTimeout = 10
                readTimeout = 10
                writeTimeout = 10
                interceptors()
                networkInterceptors()
                retryOnConnectionFailure = true
                cache = Cache(getCachePathStr(), maxCacheSize.toLong())

            }
        }

        mGet.setOnClickListener {
            //            request(TYPE.METHOD_GET, "admin", "1234567") { data: Response ->
//            }
        }

        mPost.setOnClickListener {
            request(TYPE.METHOD_POST, "test", "1234567") { data: Response ->
                Log.e("lzh", data.toString())
            }
        }

        mPut.setOnClickListener {
            request(TYPE.METHOD_PUT, "admin", "1234567") { data: Response ->
            }
        }

        mDelete.setOnClickListener {
            request(TYPE.METHOD_DELETE, "admin", "1234567") { data: Response ->
            }
        }

        mPostFile.setOnClickListener {
            requestFile("admin", "1234567", File("cover.jpg")) { data: Response ->
            }

        }

        mJson.setOnClickListener {
            EHttp {
                baseUrl = "http://mall.rayhahah.com/"
                src = "user/login.do"
                type = TYPE.METHOD_POST
                json = ""
                header = {
                    "cache-Control"("no-cache")
                }

            }.go<Response> { data: Response ->

            }
        }
    }


    fun request(method: String, username: String, password: String, success: (data: Response) -> Unit) {
        EHttp {
            baseUrl = "http://mall.rayhahah.com/"
            src = "user/login.do"
            type = method
            data = {
                "username"(username)
                "password"(password)
            }
            header = {
                "cache-Control"("no-cache")
            }

        }.go<Response>(success)
    }

    fun requestFile(username: String, password: String, cover: File, success: (data: Response) -> Unit) {
        EHttp {
            baseUrl = "http://mall.rayhahah.com/"
            src = "user/login.do"
            type = TYPE.METHOD_POST
            data = {
                "username"(username)
                "password"(password)
                file = {
                    "cover"(Files.FILE_TYPE_IMAGE, cover)
                }
            }
            header = {
                "cache-Control"("no-cache")
            }

        }.go<Response>(success)
    }

    /**
     * 获取缓存目录
     */
    fun getCachePathStr(): File {
        return if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            externalCacheDir
        } else {
            cacheDir
        }
    }


}
