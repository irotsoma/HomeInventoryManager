group = "com.irotsoma.homeinventorymanager"
version = "1.0-SNAPSHOT"

val kotlinLoggingVersion = "1.8.0.1"
val springContentVersion="1.1.0.M2"
val tikaVersion="1.24.1"
val webValidationVersion = "1.2"
val jaxbVersion="2.3.1"
val bootstrapVersion = "4.5.0"
val jqueryVersion = "3.5.1"
val popperVersion = "1.12.9-1"
val webjarsLocatorVersion = "0.40"
val javaxValidationVersion = "2.0.1.Final"
val passayVersion = "1.4.0"

plugins {
    val kotlinVersion = "1.3.72"
    val springBootVersion = "2.3.1.RELEASE"
    val liquibaseGradleVersion = "2.0.4"
    val dokkaVersion = "0.10.1"
    val springDependencyManagementVersion = "1.0.9.RELEASE"
    java
    id("io.spring.dependency-management") version springDependencyManagementVersion
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("org.jetbrains.dokka") version dokkaVersion
    id("org.springframework.boot") version springBootVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.liquibase.gradle") version liquibaseGradleVersion
}



repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    gradlePluginPortal()

}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    //spring
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation( "org.springframework.boot:spring-boot-starter-actuator")
    implementation( "org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation( "org.springframework.boot:spring-boot-starter-security")
    implementation( "org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation ("org.springframework.boot:spring-boot-starter-integration")
    //mariaDb
    implementation( "org.mariadb.jdbc:mariadb-java-client")
    //liquibase
    implementation( "org.liquibase:liquibase-core")
    //tika
    implementation("org.apache.tika:tika-core:$tikaVersion")
    //logging
    implementation( "io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    //test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    //javascript
    implementation("org.webjars:bootstrap:$bootstrapVersion")
    implementation("org.webjars:jquery:$jqueryVersion")
    implementation("org.webjars:popper.js:$popperVersion")
    implementation("org.webjars:webjars-locator:$webjarsLocatorVersion")
    //web validation library
    implementation("com.irotsoma.web:validation:$webValidationVersion")
    implementation("javax.validation:validation-api:$javaxValidationVersion")
    implementation("org.passay:passay:$passayVersion")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

//exclude spring boot logging as it will conflict with kotlin logging
configurations.all{
    exclude(module = "spring-boot-starter-logging")
    exclude(module = "logback-classic")
}
kapt.includeCompileClasspath = false