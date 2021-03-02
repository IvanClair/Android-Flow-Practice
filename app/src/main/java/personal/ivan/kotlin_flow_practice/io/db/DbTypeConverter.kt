package personal.ivan.kotlin_flow_practice.io.db

import androidx.room.TypeConverter
import personal.ivan.kotlin_flow_practice.io.model.GithubType

class DbTypeConverter {

    @TypeConverter
    fun toUserType(value: String) = enumValueOf<GithubType>(value)

    @TypeConverter
    fun fromUserType(value: GithubType) = value.name
}