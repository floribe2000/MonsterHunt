package de.geistlande.monsterhunt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.kotlinModule
import java.io.File

object Settings {
    lateinit var config: PluginConfig
        private set

    private val mapper: ObjectMapper by lazy {
        ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)).apply {
            registerModule(kotlinModule())
        }
    }

    fun load(configPath: String) {
        val configFile = File(configPath)
        if (!configFile.exists()) {
            config = PluginConfig()
            save(configPath)
            return
        }

        config = mapper.readValue(configFile, PluginConfig::class.java)
    }

    fun save(configPath: String) {
        val configFile = File(configPath)
        mapper.writeValue(configFile, config)
    }

}
