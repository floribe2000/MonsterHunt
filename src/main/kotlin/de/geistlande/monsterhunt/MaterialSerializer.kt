package de.geistlande.monsterhunt

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.bukkit.Material

class MaterialSerializer : JsonSerializer<Material>() {
    override fun serialize(value: Material, gen: JsonGenerator?, serializers: SerializerProvider?) {
//        gen?.writeString(value)
//        Material.WOODEN_SWORD
    }
}
