# EasyHttp
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/Rayhahah/EasyHttp/pulls)[ ![Kotlin](https://img.shields.io/badge/Kotlin-1.2.0-blue.svg)](http://kotlinlang.org)[ ![GitHub release](https://img.shields.io/github/release/Rayhahah/EasyHttp.svg?maxAge=2592000?style=flat-square)](https://github.com/Rayhahah/EasyHttp/releases)[ ![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)[ ![](https://jitpack.io/v/rayhahah/easyhttp.svg)](https://jitpack.io/#rayhahah/easyhttp)[ ![License Apache2.0](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/Rayhahah/EasyHttp/blob/master/LICENSE)

`EasyHttp`是一个基于OkHttp封装的Kotlin DSL网络请求框架

## 功能
- [x] 支持HTTP GET/POST/PUT/DELETE
- [x] 文件下载(带进度)
- [x] 文件上传 (multipart/form-data)(带进度)
- [x] RxJava2.0请求响应支持
- [x] 支持OkHttpClient的自定义配置
- [x] DSL配置请求

## 目录
- [安装说明](#安装说明)
- [Client配置](#Client配置)
- [普通请求](#普通请求)
- [文件上传](#文件上传)
- [文件下载](#文件下载)
- [RxJava兼容](#RxJava兼容)
- [贡献](#贡献)
- [感谢](#感谢)

## 安装说明

**Gradle:**
1. 先在 build.gradle(Project:XXXX) 的 repositories 添加:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. 然后在 build.gradle(Module:app) 的 dependencies 添加:

```
	dependencies {
		  compile 'com.github.Rayhahah:EasyHttp:{release_version}'
	}
```

## Client配置
配置全局`OkHttpClient`

```
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
```

或者直接传入自定义的`OkHttpClient`

```
EClient(client: OkHttpClient)
```

## GET/POST/PUT/DELETE请求
- `client` : 本次请求使用的`OkHttpClient`，不配置的话默认使用上面`EClient`配置的`OkHttpClient`对象
- `type` : 区别请求类型，`TYPE.METHOD_GET` 、`TYPE.METHOD_POST`、`TYPE.METHOD_PUT` 、`TYPE.METHOD_DELETE` 、
- `data` :  请求携带的参数，`"key"("value")`
- `header` : 请求头参数 ，`"key"("value")`
- `go(success,fail,progress)` : 发送请求，回调都是在主线程中
	- `success = {data:Response->}` : 请求成功回调
	- `fail = {call:Call,e:Exception -> }` : 请求失败回调
	- `progess = {value:Float,total:Long -> }`: 请求过程回传，上传文件可以查看

一次正常且配置详细的请求如下：
```
 EHttp {
		    client = okHttpClient
            baseUrl = "http://mall.rayhahah.com/"
            src = "user/login.do"
            type = TYPE.METHOD_GET
            data = {
                "username"(username)
                "password"(password)
            }
            header = {
                "cache-Control"("no-cache")
            }

        }.go(success,fail,progress)
```

接下来，简便一点：
```
EHttp{
  baseUrl = "http://mall.rayhahah.com/"
  src = "user/login.do"
 type = TYPE.METHOD_GET
 data = {
       "username"(username)
       "password"(password)
        }
}.go{data:Response -> }

```

或者简单的请求可以这样


```
EGet(url:String,params:HashMap<String,String>())
	.go{data:Response->}

EPost(url:String,params:HashMap<String,String>())
	.go{data:Response->}
```


## 文件上传

- `file` ： 上传的文件
	- `"key"("上传文件类型"，File())`
	- `"key"(HttpFile("上传文件类型",ArrayList<File>())`

```
     EHttp {
            baseUrl = "http://mall.rayhahah.com/"
            src = "easysport/user/update_cover.do"
            type = TYPE.METHOD_POST
            data = {
                "username"(username)
                "password"(password)
                file = {
	                //上传单个文件
                    "upload_file"(Files.FILE_TYPE_MULTIPART, cover)
                    //上传单个字段多个文件
                     val fileList = ArrayList<File>()
                    fileList.add(File("1.txt"))
                    fileList.add(File("2.txt"))
                    fileList.add(File("3.txt"))
                    "upload"(HttpFile(Files.FILE_TYPE_MULTIPART, fileList))
                }
            }
            header = {
                "cache-Control"("no-cache")
            }

        }.go(success, fail, progress)

```



## 文件下载


```
  EHttp {
                baseUrl = "http://thing.rayhahah.com/version/EasySport_1.1.4.apk"
                download = {
                    fileDir = FileUtils.getRootFilePath() + "EasyHttp/images"
                    fileName = "test.apk"
                }
            }.download(success={ data: File ->
                data.log()

            }, fail={ call: Call, exception: Exception ->


            }, progress={ value: Float, total: Long ->
                value.log()
                total.log()
            })

//简单一点~~~~
EDownload(url,fileDir:String,fileName:String,success,fail,progress)
```


## RxJava兼容
只要把`go` 或者`rx`就可以返回`Observable<Response>`o(*￣▽￣*)ブ

```
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

        }.rx(progress = { value, total -> })
	        .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t: Response ->
                        t.log()
                        mTvTest.setText(t.body()?.string())
                    }
```


## 贡献
如果你在使用TakePhoto中遇到任何问题可以提[Issues](https://github.com/Rayhahah/EasyHttp/issues)出来。另外欢迎大家为TakePhoto贡献智慧，欢迎大家[Fork and Pull requests](https://github.com/Rayhahah/EasyHttp)。
喜欢就给个star呗，o(*￣▽￣*)ブ

## 感谢
- [OkHttp](https://github.com/square/okhttp/)
- [RxJava](https://github.com/ReactiveX/RxJava/)


## TODO
- Parser的封装，更好对数据展示前预处理