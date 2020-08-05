package com.like.common.util

import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object RxJavaUtils {
    /**
     * 延时执行某任务
     *
     * @param interval 延时，单位毫秒
     * @param callbackInMain 回调，在UI线程执行
     */
    @JvmStatic
    fun timer(interval: Long, callbackInMain: (Long) -> Unit): Disposable = timer(interval, AndroidSchedulers.mainThread(), callbackInMain)

    /**
     * 延时执行某任务
     *
     * @param interval 延时，单位毫秒
     * @param callback 回调，在scheduler线程执行
     */
    @JvmStatic
    fun timer(interval: Long, scheduler: Scheduler, callback: (Long) -> Unit): Disposable =
            Observable.timer(interval, TimeUnit.MILLISECONDS)
                    .observeOn(scheduler)
                    .subscribe { callback(it) }

    /**
     * 周期性执行某任务
     *
     * @param interval 延时，单位毫秒
     * @param callbackInMain 回调，在UI线程执行
     */
    @JvmStatic
    fun interval(interval: Long, callbackInMain: (Long) -> Unit): Disposable = interval(interval, AndroidSchedulers.mainThread(), callbackInMain)

    /**
     * 周期性执行某任务
     *
     * @param interval 延时，单位毫秒
     * @param callback 回调，在scheduler线程执行
     */
    @JvmStatic
    fun interval(interval: Long, scheduler: Scheduler, callback: (Long) -> Unit): Disposable =
            Observable.interval(interval, TimeUnit.MILLISECONDS)// 隔一段时间产生一个数字，然后就结束，可以理解为延迟产生数字
                    .observeOn(scheduler)
                    .subscribe { callback(it) }

    /**
     * 延迟一段时间，然后以固定周期循环执行某一任务
     *
     * @param callbackInMain 回调，在UI线程执行
     * @param initialDelay 延迟一段时间，毫秒
     * @param period       周期，毫秒
     */
    @JvmStatic
    fun interval(initialDelay: Long, period: Long, callbackInMain: (Long) -> Unit): Disposable =
            interval(initialDelay, period, AndroidSchedulers.mainThread(), callbackInMain)

    /**
     * 延迟一段时间，然后以固定周期循环执行某一任务
     *
     * @param callback 回调，在scheduler线程执行
     * @param initialDelay 延迟一段时间，毫秒
     * @param period       周期，毫秒
     */
    @JvmStatic
    fun interval(initialDelay: Long, period: Long, scheduler: Scheduler, callback: (Long) -> Unit): Disposable =
            Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                    .observeOn(scheduler)
                    .subscribe { callback(it) }

    /**
     * 指定线程执行，UI线程展示
     *
     * @param invoke 执行的任务
     * @param callback 回调，在UI线程
     * @param scheduler 执行任务的线程
     * @param <T>
    </T> */
    @JvmStatic
    fun <T> runAndUpdate(invoke: () -> T, callback: (T) -> Unit, scheduler: Scheduler, error: ((Throwable) -> Unit)? = null): Disposable =
            Observable.create(ObservableOnSubscribe<T> { emitter ->
                emitter.onNext(invoke())
                emitter.onComplete()
            })
                    .subscribeOn(scheduler) // 指定 subscribe() 发生在 scheduler 线程
                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                    .subscribe(
                            { callback(it) },
                            { error?.invoke(it) }
                    )

    /**
     * computation线程执行，UI线程展示
     *
     * @param invoke 执行的任务
     * @param callback 回调，在UI线程
    </T> */
    @JvmStatic
    fun <T> runComputationAndUpdate(invoke: () -> T, callback: (T) -> Unit, error: ((Throwable) -> Unit)? = null): Disposable =
            runAndUpdate(invoke, callback, Schedulers.computation(), error)

    /**
     * IO线程执行，UI线程展示
     *
     * @param invoke 执行的任务
     * @param callback 回调，在UI线程
    </T> */
    @JvmStatic
    fun <T> runIoAndUpdate(invoke: () -> T, callback: (T) -> Unit, error: ((Throwable) -> Unit)? = null): Disposable =
            runAndUpdate(invoke, callback, Schedulers.io(), error)

    /**
     * 防抖动搜索（300毫秒限制）（当N个结点发生的时间太靠近（即发生的时间差小于设定的值T），debounce就会自动过滤掉前N-1个结点。）
     *
     * @param searchEditText
     * @param searchObservable io线程执行的任务，通常为请求接口，配合Retrofit使用
     * @param callback 结果回调，在UI线程
     */
    @JvmStatic
    fun <T> addTextChangedListener(searchEditText: TextView, searchObservable: (String) -> Observable<T>, callback: (T) -> Unit, error: ((Throwable) -> Unit)? = null): Disposable =
            RxTextView.textChanges(searchEditText)
                    .debounce(300, TimeUnit.MILLISECONDS, Schedulers.io())
                    //对用户输入的关键字进行过滤
                    .filter { charSequence -> charSequence.toString().trim { it <= ' ' }.isNotEmpty() }
                    .map(CharSequence::toString)
                    // switchMap替代flatMap，有新的数据发来的时候，之前的就会取消掉。
                    .switchMap { charSequence -> searchObservable(charSequence) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { callback(it) },
                            { error?.invoke(it) }
                    )

    /**
     * 防抖动按钮点击（指定时间间隔内只触发一次点击事件）
     *
     * @param interval      指定时间间隔(毫秒)
     * @param view
     * @param clickListener
     */
    @JvmStatic
    fun addOnClickListener(interval: Long, view: View, clickListener: View.OnClickListener): Disposable =
            RxView.clicks(view)
                    .throttleFirst(interval, TimeUnit.MILLISECONDS)
                    .subscribe { clickListener.onClick(view) }

}