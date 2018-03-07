package com.rayhahah.library.parser

import com.rayhahah.library.service.RequestManager
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.lang.reflect.Type

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
 * @time 2018/3/6
 * @tips 这个类是Object的子类
 * @fuction
 */
class FileParser(
        private val mDestFileDir: String,
        private val mdestFileName: String,
        private val progress: (progress: Float, total: Long) -> Unit) : Parser<File> {
    override fun parse(response: Response, type: Type): File? {
        var inputStream: InputStream? = null
        val buf = ByteArray(1024 * 8)
        var len = 0
        var fos: FileOutputStream? = null
        try {
            inputStream = response.body()?.byteStream()
            val total = response.body()?.contentLength()
            var sum: Long = 0

            val dir = File(mDestFileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, mdestFileName)
            fos = FileOutputStream(file)

            len = inputStream?.read(buf)!!
            while (len != -1) {
                sum += len.toLong()
                fos.write(buf, 0, len)
                val finalSum = sum
                RequestManager.mMainHandler.post({ progress(finalSum * 100.0f / total!!, total) })
                len = inputStream.read(buf)
            }
            fos.flush()

            return file

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                response.body()?.close()
                if (inputStream != null) inputStream.close()
            } catch (e: IOException) {
            }

            try {
                if (fos != null) fos.close()
            } catch (e: IOException) {
            }

        }
        return null
    }

    override fun isCanParse(contentType: String, type: Type): Boolean {
        return true
    }

    override fun parse(content: String, type: Type): File? {
        return null
    }

}