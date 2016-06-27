package intricateengineers.intricatemachinery.common.init

import intricateengineers.intricatemachinery.api.module.MachineryFrame
import mcmultipart.multipart.MultipartRegistry

object ModBlocks {

    // Register blocks and multiparts
    def init() {
        MultipartRegistry.registerPart(classOf[MachineryFrame], "machinery_frame")
    }
}
