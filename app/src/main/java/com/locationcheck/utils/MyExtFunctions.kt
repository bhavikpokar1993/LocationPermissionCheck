package com.locationcheck.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.locationcheck.BuildConfig
import com.locationcheck.R

import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
/*operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}*/

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}
/**
 * Return trimmed text of EditText
 * */
fun EditText.getTrimText(): String = text.toString().trim()

/**
 * Return true If EditText is empty otherwise false
 * */
fun EditText.isEmpty(): Boolean = TextUtils.isEmpty(text.toString().trim())

/**
 * Return true If EditText is not empty otherwise false
 * */
fun EditText.isNotEmpty(): Boolean = !isEmpty()

inline fun EditText.afterTextChanged(crossinline listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            listener(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}

inline fun EditText.setOnRightDrawableClickListener(crossinline listener: () -> Unit) {
    setOnTouchListener(View.OnTouchListener { v, event ->
        // val DRAWABLE_LEFT = 0
        // val DRAWABLE_TOP = 1
        val drwableRight = 2
        // val DRAWABLE_BOTTOM = 3
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= right - compoundDrawables[drwableRight].bounds.width()) {
                listener()
                return@OnTouchListener true
            }
        }
        false
    })
}


fun isValidPassword(password: String): Boolean {

    val pattern: Pattern
    val matcher: Matcher

    val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$"

    pattern = Pattern.compile(PASSWORD_PATTERN)
    matcher = pattern.matcher(password)

    return matcher.matches()

}


/**
 * Enable/Disable EditText to editable
 * */
fun EditText.setEditable(enable: Boolean) {
    isFocusable = enable
    isFocusableInTouchMode = enable
    isClickable = enable
    isCursorVisible = enable
}

/*
* Make EditText Scrollable inside scrollview
* */
fun EditText.makeScrollableInScrollView() {
    setOnTouchListener(View.OnTouchListener { v, event ->
        if (hasFocus()) {
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
            }
        }
        false
    })
}

fun dLog(message: String) {
    Log.d(TAG, message)
}

fun vLog(message: String) {
    Log.v(TAG, message)
}

fun eLog(message: String) {
    Log.e(TAG, message)
}


/*
* Show Error at TextInputLayout
* */
/*fun TextInputLayout.showError(errorText: String) {
    if (errorText.isNotEmpty()) {
        isErrorEnabled = true
        error = errorText
    }
}*/

/*
* Hide error at TextInputLayout
* */
/*fun TextInputLayout.hideError() {
    isErrorEnabled = false
    error = null
}*/


/**
 * Check minimum length of EditText content
 * */
fun EditText.hasMinLength(minLength: Int): Boolean {
    return getTrimText().length >= minLength
}

/*
* Run Block of function on debug mode
* */
inline fun debugMode(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}

/*
* Execute block if OS version is greater or equal Lolipop(21)
* */
inline fun lollipopAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        block()
    }
}

/*
* Execute block if OS version is greater than or equal Naugat(24)
* */
inline fun nougatAndAbove(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        block()
    }
}


/*
* Execute block into try...catch
* */
inline fun <T> justTry(tryBlock: () -> T) = try {
    tryBlock()
} catch (e: Throwable) {
    e.printStackTrace()
}

// Start new Activity functions

/*
* Start Activity from Activity
* */
inline fun <reified T : Any> Context.launchActivity(
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

/*
* Start Activity from Activity
* */
inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (requestCode == -1)
        startActivity(intent)
    else
        startActivityForResult(intent, requestCode)
}


/*
* Start Activity from fragment
* */
/*operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}*/

/*
* getting current milliseconds
* for add to Database
* */
fun currentDateToMillis(): Long {
    var calendar = Calendar.getInstance()
    return calendar.timeInMillis
}

fun convertStrToBase64(text: String): String {
    val data = text.toByteArray(charset("UTF-8"))
    val base64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT)

    return base64
}

fun convertBase64ToStr(text: String): String {
    val decrypt =
        android.util.Base64.decode(text, android.util.Base64.DEFAULT).toString(charset("UTF-8"))

    return decrypt
}


inline fun <reified T : Any> Fragment.launchActivity(
    requestCode: Int = -1,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this.context!!)
    intent.init()
    if (requestCode == -1)
        startActivity(intent)
    else
        startActivityForResult(intent, requestCode)

}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)


inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

/**
 * Return simple class name
 * */
fun Any.getClassName(): String {
    return this::class.java.simpleName
}


fun Intent.getInt(key: String, defaultValue: Int = 0): Int {
    return extras?.getInt(key, defaultValue) ?: defaultValue
}

fun Intent.getString(key: String, defaultValue: String = ""): String {
    return extras?.getString(key, defaultValue) ?: defaultValue
}

