/**
 * Copyright © 2010-2011 Nokia
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

package com.googlecode.jsonschema2pojo.integration.ref;

import static com.googlecode.jsonschema2pojo.integration.util.CodeGenerationHelper.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class AbsoluteRefIT {

    @Test
    public void absoluteRefIsReadSuccessfully() throws ClassNotFoundException, NoSuchMethodException, IOException {

        File schemaWithAbsoluteRef = createSchemaWithAbsoluteRef();

        File generatedOutputDirectory = generate(schemaWithAbsoluteRef.toURI().toURL(), "com.example", false, false);
        Class<?> absoluteRefClass = compile(generatedOutputDirectory).loadClass("com.example.AbsoluteRef");

        Class<?> addressClass = absoluteRefClass.getMethod("getAddress").getReturnType();

        assertThat(addressClass.getName(), is("com.example.Address"));
        assertThat(addressClass.getMethods(), hasItemInArray(hasProperty("name", equalTo("getPostal_code"))));

    }

    private File createSchemaWithAbsoluteRef() throws IOException {

        URL absoluteUrlForAddressSchema = this.getClass().getResource("/schema/ref/address.json");

        String absoluteRefSchemaTemplate = IOUtils.toString(this.getClass().getResourceAsStream("/schema/ref/absoluteRef.json.template"));
        String absoluteRefSchema = absoluteRefSchemaTemplate.replace("$ABSOLUTE_REF", absoluteUrlForAddressSchema.toString());

        File absoluteRefSchemaFile = File.createTempFile("absoluteRef", ".json");

        try {
            FileOutputStream outputStream = new FileOutputStream(absoluteRefSchemaFile);
            try {
                IOUtils.write(absoluteRefSchema, outputStream);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        } finally {
            absoluteRefSchemaFile.deleteOnExit();
        }

        return absoluteRefSchemaFile;

    }

}