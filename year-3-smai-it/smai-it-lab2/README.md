# Лабораторная работа 2: Основы сетей

**Выполнил: Соколов Даниил**\
**Группа: I2302**

## 1. Цель работы
Освоить базовые сетевые команды и инструменты диагностики в Linux. Научиться анализировать сетевые подключения и маршруты, а также проверять доступность удалённых ресурсов.
## 2. Теоретическое введение
• IP-адрес и маска сети.
• MAC-адрес.
• Маршрутизация: таблица маршрутов.
• DNS: преобразование имён в IP-адреса.
• Утилиты: ip, ping, traceroute, ss, netstat, dig, curl, nc.
## 3. Практические задания
### Часть 1: Базовая диагностика
1. Определите IP-адреса и MAC-адреса всех сетевых интерфейсов вашей машины.
![img](/images/img_1.png)


Интерфейс lo (Loopback)
MAC-адрес: 00:00:00:00:00:00 (специальный, для loopback)
IPv4-адрес: 127.0.0.1/8
IPv6-адрес: ::1/128

Интерфейс enp0s3 (физический/виртуальный адаптер VirtualBox)
MAC-адрес: 08:00:27:ca:c5:fe
IPv4-адрес: 10.0.2.15/24 (маска /24, широковещательный: 10.0.2.255)
IPv6-адреса:
fd17:625c:f037:2:cd6b:2e94:ac14:d871/64 (временный, dynamic temporary)
fd17:625c:f037:2:a00:27ff:feca:c5fe/64 (основной глобальный, dynamic)
fe80::a00:27ff:feca:c5fe/64 (link-local, всегда присутствует у IPv6)


2. Выведите таблицу маршрутизации.
![img](/images/img_2.png)

3. Проверьте доступность узла 8.8.8.8 и сайта google.com с помощью ping.
![img](/images/img_3.png)
![img](/images/img_4.png)

4. Сравните результаты: что произойдёт, если DNS не работает?



При исправной работе DNS оба пинга выполняются успешно. 
Если бы DNS не работал, то пинг по IP (8.8.8.8) прошёл бы, а по доменному имени (google.com) возникла бы ошибка unknown host, так как не удалось бы преобразовать имя в IP.


### Часть 2: Маршруты и трассировка
1. Выполните трассировку (traceroute) до google.com.
![img](/images/img_5.png)
2. Сохраните список промежуточных узлов.
![img](/images/img_6.png)
3. Попробуйте трассировку до локального сервера в вашей сети (если есть).
![img](/images/img_7.png)
### Часть 3: Порты и соединения
1. Определите, какие порты слушает ваша система:
![img](/images/img_8.png)

С помощью команды ss -tuln были определены слушающие порты системы. Активны службы:
DNS-резолвер (порт 53/tcp, 53/udp, адрес 127.0.0.53),
служба печати CUPS (порт 631/tcp, адрес 127.0.0.1),
mDNS (порт 5353/udp).
Также присутствуют временные порты для внутренних соединений.

2. Запустите локальный сервер для теста:
nc -l 12345
и подключитесь к нему с другой вкладки терминала (nc localhost 12345).

![img](/images/img_9.png)


### Часть 4: Работа с DNS
1. Используйте команду dig для запроса IP-адреса домена google.com.
![img](/images/img_10.png)
2. Определите, какой DNS-сервер используется вашей системой.

- Был получен IP-адрес ресурса:
142.250.185.142
Это показывает, что система успешно преобразует доменные имена в IP (DNS работает).
- Определение используемого DNS-сервера:
Система использует локальный резолвер systemd-resolved:
127.0.0.53
 Он перенаправляет запросы к внешним DNS-серверам и кэширует ответы.

3. Попробуйте запросить MX-записи для домена gmail.com.
![img](/images/img_11.png)

    Вывод ANSWER SECTION показал список почтовых серверов Google с приоритетами:
    ```bash
    5    gmail-smtp-in.l.google.com
    10   alt1.gmail-smtp-in.l.google.com
    20   alt2.gmail-smtp-in.l.google.com
    30   alt3.gmail-smtp-in.l.google.com
    40   alt4.gmail-smtp-in.l.google.com
    ```
    Эти записи указывают на серверы, через которые доставляется электронная почта для домена gmail.com.

### Часть 5: Мини-проект «Сетевой отчёт»
Каждый студент выбирает один сайт (например, github.com) и готовит:
1. IP-адреса и DNS-записи сайта.
```bash
dig python.org A      # IPv4
dig python.org AAAA   # IPv6
dig python.org MX     # почтовые серверы
dig python.org NS     # DNS-серверы домена
```
![img](/images/img_12.png)
![img](/images/img_13.png)
![img](/images/img_14.png)
![img](/images/img_15.png)

