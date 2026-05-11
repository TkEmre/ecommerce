# ############################
# BİRİNCİ AŞAMA: UYGULAMAYI DERLEME (BUILD STAGE)
# Bu aşamada, uygulamanın JAR dosyasını oluşturacağız.
# ############################
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Docker imajı içinde çalışma dizinini ayarla
WORKDIR /app

# Projenin POM dosyasını kopyala. Bu, bağımlılıkları önbelleğe almak için yapılır.
# Sadece POM dosyası değiştiğinde bağımlılıklar tekrar indirilir.
COPY pom.xml .

# Bağımlılıkları indir. Bu, bir sonraki COPY komutundan önce yapılır,
# böylece bağımlılıklar değişmediği sürece bu katman önbellekten kullanılır.
# Derleme bağımlılıkları önbelleğe alınana kadar bekleriz.
RUN mvn dependency:go-offline

# Projenin kalan kaynak kodunu kopyala
COPY src ./src

# Uygulamayı derle ve JAR dosyasını oluştur
# -DskipTests: Testleri çalıştırmaz (Docker build sürecini hızlandırmak için)
# -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true: Güvenli olmayan HTTPS bağlantılarını kabul etmek için (bazı ağ ortamlarında gerekli olabilir)
# Bu satırlar genellikle sadece özel durumlarda gereklidir. Güvenlik açığı yaratabilir.
# Eğer normalde mvn install çalışıyorsa, bu parametrelere ihtiyacın olmaz.
RUN mvn clean install -DskipTests

# ############################
# İKİNCİ AŞAMA: ÇALIŞTIRILABİLİR İMAJ OLUŞTURMA (RUNNING STAGE)
# Bu aşamada, derlenmiş JAR dosyasını içeren daha küçük bir Runtime imajı oluşturacağız.
# ############################
FROM eclipse-temurin:21-jre-alpine

# Docker imajı içinde çalışma dizinini ayarla
WORKDIR /app

# JAR dosyasını birinci aşamadan kopyala
# Derlenmiş JAR dosyası target/ecommerce-0.0.1-SNAPSHOT.jar yolunda olacak
COPY --from=build /app/target/ecommerce-0.0.1-SNAPSHOT.jar ecommerce-app.jar

# Uygulama için bellek ayarları (isteğe bağlı ama önemlidir)
# Spring Boot uygulamanız için JVM heap boyutunu kontrol eder.
# Bu örnek, mevcut RAM'in %75'ini kullanmasını söyler.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

# Spring Boot uygulamasının çalışacağı portu belirt (Dockerfile'da sadece dokümantasyondur, esas port Spring Boot ayarlarında olmalı)
EXPOSE 8080

# Uygulamayı çalıştırma komutu
# `java -jar` komutu ile Spring Boot uygulamasını başlat.
# --spring.profiles.active=prod ile uygulamayı "prod" profiliyle başlatabiliriz.
# Bu, üretim ortamına özel veritabanı ayarlarını vb. kullanmak için faydalıdır.
ENTRYPOINT ["java", "-jar", "ecommerce-app.jar"]

# Veya eğer prod profili kullanmayacaksanız:
# ENTRYPOINT ["java", "-jar", "ecommerce-app.jar"]