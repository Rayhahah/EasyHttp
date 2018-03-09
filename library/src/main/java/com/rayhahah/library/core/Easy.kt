package com.rayhahah.library.core

import com.rayhahah.library.http.TYPE
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.File

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


fun EHttp(block: EasyHttp.() -> Unit): EasyHttp {
    return EasyHttp().apply { block(this) }
}

fun EGet(url: String, params: HashMap<String, String>): EasyHttp {
    return EHttp {
        baseUrl = url
        type = TYPE.METHOD_GET
        data = {
            data = params
        }
    }
}

fun EPost(url: String, params: HashMap<String, String>): EasyHttp {
    return EHttp {
        baseUrl = url
        type = TYPE.METHOD_POST
        data = {
            data = params
        }
    }
}

fun EDownload(url: String, fileDir: String, fileName: String,
              success: (file: File) -> Unit = { f -> },
              fail: (call: Call, e: Exception) -> Unit = { c, e -> },
              progress: (value: Float, total: Long) -> Unit = { v, t -> }) {
    return EHttp {
        baseUrl = url
        download = {
            this.fileDir = fileDir
            this.fileName = fileName
        }
    }.download(success, fail, progress)
}

fun EClient(block: EasyClient.() -> Unit): OkHttpClient? {
    return EasyClient().apply { block(this) }.init()
}

fun EClient(client: OkHttpClient) {
    return EasyClient().initDefaultClient(client)
}