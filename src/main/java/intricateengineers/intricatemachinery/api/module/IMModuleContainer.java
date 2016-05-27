/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intricateengineers.intricatemachinery.api.module;

import mcmultipart.multipart.MultipartContainer;
import mcmultipart.util.IWorldLocation;

/**
 * This is the root of all the black magic we conduct
 * Or at least i think so, at the time of writing it only has a constructor
 * @author topisani
 */
public class IMModuleContainer extends MultipartContainer {

    public IMModuleContainer(IWorldLocation location, boolean canTurnIntoBlock) {
        super(location, canTurnIntoBlock);
    }
}