```
1. IP-адреса и DNS-записи python.org

IPv4 (A-записи):
151.101.0.223
151.101.64.223
151.101.128.223
151.101.192.223

IPv6 (AAAA-записи):
2a04:4e42::223
2a04:4e42:200::223
2a04:4e42:400::223
2a04:4e42:600::223

MX-записи (почтовые серверы):
mail.python.org. (приоритет 50)

NS-записи (DNS-серверы домена):
ns-2046.awsdns-63.co.uk.
ns-981.awsdns-58.net.
ns-1134.awsdns-13.org.
ns-484.awsdns-60.com.
```

2. Трассировку до сервера.

![img](/images/img_16.png)

Первый хоп _gateway (10.0.2.2) - это шлюз VirtualBox NAT, он отвечает на запросы, задержка минимальная (~0.3–0.9 мс).

Все последующие хопы показывают * * *. Это значит, что промежуточные маршрутизаторы не возвращали ответы на TTL-expired или ICMP-пакеты.

Причины:
Промежуточные маршрутизаторы блокируют ICMP или пакеты traceroute (firewall).
VirtualBox NAT скрывает часть маршрута за хост-машиной, поэтому виртуальная машина видит только первый хоп.
Важно: несмотря на * * *, доступность сайта подтверждена успешным ping или curl. Пакеты доходят до сервера, просто промежуточные узлы не раскрывают себя.

3. Список открытых портов.
![img](/images/img_17.png)
Порт 80 (HTTP) открыт и доступен.
Порт 443 (HTTPS) открыт и доступен.
Эти порты соответствуют стандартным веб-сервисам.

4. Заголовки HTTP-ответа.
![img](/images/img_18.png)

    Сервер возвращает HTTP/2 301, что означает постоянное перенаправление на https://www.python.org/.
    Сервер использует Varnish (HTTP-кэширование).
    Присутствует заголовок strict-transport-security, что указывает на обязательное использование HTTPS и защиту от атак типа MITM.
    Заголовок location показывает, куда будет перенаправлен клиент.
5. SSL-сертификат (действителен ли?).

