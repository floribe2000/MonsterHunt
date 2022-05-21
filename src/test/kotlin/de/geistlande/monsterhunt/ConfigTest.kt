package de.geistlande.monsterhunt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.bukkit.Material
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ConfigTest {

    private val testPath = "junit-test-config.yaml"

    private val mapper = ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
        .apply { registerModule(kotlinModule()) }


    @AfterTest
    fun cleanup() {
        val file = File(testPath)
        if (file.exists()) {
            file.delete()
        }
    }

    @Test
    fun writeDefaultConfig() {
        Settings.load(testPath)
        Settings.save(testPath)
        assertTrue { File(testPath).exists() }
    }

    @Test
    fun loadFile() {
        val config = PluginConfig(selectionTool = Material.WOODEN_SWORD)
        mapper.writeValue(File(testPath), config)
        Settings.load(testPath)
        assertTrue { Settings.config.selectionTool == Material.WOODEN_SWORD }
    }
}
