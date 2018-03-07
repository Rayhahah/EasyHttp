package com.rayhahah.library.http

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
 * @time 2018/3/1
 * @tips 这个类是Object的子类
 * @fuction
 */
enum class HttpStatus(val code: Int, val message: String) {


    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(100, "Continue"),

    OK(100, "Continue"),
    CREATED(100, "Continue"),
    Accepted(100, "Continue"),
    NON_AUTHORITATIVE_INFORMATION(100, "Continue"),
    NO_CONTENT(100, "Continue"),
    RESET_CONTENT(100, "Continue"),

    MULTIPLE_CHOICES(100, "Continue"),
    MOVED_PERMANENTLY(100, "Continue"),
    FOUND(100, "Continue"),
    SEE_OTHER(100, "Continue"),
    USE_PROXY(100, "Continue"),
    UNUSED(100, "Continue"),
    TEMPORARY_REDIRECT(100, "Continue"),

    BAD_REQUEST(100, "Continue"),
    PAYMENT_REQUIRED(100, "Continue"),
    FORBIDDEN(100, "Continue"),
    NOT_FOUND(100, "Continue"),
    METHOD_NOT_ALLOWED(100, "Continue"),
    NOT_ACCEPTABLE(100, "Continue"),
    REQUEST_TIMEOUT(100, "Continue"),
    CONFLICT(100, "Continue"),
    GONE(100, "Continue"),
    LENGTH_REQUIRED(100, "Continue"),
    PAYLOAD_TOO_LARGE(100, "Continue"),
    URI_TOO_LONG(100, "Continue"),
    UNSUPPORTED_MEDIA_TYPE(100, "Continue"),
    FAILED(100, "Continue"),
    UPGRADE_REQUIRED(100, "Continue"),

    INTERNAL_SERVER_ERROR(100, "Continue"),
    NOT_IMPLEMENTED(100, "Continue"),
    BAD_GATEWAY(100, "Continue"),
    SERVICE_UNAVAILABLE(100, "Continue"),
    GATEWAY_TIMEOUT(100, "Continue"),
    HTTP_VERSION_NOT_SUPPORTED(100, "Continue");

    fun isSuccess(): Boolean {
        val value = code / 100
        return value == 2
    }

    fun getValue(value: Int): HttpStatus? {
        return values().firstOrNull { value == it.code }
    }
}