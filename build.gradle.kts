group = "com.irotsoma.homeinventorymanager"
version = "1.0"

val kotlinLoggingVersion = "1.8.0.1"
//val springContentVersion="1.1.0.M2"
val tikaVersion="1.25"
val webValidationVersion = "1.3"
//val jaxbVersion="2.3.1"
val bootstrapVersion = "4.5.0"
val jqueryVersion = "3.5.1"
val popperVersion = "1.12.9-1"
val webjarsLocatorVersion = "0.40"
val javaxValidationVersion = "2.0.1.Final"
val passayVersion = "1.6.0"
val openIconicVersion = "1.1.1"
val dataTablesVersion = "1.10.21"
val jasperVersion = "6.16.0"

plugins {
    val kotlinVersion = "1.4.31"
    val springBootVersion = "2.4.3"
    val liquibaseGradleVersion = "2.0.4"
    val dokkaVersion = "1.4.20"
    val springDependencyManagementVersion = "1.0.11.RELEASE"
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
    implementation( "org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    //implementation( "org.springframework.boot:spring-boot-starter-actuator")
    implementation( "org.springframework.boot:spring-boot-starter-security")
    implementation( "org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //mongodb
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    //Neo4j
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    //mariaDb
    implementation( "org.mariadb.jdbc:mariadb-java-client")
    //H2 DB
    implementation("com.h2database:h2")
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
    implementation("org.webjars.bower:open-iconic:$openIconicVersion")
    implementation("org.webjars:datatables:$dataTablesVersion")
    //web validation library
    implementation("com.irotsoma.web:validation:$webValidationVersion")
    implementation("javax.validation:validation-api:$javaxValidationVersion")
    implementation("org.passay:passay:$passayVersion")
    //jasper
    implementation("net.sf.jasperreports:jasperreports:$jasperVersion")
    implementation("com.lowagie:itext:2.1.7")
    //spring boot tools
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

configurations.all{
    //exclude spring boot logging and logback as it will conflict with kotlin logging
    exclude(module = "spring-boot-starter-logging")
    exclude(module = "logback-classic")
    //exclude junit 4 as it causes conflict with junit 5 in spring boot
    exclude("junit","junit")
    exclude("org.junit.vintage","junit-vintage-engine")
}
kapt.includeCompileClasspath = false
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    launchScript()
}
tasks.test{
    if (project.hasProperty("args")) {
        project.logger.info("Gradle received test args: ${project.properties["args"].toString()}")
        project.properties["args"].toString().split(",").forEach{
            val pair = it.replace("--", "").split("=")
            systemProperty(pair[0],pair[1])
        }
    }
    useJUnitPlatform()
}