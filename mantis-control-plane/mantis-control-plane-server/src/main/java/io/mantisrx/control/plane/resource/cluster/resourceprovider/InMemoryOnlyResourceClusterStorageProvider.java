/*
 * Copyright 2022 Netflix, Inc.
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

package io.mantisrx.control.plane.resource.cluster.resourceprovider;

import io.mantisrx.control.plane.resource.cluster.writable.RegisteredResourceClustersWritable;
import io.mantisrx.control.plane.resource.cluster.writable.ResourceClusterSpecWritable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * [Test only] Store resource storage data in memroy only for testing.
 */
public class InMemoryOnlyResourceClusterStorageProvider implements IResourceClusterStorageProvider {
    Map<String, ResourceClusterSpecWritable> clusters = new HashMap<>();

    @Override
    public CompletionStage<ResourceClusterSpecWritable> registerAndUpdateClusterSpec(ResourceClusterSpecWritable spec) {
        this.clusters.put(spec.getId(), spec);
        return CompletableFuture.completedFuture(spec);
    }

    @Override
    public CompletionStage<RegisteredResourceClustersWritable> getRegisteredResourceClustersWritable() {
        RegisteredResourceClustersWritable.RegisteredResourceClustersWritableBuilder builder =
                RegisteredResourceClustersWritable.builder();

        this.clusters.entrySet().stream().forEach(kv -> {
            builder.cluster(kv.getKey(), RegisteredResourceClustersWritable.ClusterRegistration.builder()
                    .clusterId(kv.getKey()).version(kv.getValue().getVersion()).build());
        });

        return CompletableFuture.completedFuture(builder.build());
    }

    @Override
    public CompletionStage<ResourceClusterSpecWritable> getResourceClusterSpecWritable(String id) {
        return CompletableFuture.completedFuture(this.clusters.getOrDefault(id, null));
    }
}