/*
* Return activity main content view
* */
val Activity.contentView: View?
    get() = findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)


/**
 * Hide/Show view with scale animation
 * */
fun View.setVisibilityWithScaleAnim(visibility: Int) {
    this.clearAnimation()
    this.visibility = View.VISIBLE
    val scale = if (visibility == View.GONE)
        0f
    else
        1f

    val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", scale),
        PropertyValuesHolder.ofFloat("scaleY", scale)
    )
    scaleDown.duration = 300
    scaleDown.interpolator = DecelerateInterpolator()
    scaleDown.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            this@setVisibilityWithScaleAnim.visibility = visibility
        }
    })
    scaleDown.start()
}

fun Context.getAppVersionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

fun Context.showToast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    if (!message.isNullOrEmpty())
        Toast.makeText(this, message, duration).show()
}

fun SpannableString.setClickableSpan(
    start: Int,
    end: Int, @ColorInt color: Int,
    block: (view: View?) -> Unit
) {
    setSpan(object : ClickableSpan() {


        override fun onClick(p0: View) {
            block(p0)
        }

    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    // Set Color Span
    setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

/*
* Toggle integer value, from 0->1 or 1->0
* */
fun Int.toggle() = if (this == 1) 0 else 1

/*
* Return true if view is visible otherwise return false
* */
fun View.isVisible() = visibility == View.VISIBLE

/*
* Set enabled/disable
* */
fun View.setEnabledWithAlpha(enabled: Boolean, disabledAlpha: Float = 0.5f) {
    isEnabled = enabled
    alpha = if (isEnabled) 1f else disabledAlpha
}


fun String?.nullSafe(defaultValue: String = ""): String {
    return this ?: defaultValue
}

fun Int?.nullSafe(defaultValue: Int = 0): Int {
    return this ?: defaultValue
}

fun Long?.nullSafe(defaultValue: Long = 0L): Long {
    return this ?: defaultValue
}

fun Double?.nullSafe(defaultValue: Double = 0.0): Double {
    return this ?: defaultValue
}

fun Boolean?.nullSafe(defaultValue: Boolean = false): Boolean {
    return this ?: defaultValue
}

/*fun ImageView.loadCircleImage(imageUrl: String, @DrawableRes placeholder: Int = R.drawable.placeholder_user,
                              @DrawableRes errorPlaceholder: Int = R.drawable.placeholder_rounded_corner_grey) {
    GlideApp.with(context)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(this)
}

fun ImageView.loadRoundedCornerImage(context: Context, url: String, roundedCorner: Int = context.resources.getDimensionPixelOffset(R.dimen.dp10),
                                     @DrawableRes placeholder: Int = R.drawable.placeholder_rounded_corner_grey,
                                     @DrawableRes errorPlaceholder: Int = R.drawable.placeholder_rounded_corner_grey) {
    GlideApp.with(context)
            .load(url)
            .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(roundedCorner)))
            //.transition(withCrossFade())
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(this)
}*/


/**
 * Return true if build is debug or dev2 or localIP
 * */
/*fun Any.isDebugOrLocalOrDev2Build(): Boolean {
    return BuildConfig.DEBUG || com.base.network.BuildConfig.BASE_URL.contains("dev2") || com.base.network.BuildConfig.BASE_URL.contains("172")
}*/


@SuppressWarnings("deprecation")
fun String?.fromHtml(): Spanned {
    if (this == null)
        return SpannableString("")
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
        // we are using this flag to give a consistent behaviour
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        return Html.fromHtml(this)
    }
}

/**
 * Return ActionBar height
 * */
fun Activity.getActionBarHeight(): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(R.attr.actionBarSize, tv, true))
        TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    else 0
}

fun View.measureWidthHeight(onCompleteMeasure: (width: Int, height: Int) -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            onCompleteMeasure.invoke(measuredWidth, measuredHeight)
            return true
        }
    })
}

fun Drawable.setTintColor(@ColorInt colorTint: Int): Drawable {
    colorFilter = PorterDuffColorFilter(colorTint, PorterDuff.Mode.SRC_ATOP)
    return this
}

fun SearchView.setHintColor(@ColorInt hintColor: Int) {
    (findViewById<EditText>(androidx.appcompat.R.id.search_src_text)).setHintTextColor(hintColor)
}

/**
 * Return Tag of view as string
 * */
fun View.getStringTag(): String = if (tag == null) "" else tag.toString()

fun RadioGroup.getCheckedButtonText(): String {
    return if (checkedRadioButtonId != -1)
        findViewById<RadioButton>(checkedRadioButtonId).text.toString()
    else
        ""

}

fun updateDrawState(ds: TextPaint?) {
    ds?.isUnderlineText = false // set to false to remove underline


}