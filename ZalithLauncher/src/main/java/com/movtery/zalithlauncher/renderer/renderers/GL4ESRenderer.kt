package com.movtery.zalithlauncher.renderer.renderers

import com.movtery.zalithlauncher.renderer.AbstractRenderer

class GL4ESRenderer : AbstractRenderer {
    override fun getRendererId(): String = "opengles2"

    override fun getUniqueIdentifier(): String = "8b52d82d-8f6d-4d3a-a767-dc93f8b72fc7"

    override fun getRendererName(): String = "GL4ES"

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy { emptyMap() }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libgl4es_114.so"
}