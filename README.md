E-ticaret API Projesi
Bu proje, Spring Boot kullanılarak geliştirilmiş, temel bir e-ticaret uygulamasının RESTful API'sidir. Ürün yönetimi, sipariş işlemleri ve kullanıcı kimlik doğrulama/yetkilendirme gibi temel e-ticaret fonksiyonelliklerini sağlamaktadır.

🚀 Teknolojiler
Java 23: Programlama dili

Spring Boot 3.2.5: Hızlı uygulama geliştirme çerçevesi

Spring Data JPA

Spring Web

Spring Security (JWT tabanlı)

Spring Validation

Hibernate: JPA implementasyonu

H2 Database: Geliştirme ve test ortamı için in-memory veritabanı

Lombok: Boilerplate kodu azaltmak için

JJWT: JSON Web Token işlemleri için

Springdoc OpenAPI (Swagger UI): API dokümantasyonu için

Maven: Bağımlılık yönetimi ve proje derleme

JUnit 5 & MockMvc: Unit ve Integration testleri için

Docker: Uygulamayı konteynerize etmek için

🏗️ Mimari
Proje, temiz kod prensipleri ve SOLID prensiplerine uygun olarak katmanlı mimari (Layered Architecture) ile tasarlanmıştır:

Controller Katmanı: Gelen HTTP isteklerini karşılar, DTO'ları kullanarak giriş verilerini alır ve servis katmanına yönlendirir. Yanıtları HTTP formatında döndürür.

Service Katmanı: İş mantığını içerir. Controller'dan gelen istekleri işler, Repository katmanı ile iletişim kurar ve iş akışını yönetir. Transactional işlemler burada yönetilir.

Repository Katmanı: Veritabanı ile doğrudan iletişim kurar. Spring Data JPA sayesinde CRUD (Create, Read, Update, Delete) operasyonları kolayca gerçekleştirilir.

Model Katmanı: Veritabanı tablolarını temsil eden JPA varlıklarını (Entities) içerir.

DTO (Data Transfer Object) Katmanı: Katmanlar arasında veri transferi için kullanılan nesnelerdir. Bu, hassas verilerin açığa çıkmasını engeller ve API'nin esnekliğini artırır.

✨ Özellikler ve API Endpoint'leri
Uygulama aşağıdaki temel RESTful API endpoint'lerini sunmaktadır:

1. Ürün İşlemleri (/api/v1/products)
GET /: Tüm ürünleri listeler (sayfalama ve sıralama destekli).

GET /{id}: Belirli bir ID'ye sahip ürünü getirir.

POST /: Yeni bir ürün ekler. (ADMIN Yetkisi Gerekli)

PUT /{id}: Belirli bir ID'ye sahip ürünü günceller. (ADMIN Yetkisi Gerekli)

DELETE /{id}: Belirli bir ID'ye sahip ürünü siler. (ADMIN Yetkisi Gerekli)

GET /category/{category}: Belirli bir kategoriye ait ürünleri listeler.

PATCH /{id}/stock: Belirli bir ürünün stok miktarını günceller. (ADMIN Yetkisi Gerekli)

2. Sipariş İşlemleri (/api/v1/orders)
POST /: Yeni bir sipariş oluşturur. (USER/ADMIN Yetkisi Gerekli)

GET /{id}: Belirli bir siparişin detaylarını getirir. (Kullanıcı kendi siparişini, Admin tüm siparişleri görebilir)

GET /user/{username}: Belirli bir kullanıcının siparişlerini listeler. (Kullanıcı kendi siparişlerini, Admin tüm siparişleri görebilir)

PUT /{id}/status: Belirli bir siparişin durumunu günceller. (ADMIN Yetkisi Gerekli)

DELETE /{id}: Belirli bir siparişi iptal eder (stokları geri ekler). (USER/ADMIN Yetkisi Gerekli)

DELETE /{id}/admin: Belirli bir siparişi veritabanından tamamen siler. (ADMIN Yetkisi Gerekli)

