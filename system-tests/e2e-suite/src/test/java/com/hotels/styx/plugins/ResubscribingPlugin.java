/*
  Copyright (C) 2013-2018 Expedia Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.hotels.styx.plugins;

import com.hotels.styx.api.HttpRequest;
import com.hotels.styx.api.HttpResponse;
import com.hotels.styx.api.plugins.spi.Plugin;
import rx.Observable;

public class ResubscribingPlugin implements Plugin {
    @Override
    public Observable<HttpResponse> intercept(HttpRequest request, Chain chain) {
        return Observable.just(request)
                .map(chain::proceed)
                .flatMap(ResubscribingPlugin::resubscribe);
    }

    private static Observable<HttpResponse> resubscribe(Observable<HttpResponse> responseObservable) {
        return responseObservable
                .filter(response -> false)
                .switchIfEmpty(responseObservable);
    }
}
