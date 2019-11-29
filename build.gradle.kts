import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
	id ("org.jetbrains.kotlin.plugin.jpa") version "1.3.60"
}

group = "com.besseggen"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.security.oauth:spring-security-oauth2:2.3.8.RELEASE")
	implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.2.0.RELEASE")
	implementation("org.springframework.security:spring-security-jwt:1.1.0.RELEASE")
	implementation("org.springframework.session:spring-session-jdbc:2.2.0.RELEASE")
	implementation("com.nimbusds:nimbus-jose-jwt:8.2.1")
	implementation("mysql:mysql-connector-java:8.0.18")
	implementation("com.zaxxer:HikariCP:3.4.1")
	implementation("org.flywaydb:flyway-core:6.0.8")
	implementation("org.springframework.security:spring-security-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-jose")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
