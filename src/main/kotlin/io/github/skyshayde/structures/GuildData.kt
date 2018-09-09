package io.github.skyshayde.structures

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.io.Serializable

@Indices(
        Index(value = "ID", type = IndexType.Unique)
)
data class GuildData(
        @Id
        val ID: Long,
        var roles: Map<String, String>
) : Serializable