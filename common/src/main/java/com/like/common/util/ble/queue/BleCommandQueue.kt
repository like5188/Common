package com.like.common.util.ble.queue

import android.bluetooth.BluetoothGatt
import com.like.common.util.ble.model.BleCommand

class BleCommandQueue {
    private val mLinkedBlockingQueue = BleLinkedBlockingQueue<BleCommand>()

    fun put(command: BleCommand) {
        mLinkedBlockingQueue.put(command)
    }

    fun writeUntilCompleted(gatt: BluetoothGatt) {
        val c: Int
        val count = mLinkedBlockingQueue.count
        val takeLock = mLinkedBlockingQueue.takeLock
        takeLock.lockInterruptibly()
        try {
            while (count.get() == 0) {
                mLinkedBlockingQueue.notEmpty.await()
            }
            val cmd = mLinkedBlockingQueue.head.next.item
            cmd.write(gatt)
            while (!cmd.isCompleted) {
                mLinkedBlockingQueue.notEmpty.await()
            }
            mLinkedBlockingQueue.dequeue()
            c = count.getAndDecrement()
            if (c > 1)
                mLinkedBlockingQueue.notEmpty.signal()
        } finally {
            takeLock.unlock()
        }
        if (c == mLinkedBlockingQueue.capacity)
            mLinkedBlockingQueue.signalNotFull()
    }

    fun clear() {
        mLinkedBlockingQueue.clear()
    }
}