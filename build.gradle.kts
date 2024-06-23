
import java.util.*

plugins {
    kotlin("jvm") version "1.9.22"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {

    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
}
