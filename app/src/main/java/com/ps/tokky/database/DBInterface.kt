package com.ps.tokky.database

interface DBInterface<T> {
    fun add(item: T): Boolean
    fun update(item: T)
    fun get(itemId: String): T?
    fun getAll(reload: Boolean = true): ArrayList<T>
    fun remove(itemId: String)
}