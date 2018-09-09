package io.github.skyshayde.structures

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.io.Serializable

@Indices(
        Index(value = "PROJECT_ID", type = IndexType.NonUnique),
        Index(value = "ZONE_NAME", type = IndexType.NonUnique),
        Index(value = "VM_NAME", type = IndexType.Unique)
)
data class ServerData(
        val PROJECT_ID: String,
        val ZONE_NAME: String,
        @Id
        val VM_NAME: String
) : Serializable