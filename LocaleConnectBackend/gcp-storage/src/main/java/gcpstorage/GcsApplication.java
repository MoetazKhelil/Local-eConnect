package gcpstorage;

/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * An example Spring Boot application that reads and writes files stored in Google Cloud Storage
 * (GCS) using the Spring Resource abstraction and the gs: protocol prefix.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GcsApplication.class, args);
    }
}
