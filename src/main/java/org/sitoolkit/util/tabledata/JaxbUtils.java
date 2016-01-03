/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.util.tabledata;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
public class JaxbUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JaxbUtils.class);

    private static Map<Class<?>, JAXBContext> ctxCache = new HashMap<Class<?>, JAXBContext>();

    @SuppressWarnings("unchecked")
    public static <T> T res2obj(Class<T> type, String path) {
        try {
            JAXBContext ctx = ctx(type);
            Unmarshaller u = ctx.createUnmarshaller();
            URL resource = type.getResource(path);

            LOG.info("XMLを読み込みます。{}", resource);

            return (T) u.unmarshal(resource);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    private static JAXBContext ctx(Class<?> type) throws JAXBException {
        JAXBContext ctx = ctxCache.get(type);
        if (ctx == null) {
            ctx = JAXBContext.newInstance(type);
            ctxCache.put(type, ctx);
        }
        return ctx;
    }
}
