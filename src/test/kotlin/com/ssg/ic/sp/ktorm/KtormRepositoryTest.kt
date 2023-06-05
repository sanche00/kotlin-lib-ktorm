package com.ssg.ic.sp.ktorm

import com.ssg.ic.sp.db.DBConnect
import com.ssg.ic.sp.db.DualDataBase
import com.ssg.ic.sp.db.Repository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.database.Transaction
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import javax.sql.DataSource

class KtormRepositoryTest {

    lateinit var ktormDatabase: KtormDatabase
    lateinit var dualDataBase: DualDataBase<Database>

    @BeforeEach
    fun before() {

        val databaseConnection = { dataSource: DataSource ->
            Database.connect(
                dataSource,
                logger = ConsoleLogger(threshold = LogLevel.INFO)
            )
        }
        dualDataBase = DualDataBase(
            writeConnect = DBConnect(
                url = "jdbc:h2:mem:test;MODE=PostgreSQL;INIT=RUNSCRIPT FROM './test-sql/create-city.sql';DATABASE_TO_UPPER=false;CASE_INSENSITIVE_IDENTIFIERS=true",
                driver = "org.h2.Driver",
                user = "sa"
            ),
            databaseConnection = databaseConnection
        )
        ktormDatabase = KtormDatabase(dualDataBase)
    }

    @Test
    fun savaTest() {

        val cityRepository = CityRepositoryImpl(KtormRepository(ktormDatabase))

        var ret = cityRepository.save(City {
            id = 1
            name = "test"
        }, cityRepository.writeDatabase())

        assertEquals(1, ret)

        val fakeCityRepository = FakeCityRepository(FakeRepository())
        ret =  fakeCityRepository.transaction { database, _ ->
            fakeCityRepository.save(City {
                id = 2
                name = "test"
            }, database)
        }

        assertEquals(1, ret)
    }
}

class CityRepositoryImpl(repository: Repository<Database, Transaction>) : CityRepository<Database, Transaction>, Repository<Database, Transaction> by repository {
    override fun save(city: City, database: Database) = database.tbaCities.add(city)

}

interface City : Entity<City> {
    companion object : Entity.Factory<City>()
    var id: Int
    var name: String

}

object Cities : org.ktorm.schema.Table<City>("tba_city") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}


val Database.tbaCities get() = this.sequenceOf(Cities)


class FakeRepository:Repository<Any, Any> {

    override fun <T> readTransaction(database: Any?, block: (database: Any, transaction: Any?) -> T): T {
        return block(database?:readDatabase(), Any())
    }

    override fun writeDatabase(): Any {
        return Any()
    }

    override fun readDatabase(): Any {
        return Any()
    }

    override fun nextval(sequence: String): Long {
        return 1
    }

    override fun <T> transaction(database: Any?, block: (database: Any, transaction: Any?) -> T): T {
        return block(database?:writeDatabase(), Any())
    }

}

interface CityRepository<DATABASE, TRAN> : Repository<DATABASE, TRAN> {
    fun save(city: City, database: DATABASE): Int
}

class FakeCityRepository(repository: Repository<Any, Any>) : CityRepository<Any, Any>, Repository<Any, Any> by repository {

    val memoryCities = mutableListOf<City>()

    override fun save(city: City, database: Any) = if(memoryCities.add(city)) 1 else 0

}
