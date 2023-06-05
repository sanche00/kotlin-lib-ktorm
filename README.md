# Kotlin Ktorm Repository 
kotlin 학습용으로 만든 소스입니다.

kotlin-db-common 을 사용하여 Kotlin orm ktorm 용 repository 구현

### 사용방법

1) KtormDatabase 생성

```aidl
val ktormDatabase = KtormDatabase(dualDataBase)
```

2) Repository 
```aidl
interface CityRepository<DATABASE, TRAN> : Repository<DATABASE, TRAN> {
    fun save(city: City, database: DATABASE): Int
}

class CityRepositoryImpl(repository: Repository<Database, Transaction>) : CityRepository<Database, Transaction>, Repository<Database, Transaction> by repository {
    override fun save(city: City, database: Database) = database.tbaCities.add(city)

}
```

3) 사용
```aidl
val cityRepository = CityRepositoryImpl(KtormRepository(ktormDatabase))

var ret = cityRepository.save(City {
    id = 1
    name = "test"
}, cityRepository.writeDatabase())

```

4) test Fake Bean
```aidl
class FakeCityRepository(repository: Repository<Any, Any>) : CityRepository<Any, Any>, Repository<Any, Any> by repository {

    val memoryCities = mutableListOf<City>()

    override fun save(city: City, database: Any) = if(memoryCities.add(city)) 1 else 0

}
```

* KtormRepositoryTest.kt 참조