```bash
socolov@socolov-VirtualBox:~$ openssl s_client -connect python.org:443 -servername python.org
CONNECTED(00000003)
depth=2 OU = GlobalSign Root CA - R3, O = GlobalSign, CN = GlobalSign
verify return:1
depth=1 C = BE, O = GlobalSign nv-sa, CN = GlobalSign Atlas R3 DV TLS CA 2025 Q1
verify return:1
depth=0 CN = www.python.org
verify return:1
---
Certificate chain
 0 s:CN = www.python.org
   i:C = BE, O = GlobalSign nv-sa, CN = GlobalSign Atlas R3 DV TLS CA 2025 Q1
   a:PKEY: rsaEncryption, 2048 (bit); sigalg: RSA-SHA256
   v:NotBefore: Mar 12 12:55:26 2025 GMT; NotAfter: Apr 13 12:55:25 2026 GMT
 1 s:C = BE, O = GlobalSign nv-sa, CN = GlobalSign Atlas R3 DV TLS CA 2025 Q1
   i:OU = GlobalSign Root CA - R3, O = GlobalSign, CN = GlobalSign
   a:PKEY: rsaEncryption, 2048 (bit); sigalg: RSA-SHA256
   v:NotBefore: Oct 16 03:08:04 2024 GMT; NotAfter: Oct 16 00:00:00 2026 GMT
---
Server certificate
-----BEGIN CERTIFICATE-----
MIIGeDCCBWCgAwIBAgIQAZTEVjcH/4VSXCbcwu697DANBgkqhkiG9w0BAQsFADBY
MQswCQYDVQQGEwJCRTEZMBcGA1UEChMQR2xvYmFsU2lnbiBudi1zYTEuMCwGA1UE
AxMlR2xvYmFsU2lnbiBBdGxhcyBSMyBEViBUTFMgQ0EgMjAyNSBRMTAeFw0yNTAz
MTIxMjU1MjZaFw0yNjA0MTMxMjU1MjVaMBkxFzAVBgNVBAMMDnd3dy5weXRob24u
b3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsYaKq32RDQB/k6Aq
xdYL7n8uxL4Jx9tg0ajsQP2mp3aUmJU2gWY/M1LrxPRp+qka1JD+0EUkX4taANGq
jMOduBU0JQPQ0Zm4kvpn5APXj+/tYWDpV3y50wNPFt0LBQ3t9neGvOF4AfUlPj2o
WlpSXQVShoq5BP+wJC0t8fyKQt1fLoyORy1sraTztE+nktQP0SWTKLO14mUL9c+r
s4zqTCQ9eb9kc364yME2wrZgGflT/I+2ed4mBaH5jEBeUUTl1WKhKRsYPn57Pr7L
E7mU3hsy88jIbCc/PcxVE3KqjM0BUTf0eIxVkHJrboacjoMkv0RiW7+qIq7y7Hbu
1eGV5wIDAQABo4IDezCCA3cwMwYDVR0RBCwwKoIOd3d3LnB5dGhvbi5vcmeCDCou
cHl0aG9uLm9yZ4IKcHl0aG9uLm9yZzAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYw
FAYIKwYBBQUHAwEGCCsGAQUFBwMCMB0GA1UdDgQWBBToGKpti3+SIAlEStlA4M4+
S8w/bjBXBgNVHSAEUDBOMAgGBmeBDAECATBCBgorBgEEAaAyCgEDMDQwMgYIKwYB
BQUHAgEWJmh0dHBzOi8vd3d3Lmdsb2JhbHNpZ24uY29tL3JlcG9zaXRvcnkvMAwG
A1UdEwEB/wQCMAAwgZ4GCCsGAQUFBwEBBIGRMIGOMEAGCCsGAQUFBzABhjRodHRw
Oi8vb2NzcC5nbG9iYWxzaWduLmNvbS9jYS9nc2F0bGFzcjNkdnRsc2NhMjAyNXEx
MEoGCCsGAQUFBzAChj5odHRwOi8vc2VjdXJlLmdsb2JhbHNpZ24uY29tL2NhY2Vy
dC9nc2F0bGFzcjNkdnRsc2NhMjAyNXExLmNydDAfBgNVHSMEGDAWgBQlxCgR4n2e
MrEhT/t9/+g4UvGS6DBIBgNVHR8EQTA/MD2gO6A5hjdodHRwOi8vY3JsLmdsb2Jh
bHNpZ24uY29tL2NhL2dzYXRsYXNyM2R2dGxzY2EyMDI1cTEuY3JsMIIBfQYKKwYB
BAHWeQIEAgSCAW0EggFpAWcAdwCWl2S/VViXrfdDh2g3CEJ36fA61fak8zZuRqQ/
D8qpxgAAAZWKbJgWAAAEAwBIMEYCIQDmQBE1uiZpF6bjWUuzjyBVySjUwfiCvpyN
JX1u/O4VXQIhAIrm1tBoF3slv7hk5quPc20HJzHSt0T0I8IPa1qP9Nz7AHUAZBHE
bKQS7KeJHKICLgC8q08oB9QeNSer6v7VA8l9zfAAAAGVimyY9gAABAMARjBEAiA0
7kxBklLjSHvWj7Oa9pQW3OhJfS20vhLFNsAbtjj7awIgVOhpz1Q87+1xJZ6bSIva
v1lww/u56OCtafdOqXofjQkAdQAlL5TCKynpbp9BGnIHK2lcW1L/l6kNJUC7/NxR
7E3uCwAAAZWKbJlmAAAEAwBGMEQCIDQn6fZI/6Jwmimi51yzepy4HQlRf4zFOgkq
MkEhr25HAiBre3jCq2HVo9sjb51Q95kpJxs91o3cz+wGsZ0UaRd2EzANBgkqhkiG
9w0BAQsFAAOCAQEAUYqQ3hjSpRFic+Kx1TXgdlW8GQUYhyRPLPeM/E/W8CAbNxjO
bg/fsrrevY64tyaRjkMytyZF+XhaBtVb3d8hgYViQj+IqQlGlQp1oZJX69CKUA82
ivsfNbI8cBvnCi9qEvx5ck1pnzraDvI1C3yIEReb4J6fw3LqBS/u+3NQMEReFA3b
0wwmw0kraX64rlo500EFWZoHyR16ndZXqGcvVdbpXZ3cxJJ4TGltTvfcJAyfcdnj
WGpVoW4kSuHcS9QAlasdkkDa6F26ux8nfv2tKX1fbUB/5rn5W8c/bEq+b3tuWGPQ
PghzTpC7TF5jFhOQVucaQPoj4RAvNMdLXCx8rA==
-----END CERTIFICATE-----
subject=CN = www.python.org
issuer=C = BE, O = GlobalSign nv-sa, CN = GlobalSign Atlas R3 DV TLS CA 2025 Q1
---
No client certificate CA names sent
Peer signing digest: SHA256
Peer signature type: RSA-PSS
Server Temp Key: X25519, 253 bits
---
SSL handshake has read 3381 bytes and written 376 bytes
Verification: OK
---
New, TLSv1.3, Cipher is TLS_AES_128_GCM_SHA256
Server public key is 2048 bit
Secure Renegotiation IS NOT supported
Compression: NONE
Expansion: NONE
No ALPN negotiated
Early data was not sent
Verify return code: 0 (ok)
---
---
Post-Handshake New Session Ticket arrived:
SSL-Session:
    Protocol  : TLSv1.3
    Cipher    : TLS_AES_128_GCM_SHA256
    Session-ID: 80CA1C7194EEC3DCC6DC80107CC4E8B902516029CE185DF8B7D1C853BFDB4F31
    Session-ID-ctx: 
    Resumption PSK: 131648BA7B948C186AED0DC57AD1A9E74E2C182E4BC7652C2B1CF6DC51E2C7B9
    PSK identity: None
    PSK identity hint: None
    SRP username: None
    TLS session ticket lifetime hint: 86400 (seconds)
    TLS session ticket:
    0000 - 78 4f 7b 3b 79 dd 2c 8b-11 b5 40 62 3f b6 27 ca   xO{;y.,...@b?.'.
    0010 - b6 bc 8e 42 6b 33 e4 cf-1f d8 d5 ad 53 21 57 05   ...Bk3......S!W.
    0020 - 75 1f af 3b ed f8 48 7e-8b 52 64 b8 04 35 a9 c2   u..;..H~.Rd..5..
    0030 - f7 1d dc 08 f3 c7 ad 96-f4 4c aa 77 5d 10 a9 96   .........L.w]...
    0040 - 9b 15 96 c0 e0 a2 54 6d-3f 70 56 6a 05 a1 78 64   ......Tm?pVj..xd
    0050 - a6 95 68 e2 83 09 a1 44-c3 c7 d6 33 8b 76 30 24   ..h....D...3.v0$
    0060 - ba ae f4 f6 d5 c3 c4 88-cc f3 02 0e e2 8b d2 a5   ................
    0070 - 08 7d 08 9f 94 34 18 3f-22 24 d5 62 92 48 93 61   .}...4.?"$.b.H.a
    0080 - 9e 95 86 1c 40 93 51 75-ba 06 ce ae 4f 38 10 ad   ....@.Qu....O8..
    0090 - 19 23 08 83 ce 1b 44 f7-b7 33 ce 8a 80 d5 d3 67   .#....D..3.....g

    Start Time: 1758653267
    Timeout   : 7200 (sec)
    Verify return code: 0 (ok)
    Extended master secret: no
    Max Early Data: 0
---
read R BLOCK
closed
```