3. Kullanıcı İşlemleri (/api/v1/auth, /api/v1/users)
POST /api/v1/auth/register: Yeni bir kullanıcı kaydı yapar.

POST /api/v1/auth/login: Kullanıcı girişi yapar ve JWT token döndürür.

GET /api/v1/users/profile: Kimliği doğrulanmış kullanıcının profil bilgilerini getirir.

PUT /api/v1/users/profile: Kimliği doğrulanmış kullanıcının profil bilgilerini günceller.

POST /api/v1/users/address: Kimliği doğrulanmış kullanıcıya yeni bir adres ekler.

⚙️ Kurulum ve Çalıştırma
Ön Koşullar
Java 23 JDK

Maven

Docker (isteğe bağlı, Docker ile çalıştırmak isterseniz)

Git

Yerel Kurulum
Projeyi Klonlayın:

git clone <proje_depo_url_buraya>
cd ecommerce

Bağımlılıkları Yükleyin:

mvn clean install

Uygulamayı Çalıştırın:

mvn spring-boot:run

Uygulama varsayılan olarak http://localhost:8080 adresinde çalışacaktır.

Docker ile Çalıştırma
Docker Görüntüsünü Oluşturun:

docker build -t ecommerce-api .

Docker Konteynerini Çalıştırın:

docker run -p 8080:8080 ecommerce-api

Uygulama http://localhost:8080 adresinde Docker konteyneri içinde çalışacaktır.

🗄️ Veritabanı Erişimi (H2 Konsolu)
Uygulama çalışırken H2 veritabanı konsoluna aşağıdaki adresten erişebilirsiniz:
http://localhost:8080/h2-console

Bağlantı Bilgileri:

JDBC URL: jdbc:h2:mem:testdb (veya application.properties dosyanızdaki spring.datasource.url değeri)

User Name: sa

Password: password

🔒 Güvenlik
Uygulama, Spring Security ile JWT (JSON Web Token) tabanlı kimlik doğrulama ve rol tabanlı yetkilendirme (@PreAuthorize) kullanmaktadır.

Kullanıcılar POST /api/v1/auth/login endpoint'inden JWT token alarak korumalı API'lere erişebilirler.

Token, Authorization başlığında Bearer <TOKEN> formatında gönderilmelidir.

Sistemde USER ve ADMIN rolleri bulunmaktadır. Belirli işlemler (ürün ekleme/güncelleme/silme, sipariş durumu güncelleme vb.) sadece ADMIN rolüne sahip kullanıcılar tarafından yapılabilir.

📄 API Dokümantasyonu (Swagger/OpenAPI)
Uygulama çalışırken, API dokümantasyonuna Swagger UI üzerinden erişebilirsiniz:
http://localhost:8080/swagger-ui.html

Bu arayüz, tüm API endpoint'lerini, beklenen istek formatlarını ve olası yanıtları görsel olarak sunar. Ayrıca buradan doğrudan API istekleri de gönderebilirsiniz.

🧪 Testler
Proje, hem birim (Unit) hem de entegrasyon (Integration) testlerini içermektedir:

Unit Testler: Servis katmanındaki iş mantığını izole bir şekilde test eder. (Örn: ProductServiceTest.java)

Integration Testler: Controller katmanı ve veritabanı entegrasyonu dahil olmak üzere uçtan uca senaryoları test eder. (Örn: ProductControllerIT.java, OrderControllerIT.java)

Testleri çalıştırmak için:

mvn test

🤝 Katkıda Bulunma
Projenin geliştirilmesine katkıda bulunmak isterseniz, lütfen bir "pull request" açmadan önce değişikliklerinizi ayrı bir dalda yapın.

📝 Lisans
Bu proje MIT Lisansı altında lisanslanmıştır. Daha fazla bilgi için LICENSE dosyasına bakın.

✉️ İletişim
Adınız Soyadınız: Emre Tek

E-posta: emretek443@gmail.com

