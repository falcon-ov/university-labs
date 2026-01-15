## Плейбук 1. «Статический сайт через Nginx + распаковка архива»

**Цель:** я поставил nginx, разложил свой мини-сайт из архива (`.tar.gz`) в web-директорию.  

**Что я сделал (3–4 шага):**

1. Я установил и запустил `nginx`.
   ![img](/images/img_1.png)
   ![img](/images/img_2.png)
2. Я создал каталог для сайта `/var/www/mysite`.
   ![img](/images/img_3.png)
3. Я распаковал архив сайта `files/site.tar.gz` в `/var/www/mysite` (модуль `unarchive`).
   ![img](/images/img_4.png)
4. Я положил минимальный nginx-vhost и активировал его (перезапуск по handler).
   ![img](/images/img_5.png)
5. Запустил и проверил работу сайта:
   ![img](/images/img_6.png)



---

## Плейбук 2. «Пользователь деплоя + SSH-ключ + sudoers drop-in»

**Цель:** я завел техпользователя без пароля, с входом по ключу и правами `sudo` через отдельный файл в `/etc/sudoers.d`.  

**Что я сделал:**

1. Я создал пользователя `deploy`, добавил в группу `sudo`.
   ![img](/images/img_7.png)
2. Я прописал публичный ключ в `~deploy/.ssh/authorized_keys` (`authorized_key`).
   ![img](/images/img_8.png)
   ![img](/images/img_9.png)
3. Я создал файл `/etc/sudoers.d/deploy` c правилом `deploy ALL=(ALL) NOPASSWD:ALL`.
   ![img](/images/img_10.png)
4. Я проверил синтаксис sudoers (через `command: visudo -cf …`), и только если ок — оставил файл.
   ![img](/images/img_11.png)