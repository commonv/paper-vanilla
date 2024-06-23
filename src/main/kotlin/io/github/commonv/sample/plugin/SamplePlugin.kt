package io.github.commonv.sample.plugin

import org.bukkit.plugin.java.JavaPlugin

class SamplePlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Hello world!")
    }
}
