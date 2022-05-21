package de.geistlande.monsterhunt

import org.jetbrains.annotations.PropertyKey
import java.text.MessageFormat
import java.util.ResourceBundle

object Localizer {
    private val resources = ResourceBundle.getBundle("monsterhunt.localization", Settings.config.locale)

    fun getString(@PropertyKey(resourceBundle = "monsterhunt.localization") key: String): String = resources.getString(key)
    fun getString(@PropertyKey(resourceBundle = "monsterhunt.localization") key: String, vararg params: Any): String = MessageFormat.format(resources.getString(key), params)
}
