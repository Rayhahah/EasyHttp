package com.rayhahah.easyhttp

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.rayhahah.easyhttp.Util.FileUtils
import com.rayhahah.library.core.EClient
import com.rayhahah.library.core.EHttp
import com.rayhahah.library.core.Files
import com.rayhahah.library.http.TYPE
import com.sembozdemir.permissionskt.askPermissions
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var maxCacheSize = 10 * 1024 * 1024
    val CODE_CHOOSE_PHOTO = 100

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

        /**
         * 普通Get请求
         */
        mGet.setOnClickListener {
            request(TYPE.METHOD_GET, "test", "1234567") { data: Response ->
                mTvTest.setText(data.body()?.string())
            }
        }

        /**
         * 普通POST请求
         */
        mPost.setOnClickListener {
            request(TYPE.METHOD_POST, "test", "1234567") { data: Response ->
                Log.e("lzh", data.toString())
                mTvTest.setText(data.body()?.string())
            }
        }

        /**
         * 普通PUT请求
         */
        mPut.setOnClickListener {
            request(TYPE.METHOD_PUT, "admin", "1234567") { data: Response ->
            }
        }

        /**
         * 普通DELETE请求
         */
        mDelete.setOnClickListener {
            request(TYPE.METHOD_DELETE, "admin", "1234567") { data: Response ->
            }
        }

        /**
         * 上传单个文件请求
         */
        mPostFile.setOnClickListener {
            val context = this
            askPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                onGranted {
                    val chooseIntent = Intent(Intent.ACTION_GET_CONTENT)
                    chooseIntent.type = "image/*"
                    context.startActivityForResult(chooseIntent, CODE_CHOOSE_PHOTO)
                }
            }
        }

        /**
         * 上传Json数据
         */
        mJson.setOnClickListener {
            EHttp {
                baseUrl = "http://mall.rayhahah.com/"
                src = "user/login.do"
                type = TYPE.METHOD_POST
                json = ""
                header = {
                    "cache-Control"("no-cache")
                }

            }.go { data: Response ->

            }
        }


        /**
         * 下载文件数据
         */
        mDownload.setOnClickListener {
            EHttp {
                baseUrl = "http://thing.rayhahah.com/version/EasySport_1.1.4.apk"
                download = {
                    fileDir = FileUtils.getRootFilePath() + "EasyHttp/images"
                    fileName = "test.apk"
                }
            }.download({ data: File ->
                data.log()

            }, { call: Call, exception: Exception ->


            }, { value: Float, total: Long ->
                value.log()
                total.log()
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_CHOOSE_PHOTO -> if (data != null) {
                var path = FileUtils.getPathFromUri(this, data.data)
                val file = FileUtils.getFileByPath(path)
                requestFile("test", "1234567", file, { data: Response ->
                    data.log()
                    mTvTest.setText(data.body()?.string())
                }, { call: Call, e: Exception ->

                }, { value, total ->
                    value.log()
                    total.log()
                })
            }
            else -> {
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

        }.go(success)
    }

    fun rxRequest(method: String, username: String, password: String): io.reactivex.Observable<Response> {
        return EHttp {
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

        }.rx()
    }


    fun requestFile(username: String, password: String, cover: File,
                    success: (data: Response) -> Unit,
                    fail: (call: Call, e: Exception) -> Unit,
                    progress: (value: Float, total: Long) -> Unit) {
        EHttp {
            baseUrl = "http://mall.rayhahah.com/"
            src = "easysport/user/update_cover.do"
            type = TYPE.METHOD_POST
            data = {
                "username"(username)
                "password"(password)
                file = {
                    "upload_file"(Files.FILE_TYPE_MULTIPART, cover)
                }
            }
            header = {
                "cache-Control"("no-cache")
            }

        }.go(success, fail, progress)
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
