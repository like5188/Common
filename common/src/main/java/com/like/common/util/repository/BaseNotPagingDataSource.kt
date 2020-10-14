package com.like.common.util.repository

import androidx.lifecycle.MutableLiveData
import com.like.repository.notPaging.NotPagingDataSource
import com.like.repository.RequestType
import kotlinx.coroutines.CoroutineScope

abstract class BaseNotPagingDataSource<ResultType>(
        coroutineScope: CoroutineScope,
        liveValue: MutableLiveData<ResultType?> = MutableLiveData()
) : NotPagingDataSource<ResultType?>(coroutineScope, liveValue) {

    override suspend fun load(requestType: RequestType): ResultType? {
        val resultModel = loadResultModel(requestType)
        if (!resultModel.success()) {
            throw ResultModelException(resultModel.code(), resultModel.errorMessage())
        }
        return resultModel.data()
    }

    abstract suspend fun loadResultModel(requestType: RequestType): IResultModel<ResultType?>
}
