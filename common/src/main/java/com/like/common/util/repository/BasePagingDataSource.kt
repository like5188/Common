package com.like.common.util.repository

import androidx.lifecycle.MutableLiveData
import com.like.repository.paging.byPageNo.PageNoKeyedPagingDataSource
import com.like.repository.RequestType
import kotlinx.coroutines.CoroutineScope

abstract class BasePagingDataSource<ValueInList>(
        coroutineScope: CoroutineScope,
        pageSize: Int = 10,
        isLoadAfter: Boolean = true,
        liveValue: MutableLiveData<IPagingModel<ValueInList>?> = MutableLiveData()
) : PageNoKeyedPagingDataSource<IPagingModel<ValueInList>?>(
        coroutineScope,
        pageSize,
        isLoadAfter,
        liveValue
) {

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): IPagingModel<ValueInList>? {
        val resultModel = loadResultModel(requestType, pageNo, pageSize)
        if (!resultModel.success()) {
            throw ResultModelException(resultModel.code(), resultModel.errorMessage())
        }
        return resultModel.data()
    }

    abstract suspend fun loadResultModel(requestType: RequestType, pageNo: Int, pageSize: Int): IResultModel<IPagingModel<ValueInList>?>

}