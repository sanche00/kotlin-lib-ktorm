package com.ssg.ic.sp.ktorm

import com.ssg.ic.sp.db.Repository
import org.ktorm.database.Database
import org.ktorm.database.Transaction
import org.ktorm.dsl.UpdateStatementBuilder
import org.ktorm.dsl.update
import org.ktorm.schema.BaseTable
import java.sql.ResultSet

class KtormRepository(val ktormDatabase: KtormDatabase) : Repository<Database, Transaction> {

    override fun <T> transaction(database: Database?, block: (database: Database, transaction: Transaction?) -> T): T {
        val db = (database ?: writeDatabase())
        return db.useTransaction { block(db, it) }
    }

    override fun writeDatabase(): Database {
        return ktormDatabase.writeDatabase()
    }

    override fun readDatabase(): Database {
        return ktormDatabase.readDatabase()
    }

    inline fun <reified T : Any> nativeSql(sql: String, block: (ResultSet) -> T): List<T> =
        ktormDatabase.nativeSql(sql, block)

    override fun nextval(sequence: String): Long =
        ktormDatabase.nativeSql(NEXT_VAL.format(sequence)) { it.getLong(1) }.first()

    override fun <T> readTransaction(
        database: Database?,
        block: (database: Database, transaction: Transaction?) -> T
    ): T {
        val db = (database ?: readDatabase())
        return db.useTransaction { block(db, it) }
    }

    //    override fun <T> readTransaction(
//        database: Database?,
//        block: (database: Database, transaction: Transaction?) -> T
//    ): T {
//        val db = (database?:readDatabase())
//        return db.useTransaction { block(db, it) }
//    }
    fun <T : BaseTable<*>> updateByStatement(table: T, block: UpdateStatementBuilder.(T) -> Unit): Int {
        return writeDatabase().update(table, block)
    }
}