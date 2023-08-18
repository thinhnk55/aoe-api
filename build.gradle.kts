plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

group = "vn.vietdefi.game"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.api-client:google-api-client:1.33.0")
    // https://mvnrepository.com/artifact/org.apache.ibatis/ibatis-core
    implementation("org.apache.ibatis:ibatis-core:3.0")
    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.2")
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    implementation("com.zaxxer:HikariCP:4.0.3")
    // https://mvnrepository.com/artifact/io.lettuce/lettuce-core
    implementation("io.lettuce:lettuce-core:6.2.3.RELEASE")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.5")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12
    implementation("org.slf4j:slf4j-log4j12:2.0.5")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
    // https://mvnrepository.com/artifact/io.vertx/vertx-web
    implementation("io.vertx:vertx-web:4.4.0")
    // https://mvnrepository.com/artifact/io.vertx/vertx-core
    implementation("io.vertx:vertx-core:4.4.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.12.0")
    // https://mvnrepository.com/artifact/commons-logging/commons-logging
    implementation("commons-logging:commons-logging:1.2")
    // https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
    implementation("org.apache.poi:poi-ooxml:5.2.2")
    // https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    // https://mvnrepository.com/artifact/com.github.pengrad/java-telegram-bot-api
    implementation("com.github.pengrad:java-telegram-bot-api:6.2.0")
    // https://mvnrepository.com/artifact/com.vdurmont/emoji-java
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}


task("copyDependencies", Copy::class) {
    configurations.compileClasspath.get()
        .filter { it.extension == "jar" }
        .forEach { from(it.absolutePath).into("$buildDir/lib") }
}