Сертификат выдан на CN = www.python.org организацией GlobalSign Atlas R3 DV TLS CA 2025 Q1.
Цепочка сертификации:
www.python.org →
GlobalSign Atlas R3 DV TLS CA 2025 Q1 →
GlobalSign Root CA - R3.
Публичный ключ сервера: RSA 2048 бит.
Протокол соединения: TLS 1.3, шифр TLS_AES_128_GCM_SHA256.
Срок действия сертификата: 12 марта 2025 – 13 апреля 2026.
Проверка сертификата прошла успешно: Verify return code: 0 (ok).

### 4. Контрольные вопросы
1. Чем отличаются частные и публичные IP-адреса?
    - Частные IP — используются внутри локальной сети (например, дома или в офисе), их нельзя напрямую использовать в интернете. Примеры: 10.x.x.x, 192.168.x.x, 172.16–31.x.x.
    - Публичные IP — это адреса, видимые в интернете. Они уникальны и назначаются провайдером, чтобы устройства могли общаться с внешними серверами.
2. Для чего нужны порты и какие протоколы их используют?
- Порт нужен, чтобы на одном IP можно было одновременно запускать несколько сервисов (например, веб-сервер и почтовый сервер).

- TCP и UDP — основные протоколы, использующие порты. TCP для надёжных соединений (HTTP, HTTPS, SSH), UDP для быстрых, но ненадёжных (DNS, видеостриминг, игры).
3. Как работает DNS?
- DNS — это «телефонная книга» интернета. Когда ты вводишь домен (например, python.org), компьютер обращается к DNS-серверу, который переводит имя в IP-адрес, по которому реально находится сервер.
4. Как определить, открыт ли порт на удалённом хосте?

- Самый простой способ — использовать команду nc (netcat) или telnet. Например:
    ```bash
    nc -vz python.org 443
    ```
    Если порт открыт, соединение устанавливается; если закрыт — будет ошибка.

## 4. Вывод
В ходе лабораторной работы я освоил базовые команды для работы с сетью в Linux: проверку IP- и MAC-адресов, таблицы маршрутизации, доступность узлов с помощью ping, трассировку маршрута (traceroute), работу с портами (ss, nc) и анализ DNS-записей (dig).

Также научился получать HTTP-заголовки сайтов через curl и проверять SSL-сертификаты с помощью openssl. Практические задания показали, как определить открытые порты, маршруты до сервера и действительность сертификатов.

В результате я собрал полный сетевой отчёт по сайту python.org, что позволило на практике увидеть работу основных сетевых протоколов и механизмов безопасности.
