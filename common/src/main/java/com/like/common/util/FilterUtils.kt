package com.like.common.util

class FilterUtils {
    fun filter(searchStr: String, infos: List<Filterable>): List<Filterable> {
        val filterList = mutableListOf<Filterable>()
        val sb = StringBuilder()
        infos.filter { it.getFilterString().length >= searchStr.length }
                .forEach {
                    sb.delete(0, sb.length)
                    sb.append(searchStr)
                    it.clearHighLightPositions()
                    if (it.getFilterString() == sb.toString()) {
                        it.addHighLightPositions(0 until sb.length)
                        filterList.add(it)
                    } else {
                        it.getFilterString().forEachIndexed { i, c ->
                            if (sb.isNotEmpty()) {
                                val index = sb.indexOf(c)
                                if (index >= 0) {
                                    sb.delete(0, index + 1)
                                    it.addHighLightPosition(i)
                                }
                            }
                        }
                        // it.getHighLightPositionsSize() == searchStr.length表示必须要匹配所有的searchStr字符
                        if (it.getHighLightPositionsSize() > 0 && it.getHighLightPositionsSize() == searchStr.length) {
                            filterList.add(it)
                        } else {
                            it.clearHighLightPositions()
                        }
                    }
                }
        return filterList
    }

    interface Filterable {
        fun getFilterString(): String
        fun getHighLightPositionList(): MutableList<Int>

        fun clearHighLightPositions() {
            getHighLightPositionList().clear()
        }

        fun addHighLightPositions(positions: IntRange) {
            getHighLightPositionList().addAll(positions)
        }

        fun addHighLightPosition(position: Int) {
            getHighLightPositionList().add(position)
        }

        fun getHighLightPositionsSize() = getHighLightPositionList().size

        fun getHighLightPositionArray() = getHighLightPositionList().toIntArray()
    }
}
