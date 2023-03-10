plugins {
    id("java")
}

group = "fun.vari"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation ("info.picocli:picocli:4.7.1" )
    annotationProcessor("info.picocli:picocli-codegen:4.7.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}