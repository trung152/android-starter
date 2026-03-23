---
name: implement_local_db
description: Kỹ năng tích hợp Room Database cho một feature. Dùng khi user cần lưu dữ liệu offline, cache, hoặc offline-first strategy.
---

# Skill: Implement Local Database (Room)

## Khi nào dùng Skill này?

- "Tôi muốn lưu dữ liệu offline"
- "Thêm caching cho feature X"
- "Implement offline-first cho màn hình Y"

---

## Thông tin cần thu thập

1. **Tên Entity**: ví dụ `ProductEntity` (tương ứng một bảng trong DB)
2. **Các fields cần lưu**: id, name, price, timestamp...
3. **Các thao tác cần thiết**: chỉ đọc? đọc + ghi? có xóa không?
4. **Có gọi API không?** (nếu có → implement offline-first: API → lưu Room → đọc Room)

---

## Các bước thực hiện

### Bước 1: Tạo Room Entity (tầng Data)

```kotlin
// feature/{featureName}/data/local/entity/{Model}Entity.kt
@Entity(tableName = "{feature_name}_table")
data class {Model}Entity(
    @PrimaryKey val id: String,
    val name: String,
    // ... các fields khác
    val cachedAt: Long = System.currentTimeMillis() // timestamp để invalidate cache
)
```

### Bước 2: Tạo Mapper (Entity ↔ Domain Model)

```kotlin
// feature/{featureName}/data/local/entity/{Model}Entity.kt
// (thêm extension function vào cuối file Entity)

// Entity → Domain Model (dùng khi đọc từ DB lên)
fun {Model}Entity.toDomainModel(): {Model} = {Model}(
    id = this.id,
    name = this.name,
    // ...
)

// Domain Model → Entity (dùng khi ghi xuống DB)
fun {Model}.toEntity(): {Model}Entity = {Model}Entity(
    id = this.id,
    name = this.name,
    // ...
)
```

### Bước 3: Tạo DAO

```kotlin
// feature/{featureName}/data/local/{Model}Dao.kt
@Dao
interface {Model}Dao {

    @Query("SELECT * FROM {feature_name}_table")
    fun getAll(): Flow<List<{Model}Entity>>      // Flow → tự động re-emit khi DB thay đổi

    @Query("SELECT * FROM {feature_name}_table WHERE id = :id")
    suspend fun getById(id: String): {Model}Entity?

    @Upsert                                       // Insert hoặc Update nếu đã tồn tại
    suspend fun upsert(entity: {Model}Entity)

    @Upsert
    suspend fun upsertAll(entities: List<{Model}Entity>)

    @Query("DELETE FROM {feature_name}_table WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM {feature_name}_table")
    suspend fun deleteAll()
}
```

### Bước 4: Tạo (hoặc cập nhật) AppDatabase

```kotlin
// core/database/AppDatabase.kt
@Database(
    entities = [
        // Thêm entity mới vào đây:
        {Model}Entity::class,
    ],
    version = 1,              // Tăng version khi thêm entity/thay đổi schema
    exportSchema = true       // Xuất schema để tạo migration dễ hơn
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun {model}Dao(): {Model}Dao
    // Thêm abstract fun cho DAO mới
}
```

### Bước 5: Cung cấp Database qua Hilt

```kotlin
// core/database/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()

    @Provides
    @Singleton
    fun provide{Model}Dao(db: AppDatabase): {Model}Dao = db.{model}Dao()
}
```

### Bước 6: Tích hợp vào Repository (Offline-First pattern)

```kotlin
class {Feature}RepositoryImpl @Inject constructor(
    private val apiService: {Feature}ApiService,  // Retrofit
    private val dao: {Model}Dao,                  // Room
) : {Feature}Repository {

    /**
     * Offline-First Strategy:
     * 1. Trả về data từ cache (Room) ngay lập tức
     * 2. Gọi API ở background
     * 3. Lưu kết quả mới vào Room
     * 4. Room tự emit dữ liệu mới qua Flow
     */
    override fun get{Feature}List(): Flow<List<{Model}>> =
        dao.getAll()
            .map { entities -> entities.map { it.toDomainModel() } }
            .onStart {
                // Refresh từ server khi bắt đầu (fire-and-forget)
                refreshFromNetwork()
            }

    private suspend fun refreshFromNetwork() = runCatching {
        val remoteData = apiService.get{Feature}List()
        dao.upsertAll(remoteData.map { it.toEntity() })
    }
}
```

---

## Thêm Migration (khi thay đổi schema)

```kotlin
// Trong DatabaseModule, sau khi tăng `version`:
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE {feature_name}_table ADD COLUMN newColumn TEXT NOT NULL DEFAULT ''")
    }
}

// Truyền vào builder:
Room.databaseBuilder(...)
    .addMigrations(MIGRATION_1_2)
    .build()
```

---

## Checklist

- [ ] Entity có `@PrimaryKey`
- [ ] DAO trả về `Flow<>` cho queries theo dõi realtime
- [ ] Mapper tách biệt, đặt cùng file Entity hoặc trong `mapper/`
- [ ] `AppDatabase` đã được cập nhật với entity mới
- [ ] `version` tăng lên nếu có thay đổi schema
- [ ] Migration được viết nếu `version > 1`
- [ ] `DatabaseModule` export DAO qua `@Provides`
