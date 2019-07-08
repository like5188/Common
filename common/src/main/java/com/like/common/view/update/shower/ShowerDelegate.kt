package com.like.common.view.update.shower

class ShowerDelegate : Shower {
    var shower: Shower? = null

    override fun onDownloadPending() {
        shower?.onDownloadPending()
    }

    override fun onDownloadRunning(currentSize: Long, totalSize: Long) {
        shower?.onDownloadRunning(currentSize, totalSize)
    }

    override fun onDownloadPaused(currentSize: Long, totalSize: Long) {
        shower?.onDownloadPaused(currentSize, totalSize)
    }

    override fun onDownloadSuccessful(totalSize: Long) {
        shower?.onDownloadSuccessful(totalSize)
    }

    override fun onDownloadFailed(throwable: Throwable?) {
        shower?.onDownloadFailed(throwable)
    }

}