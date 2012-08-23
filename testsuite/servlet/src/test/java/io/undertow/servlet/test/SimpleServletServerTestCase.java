/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package io.undertow.servlet.test;

import java.io.IOException;

import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.test.util.TestResourceLoader;
import io.undertow.test.shared.DefaultServer;
import io.undertow.test.shared.HttpClientUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class SimpleServletServerTestCase {


    @BeforeClass
    public static void setup() {

        final PathHandler root = new PathHandler();
        final ServletContainer container = new ServletContainer(root);

        ServletInfo s = new ServletInfo("servlet", SimpleServlet.class)
                .addMapping("/aa");

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(SimpleServletServerTestCase.class.getClassLoader())
                .setContextPath("/servletContext")
                .setDeploymentName("servletContext.war")
                .setResourceLoader(TestResourceLoader.INSTANCE)
                .addServlet(s);

        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        manager.start();

        DefaultServer.setRootHandler(root);
    }

    @Test
    public void testSimpleHttpServlet() throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(DefaultServer.getDefaultServerAddress() + "/servletContext/aa");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(200, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleServlet.HELLO_WORLD, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

}
