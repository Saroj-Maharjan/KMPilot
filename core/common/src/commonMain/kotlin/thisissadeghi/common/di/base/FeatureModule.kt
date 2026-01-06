package thisissadeghi.common.di.base

import org.koin.core.module.Module

/**
 * Interface that each feature module must implement to provide its Koin modules
 */
interface FeatureModule {
    val moduleName: String

    fun getKoinModules(): List<Module>

    fun initialize()
}

/**
 * Base class for common feature initialization logic
 */
abstract class BaseFeature(
    override val moduleName: String,
) : FeatureModule {
    init {
        // Auto-register when instantiated
        FeatureRegistry.registerFeature(this)
    }
}

/**
 * Registry for all feature modules in the application
 */
object FeatureRegistry {
    private val features = mutableMapOf<String, FeatureModule>()

    /**
     * Register a feature module
     */
    fun registerFeature(feature: FeatureModule) {
        features[feature.moduleName] = feature
    }

    /**
     * Get all Koin modules from all registered features
     */
    fun getAllKoinModules(): List<Module> = features.values.flatMap { it.getKoinModules() }
}
